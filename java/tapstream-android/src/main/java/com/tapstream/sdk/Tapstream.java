package com.tapstream.sdk;

import android.app.Application;

import com.tapstream.sdk.timeline.TimelineApiResponse;
import com.tapstream.sdk.wordofmouth.WordOfMouth;

import java.io.IOException;

public class Tapstream implements AndroidApiClient {
	private static Tapstream instance;
	private WordOfMouth wom;
	private Platform platform;
	private ApiClient client;

	public interface ClientBuilder {
		ApiClient build(Application app, Config config);
	}

	private static ClientBuilder clientBuilder;

	public static class DefaultClientBuilder implements ClientBuilder {

		@Override
		public ApiClient build(Application app, Config config) {
			HttpApiClient client = new HttpApiClient(new AndroidPlatform(app), config);
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
			instance = new Tapstream(builder.build(app, config));
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
	}

	@Override
	public void close() throws IOException {
		instance.close();
	}

	@Override
	public void fireEvent(Event e) {
		client.fireEvent(e);
	}

	@Override
	public void lookupTimeline(Callback<TimelineApiResponse> completion) {
		client.lookupTimeline(completion);
	}

	@Override
	synchronized public WordOfMouth getWordOfMouth(){
//		if(wom == null) {
//			wom = WordOfMouthImpl.getInstance(client.getExecutor(), platform, secret, platform.getPackageName());
//		}
//		return wom;

		return null;
	}
}
