package com.tapstream.sdk;

import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class Core implements ExecutorProvider {
	public static final String VERSION = "2.10.0";

	private final Platform platform;
	private final ActivityEventSource activityEventSource;
	private final Config config;
	private final String accountName;
	private final String secret;
	private final ScheduledExecutorService executor;
	private final Set<String> firingEvents;
	private final Set<String> firedEvents;
	private AtomicBoolean retainEvents = new AtomicBoolean(true);
	private List<Event> retainedEvents = new ArrayList<Event>();
	private EventParams commonEventParams;
	private final String appName;

	Core(Platform platform, ActivityEventSource activityEventSource, String accountName, String developerSecret, Config config) {
		this.platform = platform;
		this.activityEventSource = activityEventSource;
		this.config = config;
		this.accountName = accountName;
		this.secret = developerSecret;
		this.firingEvents = new HashSet<String>();
		this.firedEvents = platform.loadFiredEvents();
		this.executor = Executors.newSingleThreadScheduledExecutor(new DeamonThreadFactory());

		String appName = platform.getAppName();
		if (appName == null) {
			appName = "";
		}
		this.appName = appName;
	}

	public void start() {
		// Automatically fire run event
		if(config.getFireAutomaticInstallEvent()) {
			String installEventName = config.getInstallEventName();
			if(installEventName != null) {
				fireEvent(new Event(installEventName, true));
			} else {
				fireEvent(new Event(String.format(Locale.US, "android-%s-install", appName), true));
			}
		}

		if(config.getFireAutomaticOpenEvent()) {
			String openEventName = config.getOpenEventName();
			if(openEventName != null) {
				fireEvent(new Event(openEventName, false));
			} else {
				fireEvent(new Event(String.format(Locale.US, "android-%s-open", appName), false));
			}
		}

		if (config.getFireAutomaticOpenEvent()){
			activityEventSource.setListener(new ActivityEventSource.ActivityListener() {
				@Override
				public void onOpen() {
					String openEventName = config.getOpenEventName();
					if(openEventName != null) {
						fireEvent(new Event(openEventName, false));
					} else {
						fireEvent(new Event(String.format(Locale.US, "android-%s-open", appName), false));
					}
				}
			});
		}

		executor.submit(new Runnable() {
			@Override
			public void run() {
				commonEventParams = buildCommonEventParams();
				dispatchRetainedEvents();
			}
		});
	}

	private EventParams buildCommonEventParams(){
		EventParams params = new EventParams();
		params.put("secret", secret);
		params.put("sdkversion", VERSION);
		params.put("hardware", config.getHardware());
		params.put("hardware-odin1", config.getOdin1());
		params.put("hardware-open-udid", config.getOpenUdid());
		params.put("hardware", config.getHardware());
		params.put("hardware-wifi-mac", config.getWifiMac());
		params.put("hardware-android-device-id", config.getDeviceId());
		params.put("hardware-android-android-id", config.getAndroidId());
		params.put("uuid", platform.loadUuid());
		params.put("platform", "Android");
		params.put("vendor", platform.getManufacturer());
		params.put("model", platform.getModel());
		params.put("os", platform.getOs());
		params.put("resolution", platform.getResolution());
		params.put("locale", platform.getLocale());
		params.put("app-name", platform.getAppName());
		params.put("app-version", platform.getAppVersion());
		params.put("package-name", platform.getPackageName());

		int offsetFromUtc = TimeZone.getDefault().getOffset((new Date()).getTime()) / 1000;
		params.put("gmtoffset", Integer.toString(offsetFromUtc));

		Callable<AdvertisingID> adIdFetcher = platform.getAdIdFetcher();
		AdvertisingID advertisingIdInfo = null;

		if (adIdFetcher != null && config.getCollectAdvertisingId()){
			try{
				advertisingIdInfo = adIdFetcher.call();
			} catch (Exception e){
				Logging.log(Logging.WARN, "Exception while getting the Advertising ID: " + e.getMessage());
			}

			if (advertisingIdInfo != null && advertisingIdInfo.isValid()){
				params.put("hardware-android-advertising-id", advertisingIdInfo.getId());
				params.put("android-limit-ad-tracking", Boolean.toString(advertisingIdInfo.isLimitAdTracking()));
			} else {
				Logging.log(Logging.WARN, "Advertising ID could not be collected. Is Google Play Services installed?");
			}
		}

		String referrer = platform.getReferrer();
		if(referrer != null && referrer.length() > 0) {
			params.put("android-referrer", referrer);
		}

		return params;
	}

	private synchronized void dispatchRetainedEvents(){
		retainEvents.set(false);

		for(Event e: retainedEvents) {
			fireEvent(e);
		}

		retainedEvents = null;
	}


	public synchronized void fireEvent(final Event e) {
		// If we are retaining events, add them to a list to be sent later
		if(retainEvents.get()) {
			retainedEvents.add(e);
			return;
		}

		e.prepare(appName);

		if (e.isOneTimeOnly()) {
			if (firedEvents.contains(e.getName())) {
				Logging.log(Logging.INFO, "Tapstream ignoring event named \"%s\" because it is a " +
						"one-time-only event that has already been fired", e.getName());
				return;
			} else if (firingEvents.contains(e.getName())) {
				Logging.log(Logging.INFO, "Tapstream ignoring event named \"%s\" because it is a " +
						"one-time-only event that is already in progress", e.getName());
				return;
			}

			firingEvents.add(e.getName());
		}

		final Core self = this;

		final HttpRequest eventRequest;

		try{
			eventRequest = RequestBuilders
					.eventRequestBuilder(accountName, e.getName())
					.postBody(e.buildPostBody(commonEventParams, config.globalEventParams))
					.build();
		} catch (MalformedURLException error){
			// log the error
			Logging.log(Logging.ERROR, "Tapstream failed to build the request URL: " + error.getMessage());
			return;
		}

		fireEvent(e, eventRequest.makeRetryable(config.getEventRetryStrategy()));
	}

	private void fireEvent(final Event event, final Retry.Retryable<HttpRequest> retryableRequest){
		final Core self = this;

		Runnable task = new Runnable() {
			public void innerRun() throws IOException {
				HttpResponse response = platform.sendRequest(retryableRequest.get());

				if (event.isOneTimeOnly()) {
					synchronized (self) {
						firingEvents.remove(event.getName());

						if (response.succeeded()){
							firedEvents.add(event.getName());
							platform.saveFiredEvents(self.firedEvents);
						}
					}
				}

				if (response.succeeded()){
					Logging.log(Logging.INFO, "Tapstream fired event named \"%s\"", event.getName());
				} else {
					if (response.status == 404) {
						Logging.log(Logging.ERROR, "Tapstream Error: Failed to fire event, http code %d\n" +
								"Does your event name contain characters that are not url safe? This " +
								"event will not be retried.", response.status);
					} else if (response.status == 403) {
						Logging.log(Logging.ERROR, "Tapstream Error: Failed to fire event, http code %d\n" +
								"Are your account name and application secret correct?  This event " +
								"will not be retried.", response.status);
					} else {
						String retryMsg = "";
						if (!response.shouldRetry()) {
							retryMsg = "  This event will not be retried.";
						}
						Logging.log(Logging.ERROR, "Tapstream Error: Failed to fire event, http code %d.%s",
								response.status, retryMsg);
					}

					if (response.shouldRetry() && retryableRequest.shouldRetry()) {
						retryableRequest.incrementAttempt();
						fireEvent(event, retryableRequest);
					}
				}
			}
			public void run() {
				try {
					innerRun();
				} catch(Exception ex) {
					Logging.log(Logging.ERROR, "Tapstream Error: Unhandled exception while firing event: " + ex.getMessage());
				}
			}
		};

		executor.schedule(task, retryableRequest.getDelay(), TimeUnit.MILLISECONDS);
	}

	public void getConversionData(final Callback<JSONObject> callback)
	{
		final Retry.Retryable<HttpRequest> retryable;
		try {
			retryable = RequestBuilders.timelineLookupRequestBuilder(secret, platform.loadUuid())
					.build()
					.makeRetryable(config.getTimelineLookupRetryStrategy());
		} catch (MalformedURLException e) {
			Logging.log(Logging.ERROR, "Tapstream Error: Failed to build timeline lookup URL");
			return;
		}

		Runnable task = new Runnable() {
			public void checkedRun() throws IOException{
				HttpResponse res = platform.sendRequest(retryable.get());
				if(res.status >= 200 && res.status < 300) {

					// Check for the legacy "timeline not found" pattern
					String responseBody = res.getBodyAsString();
					Object jsonRoot = new JSONTokener(responseBody).nextValue();

					if (jsonRoot instanceof JSONArray){
						// The legacy empty timeline object is an empty array
						JSONArray jsonRootArray = (JSONArray)jsonRoot;

						if (jsonRootArray.length() == 0){
							// We found the empty array. Poll again if the retry strategy allows it.
							if (retryable.shouldRetry()){
								retryable.incrementAttempt();
								executor.schedule(this, retryable.getDelay(), TimeUnit.MILLISECONDS);
							} else {
								callback.error(new TimelineLookupFailed("Lookup attempts exhausted"));
							}
						} else {
							callback.error(new TimelineLookupFailed("Unknown response structure"));
						}

					} else if (jsonRoot instanceof JSONObject){
						callback.success((JSONObject)jsonRoot);
					} else {
						callback.error(new TimelineLookupFailed("Unknown response structure"));
					}
				}
			}

			@Override
			public void run() {
				try {
					checkedRun();
				} catch (Exception e){
					Logging.log(Logging.ERROR, "Unhandled exception during timeline lookup");
					callback.error(e);
				}
			}
		};

		executor.submit(task);


	}

	@Override
	public <T> Future<T> submit(Callable<T> task, int time, TimeUnit unit) {
		return executor.schedule(task, time, unit);
	}
}
