package com.tapstream.sdk;

import java.util.Set;
import java.util.concurrent.ThreadFactory;

interface Platform {
	public ThreadFactory makeWorkerThreadFactory();

	public String loadUuid();

	public Set<String> loadFiredEvents();

	public void saveFiredEvents(Set<String> firedEvents);

	public String getResolution();

	public String getManufacturer();

	public String getModel();

	public String getOs();

	public String getLocale();

	public String getWifiMac();

	public String getDeviceId();

	public String getAndroidId();

	public String getAppName();

	public String getPackageName();

	public Response request(String url, String data);
}
