package com.tapstream.sdk;

import java.lang.reflect.Method;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AdvertisingIdFetcher implements Runnable {
	private static final String UUID_KEY = "TapstreamSDKUUID";
	private Application app;
	
	public AdvertisingIdFetcher(Application app) {
		super();
		this.app = app;
	}

	@Override
	public void run() {
		try {
			Class<?> clientCls = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
			Class<?> infoCls = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient$Info");
			Method getAdvertisingIdInfo = clientCls.getMethod("getAdvertisingIdInfo", Context.class);
			Object info = getAdvertisingIdInfo.invoke(clientCls, this.app);
			Method getId = infoCls.getMethod("getId"); 
			String id = (String) getId.invoke(info);
			Method isLimitAdTrackingEnabled = infoCls.getMethod("isLimitAdTrackingEnabled"); 
			boolean limitAdTracking = (Boolean) isLimitAdTrackingEnabled.invoke(info);
			
			// Stash in the preferences
			SharedPreferences prefs = this.app.getSharedPreferences(UUID_KEY, 0);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("advertisingId", id);
			editor.putBoolean("limitAdTracking", limitAdTracking);
			editor.commit();
		} catch(Exception e) {}
	}
}
