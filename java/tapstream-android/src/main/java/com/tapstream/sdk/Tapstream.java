package com.tapstream.sdk;

import android.app.Application;

import com.tapstream.sdk.wordofmouth.OfferApiResponse;
import com.tapstream.sdk.wordofmouth.RewardApiResponse;
import com.tapstream.sdk.wordofmouth.WordOfMouth;
import com.tapstream.sdk.wordofmouth.WordOfMouthImpl;

import java.io.IOException;

public class Tapstream implements AndroidApiClient {
	private static Tapstream instance;
	private WordOfMouth wom;
	private ApiClient client;

	public interface ClientBuilder {
		ApiClient build(Platform platform, Config config);
	}

	private static ClientBuilder clientBuilder;

	public static class DefaultClientBuilder implements ClientBuilder {
		@Override
		public ApiClient build(Platform platform, Config config) {
			HttpApiClient client = new HttpApiClient(platform, config);
			client.start();
			return client;
		}
	}

	synchronized public static void setClientBuilder(ClientBuilder clientBuilder){
		Tapstream.clientBuilder = clientBuilder;
	}

	synchronized public static void create(Application app, Config config) {
		if (instance == null) {
			ClientBuilder builder = clientBuilder == null ? new DefaultClientBuilder() : clientBuilder;
			final Platform platform = new AndroidPlatform(app);
			if(config.getUseWordOfMouth()) {
				instance = new Tapstream(builder.build(platform, config));
			}else{
				instance = new Tapstream(builder.build(platform, config), WordOfMouthImpl.getInstance(platform));
			}
		} else {
			Logging.log(Logging.WARN, "Tapstream Warning: Tapstream already instantiated, it cannot be re-created.");
		}
	}

	synchronized public static Tapstream getInstance() {
		if (instance == null) {
			throw new RuntimeException("You must first call Tapstream.create");
		}
		return instance;
	}

	Tapstream(ApiClient client){
		this.client = client;
		this.wom = null;
	}

	Tapstream(ApiClient client, WordOfMouth wom){
		this.client = client;
		this.wom = wom;
	}

	@Override
	public void close() throws IOException {
		instance.close();
	}

	@Override
	public ApiFuture<EventApiResponse> fireEvent(Event e) {
		return client.fireEvent(e);
	}

	@Override
	public ApiFuture<TimelineApiResponse> lookupTimeline() {
		return client.lookupTimeline();
	}

	@Override
	public ApiFuture<OfferApiResponse> getWordOfMouthOffer(String insertionPoint) {
		return client.getWordOfMouthOffer(insertionPoint);
	}

	@Override
	public ApiFuture<RewardApiResponse> getWordOfMouthRewardList() {
		return client.getWordOfMouthRewardList();
	}

	@Override
	public WordOfMouth getWordOfMouth(){
		return wom;
	}
}
