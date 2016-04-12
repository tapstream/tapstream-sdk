package com.tapstream.sdk;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class AdvertisingIdFetcher implements Callable<AdvertisingID> {
	private final Application app;

	public AdvertisingIdFetcher(Application app) {
		super();
		this.app = app;
	}

	@Override
	public AdvertisingID call() throws Exception{
		Class<?> clientCls = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
		Class<?> infoCls = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient$Info");
		Method getAdvertisingIdInfo = clientCls.getMethod("getAdvertisingIdInfo", Context.class);
		Object info = getAdvertisingIdInfo.invoke(clientCls, this.app);
		Method getId = infoCls.getMethod("getId");
		String id = (String) getId.invoke(info);
		Method isLimitAdTrackingEnabled = infoCls.getMethod("isLimitAdTrackingEnabled");
		boolean limitAdTracking = (Boolean) isLimitAdTrackingEnabled.invoke(info);
		return new AdvertisingID(id, limitAdTracking);
	}
}
