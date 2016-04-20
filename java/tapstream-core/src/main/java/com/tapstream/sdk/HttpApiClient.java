package com.tapstream.sdk;

import com.tapstream.sdk.errors.ApiException;
import com.tapstream.sdk.errors.EventAlreadyFiredException;
import com.tapstream.sdk.errors.RecoverableApiException;
import com.tapstream.sdk.http.HttpClient;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;
import com.tapstream.sdk.http.RequestBuilders;
import com.tapstream.sdk.http.StdLibHttpClient;
import com.tapstream.sdk.wordofmouth.Offer;
import com.tapstream.sdk.wordofmouth.Reward;

import org.json.JSONArray;
import org.json.JSONObject;

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
	private List<QueuedEvent> queuedEvents = new ArrayList<QueuedEvent>();

	private Event.Params commonEventParams;


	HttpApiClient(Platform platform, Config config){
		this(platform, config, new StdLibHttpClient(), Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory()));
	}

	HttpApiClient(Platform platform, Config config, HttpClient client, ScheduledExecutorService executor) {
		this.platform = platform;
		this.config = config;
		this.client = client;
		this.executor = executor;
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

		if (!started.compareAndSet(false, true)) {
			return;
		}

		final String appName = Utils.getOrDefault(platform.getAppName(), "");

		if(config.getFireAutomaticInstallEvent()) {
			String installEventName = config.getInstallEventName();
			if(installEventName != null) {
				fireEvent(new Event(installEventName, true));
			} else {
				fireEvent(new Event(String.format(Locale.US, "android-%s-install", appName), true));
			}
		}

		if(config.getFireAutomaticOpenEvent()) {
			ActivityEventSource eventSource = platform.getActivityEventSource();
			if (eventSource != null){
				eventSource.setListener(new ActivityEventSource.ActivityListener() {
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

		for(QueuedEvent e: queuedEvents) {
			prepareAndSendEvent(e.getEvent(), e.getResponseFuture());
		}

		queuedEvents = null;
	}


	@Override
	public ApiFuture<EventApiResponse> fireEvent(final Event event) {
		ApiFuture<EventApiResponse> responseFuture = new ApiFuture<EventApiResponse>();
		try {
			prepareAndSendEvent(event, responseFuture);
		} catch (Exception e){
			responseFuture.setException(e);
		}
		return responseFuture;
	}

	synchronized private void prepareAndSendEvent(final Event event, final ApiFuture<EventApiResponse> responseFuture){
		try {
			if (queueEvents) {
				queuedEvents.add(new QueuedEvent(event, responseFuture));
				return;
			}

			event.prepare(Utils.getOrDefault(platform.getAppName(), ""));

			if (event.isOneTimeOnly()) {
				if (oneTimeEventTracker.hasBeenAlreadySent(event)) {
					Logging.log(Logging.INFO, "Tapstream ignoring event named \"%s\" because it is a " +
							"one-time-only event that has already been fired", event.getName());
					responseFuture.setException(new EventAlreadyFiredException());
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
				responseFuture.setException(new ApiException(error));
				return;
			}

			sendEventRequest(event, responseFuture, eventRequest);

		} catch (Exception e){
			responseFuture.setException(e);
		}
	}

	private void sendEventRequest(final Event event, ApiFuture<EventApiResponse> responseFuture, Retry.Retryable<HttpRequest> retryableRequest){

		CheckedApiRunnable<EventApiResponse> task = new CheckedApiRunnable<EventApiResponse>() {
			@Override
			public void onFailure() {
				oneTimeEventTracker.failed(event);
			}

			@Override
			public EventApiResponse checkedRun(HttpResponse response) throws IOException, ApiException {
				Logging.log(Logging.INFO, "Fired event named \"%s\"", event.getName());
				oneTimeEventTracker.sent(event);
				return new EventApiResponse(response);
			}
		};
        ApiRunnable.createAndStart(responseFuture, retryableRequest, task, executor, client);
	}

	@Override
	public ApiFuture<TimelineApiResponse> lookupTimeline()
	{
		final ApiFuture<TimelineApiResponse> responseFuture = new ApiFuture<TimelineApiResponse>();

		try {
			final Retry.Retryable<HttpRequest> retryable = RequestBuilders
					.timelineLookupRequestBuilder(config.getDeveloperSecret(), platform.loadUuid())
					.build()
					.makeRetryable(config.getTimelineLookupRetryStrategy());


            CheckedApiRunnable<TimelineApiResponse> task = new CheckedApiRunnable<TimelineApiResponse>() {
                @Override
                public TimelineApiResponse checkedRun(HttpResponse resp) throws IOException, ApiException {
                    TimelineApiResponse apiResponse = new TimelineApiResponse(resp);
                    if (apiResponse.isEmpty()){
                        throw new RecoverableApiException(resp);
                    }

                    return apiResponse;
                }
            };

            ApiRunnable.createAndStart(responseFuture, retryable, task, executor, client);

		} catch (Exception e){
			responseFuture.setException(e);
		}

		return responseFuture;
	}

	@Override
	public  ApiFuture<Offer> getWordOfMouthOffer(final String insertionPoint) {
		final ApiFuture<Offer> responseFuture = new ApiFuture<Offer>();

		try{
			final String bundle = platform.getPackageName();

			final Retry.Retryable<HttpRequest> retryable = RequestBuilders
					.wordOfMouthOfferRequestBuilder(config.getDeveloperSecret(), insertionPoint, bundle)
					.build()
					.makeRetryable(config.getTimelineLookupRetryStrategy());

			CheckedApiRunnable<Offer> offerRequest = new CheckedApiRunnable<Offer>() {
				@Override
				public Offer checkedRun(HttpResponse resp) throws IOException, ApiException {
					JSONObject responseObject = new JSONObject(resp.getBodyAsString());
					Offer offer = Offer.fromApiResponse(responseObject);
					return offer;
				}
			};

			ApiRunnable.createAndStart(responseFuture, retryable, offerRequest, executor, client);

		} catch (Exception e){
			responseFuture.setException(e);
		}

		return responseFuture;

	}


	@Override
	public ApiFuture<List<Reward>> getWordOfMouthRewardList() {
		final ApiFuture<List<Reward>> responseFuture = new ApiFuture<List<Reward>>();

		try {
			final Retry.Retryable<HttpRequest> retryable = RequestBuilders
					.wordOfMouthRewardRequestBuilder(config.getDeveloperSecret(), platform.loadUuid())
					.build()
					.makeRetryable(config.getTimelineLookupRetryStrategy());


			CheckedApiRunnable<List<Reward>> getRewards = new CheckedApiRunnable<List<Reward>>() {
				public List<Reward> checkedRun(HttpResponse resp) throws IOException, ApiException {
                    JSONArray responseObject = new JSONArray(resp.getBodyAsString());
                    List<Reward> result = new ArrayList<Reward>(responseObject.length());

                    for (int ii = 0; ii < responseObject.length(); ii++) {
                        Reward reward = Reward.fromApiResponse(responseObject.getJSONObject(ii));
                        if (!reward.isConsumed(platform)) {
                            result.add(reward);
                        }
                    }
                    return result;
				}
			};

			ApiRunnable.createAndStart(responseFuture, retryable, getRewards, executor, client);
		} catch (Exception e){
			responseFuture.setException(e);
		}

		return responseFuture;

	}

}
