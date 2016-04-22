package com.tapstream.sdk;

import com.tapstream.sdk.wordofmouth.Reward;

import java.util.Set;
import java.util.concurrent.Callable;

public interface Platform {
	String loadSessionId();

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
	
	String getReferrer();

	Integer getCountForReward(Reward reward);

	void consumeReward(Reward reward);

	Callable<AdvertisingID> getAdIdFetcher();

	ActivityEventSource getActivityEventSource();
}
