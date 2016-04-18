package com.tapstream.sdk;

import com.tapstream.sdk.http.HttpClient;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;
import com.tapstream.sdk.http.RequestBuilders;
import com.tapstream.sdk.http.StdLibHttpClient;
import com.tapstream.sdk.timeline.TimelineApiResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class HttpApiClient implements ApiClient {
	public static final String VERSION = "3.0.0";

	private final Platform platform;
	private final Config config;
	private final ScheduledExecutorService executor;
	private final AtomicBoolean started = new AtomicBoolean(false);
	private final HttpClient client;
	private final OneTimeOnlyEventTracker oneTimeEventTracker;

	private boolean queueEvents = true;
	private List<Event> queuedEvents = new ArrayList<Event>();

	private Event.Params commonEventParams;
	private final String appName;


	HttpApiClient(Platform platform, Config config){
		this(platform, config, new StdLibHttpClient(), Executors.newSingleThreadScheduledExecutor(new DeamonThreadFactory()));
	}

	HttpApiClient(Platform platform, Config config, HttpClient client, ScheduledExecutorService executor) {
		this.platform = platform;
		this.config = config;
		this.client = client;
		this.executor = executor;

		String appName = platform.getAppName();
		if (appName == null) {
			appName = "";
		}
		this.appName = appName;
		this.oneTimeEventTracker = new OneTimeOnlyEventTracker(platform);
	}


	@Override
	public void close() throws IOException {
		Utils.closeQuietly(client);

		executor.shutdownNow();
		try{
			executor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (Exception e){
			Logging.log(Logging.WARN, "Failed to shutdown executor");
		}
	}


	public void start() {
		if (!started.compareAndSet(false, true))
			return;

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
			platform.getActivityEventSource().setListener(new ActivityEventSource.ActivityListener() {
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
				dispatchQueuedEvents();
			}
		});
	}

	/**
	 * Builds the event parameters that will be included with all events sent from this device.
	 * This method must not be called in the main thread.
	 * @return the event params.
     */
	Event.Params buildCommonEventParams(){
		Event.Params params = new Event.Params();
		params.put("secret", config.getDeveloperSecret());
		params.put("sdkversion", VERSION);
		params.put("hardware-odin1", config.getOdin1());
		params.put("hardware-open-udid", config.getOpenUdid());
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

		if (adIdFetcher != null && config.getCollectAdvertisingId()){
			AdvertisingID advertisingIdInfo = null;
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

	private synchronized void dispatchQueuedEvents(){
		queueEvents = false;

		for(Event e: queuedEvents) {
			fireEvent(e);
		}

		queuedEvents = null;
	}


	@Override
	public synchronized void fireEvent(final Event event) {
		if (queueEvents) {
			queuedEvents.add(event);
			return;
		}

		event.prepare(appName);

		if (event.isOneTimeOnly()) {
			if (oneTimeEventTracker.hasBeenAlreadySent(event)) {
				Logging.log(Logging.INFO, "Tapstream ignoring event named \"%s\" because it is a " +
						"one-time-only event that has already been fired", event.getName());
				return;
			}
			oneTimeEventTracker.inProgress(event);
		}

		final Retry.Retryable<HttpRequest> eventRequest;

		try{
			eventRequest = RequestBuilders
					.eventRequestBuilder(config.getAccountName(), event.getName())
					.postBody(event.buildPostBody(commonEventParams, config.getGlobalEventParams()))
					.build()
					.makeRetryable(config.getEventRetryStrategy());
		} catch (MalformedURLException error){
			// log the error
			Logging.log(Logging.ERROR, "Tapstream failed to build the request URL: " + error.getMessage());
			return;
		}

		fireEvent(event, eventRequest);
	}

	private void fireEvent(final Event event, final Retry.Retryable<HttpRequest> retryableRequest){
		Runnable task = new Runnable() {
			public void innerRun() throws IOException {
				HttpResponse response = client.sendRequest(retryableRequest.get());

				if (response.succeeded()){
					Logging.log(Logging.INFO, "Tapstream fired event named \"%s\"", event.getName());
					oneTimeEventTracker.sent(event);
				} else {
					if (response.status == 404) {
						Logging.log(Logging.ERROR, "Tapstream Error: Failed to fire event, http code %d\n" +
								"Does your event name contain characters that are not url safe? This " +
								"event will not be retried.", response.status);
					} else if (response.status == 403) {
						Logging.log(Logging.ERROR, "Tapstream Error: Failed to fire event, http code %d\n" +
								"Are your account name and application secret correct?  This event " +
								"will not be retried.", response.status);
					} else if (response.shouldRetry() && retryableRequest.shouldRetry()) {
						retryableRequest.incrementAttempt();
						fireEvent(event, retryableRequest);
					} else {
						// Give up
						String retryMsg = "";
						if (!response.shouldRetry()) {
							retryMsg = "  This event will not be retried.";
						}
						Logging.log(Logging.ERROR, "Tapstream Error: Failed to fire event, http code %d.%s",
								response.status, retryMsg);

						oneTimeEventTracker.failed(event);
					}
				}
			}
			public void run() {
				try {
					innerRun();
				} catch(Exception ex) {
					Logging.log(Logging.ERROR, "Tapstream Error: Unhandled exception while firing event: " + ex.getMessage());
					oneTimeEventTracker.failed(event);
				}
			}
		};

		executor.schedule(task, retryableRequest.getDelayMs(), TimeUnit.MILLISECONDS);
	}

	@Override
	public void lookupTimeline(final Callback<TimelineApiResponse> callback)
	{
		final Retry.Retryable<HttpRequest> retryable;
		try {
			retryable = RequestBuilders
					.timelineLookupRequestBuilder(config.getDeveloperSecret(), platform.loadUuid())
					.build()
					.makeRetryable(config.getTimelineLookupRetryStrategy());
		} catch (MalformedURLException e) {
			Logging.log(Logging.ERROR, "Tapstream Error: Failed to build timeline lookup URL");
			return;
		}

		Runnable task = new Runnable() {
			public void checkedRun() throws IOException{
				HttpResponse httpResponse = client.sendRequest(retryable.get());
				if(httpResponse.succeeded()) {

					// Check for the legacy "timeline not found" pattern
					TimelineApiResponse apiResponse = new TimelineApiResponse(httpResponse.getBodyAsString());

					if (apiResponse.isEmpty()){
						// We found the empty array. Poll again if the retry strategy allows it.
						if (retryable.shouldRetry()){
							retryable.incrementAttempt();
							executor.schedule(this, retryable.getDelayMs(), TimeUnit.MILLISECONDS);
						} else {
							callback.error(new com.tapstream.sdk.timeline.TimelineLookupFailed("Lookup attempts exhausted"));
						}
					} else {
						callback.success(apiResponse);
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

	ScheduledExecutorService getExecutor(){
		return executor;
	}
}
