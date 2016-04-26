package com.tapstream.sdk;

import com.tapstream.sdk.errors.ApiException;
import com.tapstream.sdk.errors.EventAlreadyFiredException;
import com.tapstream.sdk.http.AsyncHttpClient;
import com.tapstream.sdk.http.AsyncHttpRequest;
import com.tapstream.sdk.http.HttpClient;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;
import com.tapstream.sdk.http.RequestBuilders;
import com.tapstream.sdk.http.StdLibHttpClient;
import com.tapstream.sdk.wordofmouth.Offer;
import com.tapstream.sdk.wordofmouth.OfferApiResponse;
import com.tapstream.sdk.wordofmouth.Reward;
import com.tapstream.sdk.wordofmouth.RewardApiResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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

public class HttpApiClient implements ApiClient {
	public static final String VERSION = "3.0.0";

	private final Platform platform;
	private final Config config;
	private final ScheduledExecutorService executor;
	private final AtomicBoolean started = new AtomicBoolean(false);
	private final AsyncHttpClient asyncClient;
	private final OneTimeOnlyEventTracker oneTimeEventTracker;

	private boolean queueRequests = true;
	private List<Runnable> queuedRequests = new ArrayList<Runnable>();
	private Event.Params commonEventParams;


	public HttpApiClient(Platform platform, Config config){
		this(platform, config, new StdLibHttpClient(), Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory()));
	}

	public HttpApiClient(Platform platform, Config config, HttpClient client, ScheduledExecutorService executor) {
		this.platform = platform;
		this.config = config;
		this.executor = executor;
		this.asyncClient = new AsyncHttpClient(client, executor);
		this.oneTimeEventTracker = new OneTimeOnlyEventTracker(platform);
	}


	/**
	 * Visible for testing only.
	 * @return the common event params.
     */
	Event.Params getCommonEventParams(){
		return commonEventParams;
	}


	@Override
	public void close() throws IOException {
		Utils.closeQuietly(asyncClient);

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
			String installEventName = config.getInstallEventName() == null
					? String.format(Locale.US, "android-%s-install", appName)
					: config.getInstallEventName();

			fireEvent(new Event(installEventName, true));
		}

		if(config.getFireAutomaticOpenEvent()) {
			final String openEventName = config.getOpenEventName() == null
					? String.format(Locale.US, "android-%s-open", appName)
					: config.getOpenEventName();

			ActivityEventSource eventSource = platform.getActivityEventSource();
			if (eventSource == null) {
				fireEvent(new Event(openEventName, false));
			} else {
				eventSource.setListener(new ActivityEventSource.ActivityListener() {
					@Override
					public void onOpen() {
						fireEvent(new Event(openEventName, false));
					}
				});
			}
		}

		executor.submit(new Runnable() {
			@Override
			public void run() {
				commonEventParams = buildCommonEventParams();
				dispatchQueuedRequests();
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
		params.put("uuid", platform.loadSessionId());
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
				Logging.log(Logging.WARN, "Advertising ID could not be collected. Is Google Play Services installed?");
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

	private synchronized void dispatchQueuedRequests(){
		queueRequests = false;

		for(Runnable r: queuedRequests) {
			executor.submit(r);
		}

		queuedRequests = null;
	}


	@Override
	public ApiFuture<EventApiResponse> fireEvent(final Event event) {
		SettableApiFuture<EventApiResponse> responseFuture = new SettableApiFuture<EventApiResponse>();
		try {
			fireEvent(event, responseFuture);
		} catch (Exception e){
			responseFuture.setException(e);
		}
		return responseFuture;
	}

	private void fireEvent(final Event event, final SettableApiFuture<EventApiResponse> responseFuture){
		try {
			synchronized (this){
				if (queueRequests) {
					queuedRequests.add(new Runnable() {

						@Override
						public void run() {
							fireEvent(event, responseFuture);
						}
					});
					return;
				}
			}

			event.prepare(Utils.getOrDefault(platform.getAppName(), ""));

			if (event.isOneTimeOnly()) {
				if (oneTimeEventTracker.hasBeenAlreadySent(event)) {
					Logging.log(Logging.INFO, "Ignoring event named \"%s\" because it is a " +
							"one-time-only event that has already been fired", event.getName());
					responseFuture.setException(new EventAlreadyFiredException());
					return;
				}
				oneTimeEventTracker.inProgress(event);
			}

			final HttpRequest eventRequest = RequestBuilders
					.eventRequestBuilder(config.getAccountName(), event.getName())
					.postBody(event.buildPostBody(commonEventParams, config.getGlobalEventParams()))
					.build();


			AsyncHttpRequest.Handler<EventApiResponse> responseHandler = new AsyncHttpRequest.Handler<EventApiResponse>() {
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

			asyncClient.sendRequest(eventRequest, config.getDataCollectionRetryStrategy(), responseHandler, responseFuture);


		} catch (Exception e){
			responseFuture.setException(e);
		}
	}

	@Override
	public ApiFuture<TimelineApiResponse> lookupTimeline() {
		final SettableApiFuture<TimelineApiResponse> responseFuture = new SettableApiFuture<TimelineApiResponse>();
		try {
			lookupTimeline(responseFuture);
		} catch (Exception e){
			responseFuture.setException(e);
		}
		return responseFuture;
	}

	private void lookupTimeline(final SettableApiFuture<TimelineApiResponse> responseFuture) {
		try {
			synchronized (this){
				if (queueRequests) {
					queuedRequests.add(new Runnable() {

						@Override
						public void run() {
							lookupTimeline(responseFuture);
						}
					});
					return;
				}
			}

			final HttpRequest request = RequestBuilders
					.timelineLookupRequestBuilder(config.getDeveloperSecret(), platform.loadSessionId())
					.build();

			AsyncHttpRequest.Handler<TimelineApiResponse> responseHandler = new AsyncHttpRequest.Handler<TimelineApiResponse>() {
				@Override
				public TimelineApiResponse checkedRun(HttpResponse resp) throws IOException, ApiException {
					return new TimelineApiResponse(resp);
				}
			};

			asyncClient.sendRequest(request, config.getUserFacingRequestRetryStrategy(), responseHandler, responseFuture);

		} catch (Exception e) {
			responseFuture.setException(e);
			return;
		}

	}

	@Override
	public ApiFuture<OfferApiResponse> getWordOfMouthOffer(final String insertionPoint) {
		final SettableApiFuture<OfferApiResponse> responseFuture = new SettableApiFuture<OfferApiResponse>();

		try{
			final String bundle = platform.getPackageName();

			final HttpRequest request = RequestBuilders
					.wordOfMouthOfferRequestBuilder(config.getDeveloperSecret(), insertionPoint, bundle)
					.build();

			AsyncHttpRequest.Handler<OfferApiResponse> handler = new AsyncHttpRequest.Handler<OfferApiResponse>() {
				@Override
				public OfferApiResponse checkedRun(HttpResponse resp) throws IOException, ApiException {
					JSONObject responseObject = new JSONObject(resp.getBodyAsString());
					Offer offer = Offer.fromApiResponse(responseObject);
					return new OfferApiResponse(resp, offer);
				}
			};

			asyncClient.sendRequest(request, config.getUserFacingRequestRetryStrategy(), handler, responseFuture);

		} catch (Exception e){
			responseFuture.setException(e);
		}

		return responseFuture;

	}


	@Override
	public ApiFuture<RewardApiResponse> getWordOfMouthRewardList() {
		final SettableApiFuture<RewardApiResponse> responseFuture = new SettableApiFuture<RewardApiResponse>();

		try {
			final HttpRequest request = RequestBuilders
					.wordOfMouthRewardRequestBuilder(config.getDeveloperSecret(), platform.loadSessionId())
					.build();

			AsyncHttpRequest.Handler<RewardApiResponse> handler = new AsyncHttpRequest.Handler<RewardApiResponse>() {
				@Override
				public RewardApiResponse checkedRun(HttpResponse resp) throws IOException, ApiException {

					JSONArray responseObject = new JSONArray(resp.getBodyAsString());
					List<Reward> rewards = new ArrayList<Reward>(responseObject.length());

					for (int ii = 0; ii < responseObject.length(); ii++) {
						Reward reward = Reward.fromApiResponse(responseObject.getJSONObject(ii));
						if (!reward.isConsumed(platform)) {
							rewards.add(reward);
						}
					}
					return new RewardApiResponse(resp, rewards);
				}
			};

			asyncClient.sendRequest(request, config.getUserFacingRequestRetryStrategy(), handler, responseFuture);

		} catch (Exception e){
			responseFuture.setException(e);
		}

		return responseFuture;

	}

}
