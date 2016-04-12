package com.tapstream.sdk;

import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;
import com.tapstream.sdk.wordofmouth.Reward;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;

public interface Platform {
	String loadUuid();

	Set<String> loadFiredEvents();

	void saveFiredEvents(Set<String> firedEvents);

	String getResolution();

	String getManufacturer();

	String getModel();

	String getOs();

	String getLocale();

	String getAppName();
	
	String getAppVersion();

	String getPackageName();

	HttpResponse sendRequest(HttpRequest request) throws IOException;
	
	String getReferrer();

	Integer getCountForReward(Reward reward);

	void consumeReward(Reward reward);

	Callable<AdvertisingID> getAdIdFetcher();
}
