package com.tapstream.sdk;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.tapstream.sdk.wordofmouth.Reward;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;


class AndroidPlatform implements Platform {
	private static final String FIRED_EVENTS_KEY = "TapstreamSDKFiredEvents";
	private static final String UUID_KEY = "TapstreamSDKUUID";
	private static final String WOM_REWARDS_KEY = "TapstreamWOMRewards";

	private final Application app;

	public AndroidPlatform(Application app) {
		this.app = app;
	}

	@Override
	public String loadUuid() {
		SharedPreferences prefs = app.getApplicationContext().getSharedPreferences(UUID_KEY, 0);
		String uuid = prefs.getString("uuid", null);
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("uuid", uuid);
			editor.commit();
		}
		return uuid;
	}

	@Override
	public Set<String> loadFiredEvents() {
		SharedPreferences settings = app.getApplicationContext().getSharedPreferences(FIRED_EVENTS_KEY, 0);
		Map<String, ?> fired = settings.getAll();
		return new HashSet<String>(fired.keySet());
	}

	@Override
	public void saveFiredEvents(Set<String> firedEvents) {
		SharedPreferences settings = app.getApplicationContext().getSharedPreferences(FIRED_EVENTS_KEY, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		for (String name : firedEvents) {
			editor.putString(name, "");
		}
		editor.commit();
	}

	@Override
	public String getResolution() {
		WindowManager wm = (WindowManager) this.app.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return String.format(Locale.US, "%dx%d", metrics.widthPixels, metrics.heightPixels);
	}

	@Override
	public String getManufacturer() {
		try {
			return Build.MANUFACTURER;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getModel() {
		return Build.MODEL;
	}

	@Override
	public String getOs() {
		return String.format(Locale.US, "Android %s", Build.VERSION.RELEASE);
	}

	@Override
	public String getLocale() {
		return Locale.getDefault().toString();
	}

	@Override
	public String getAppName() {
		PackageManager pm = app.getPackageManager();
		String appName;
		try {
			ApplicationInfo ai = pm.getApplicationInfo(app.getPackageName(), 0);
			CharSequence cs = pm.getApplicationLabel(ai);
			appName = cs.toString();
		} catch (NameNotFoundException e) {
			appName = app.getPackageName();
		}
		return appName;
	}

	@Override
	public String getAppVersion() {
		PackageManager pm = app.getPackageManager();
		try {
			return pm.getPackageInfo(app.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	@Override
	public String getPackageName() {
		return app.getPackageName();
	}


	@Override
	public String getReferrer() {
		SharedPreferences prefs = app.getApplicationContext().getSharedPreferences(UUID_KEY, Context.MODE_PRIVATE);
		return prefs.getString("referrer", null);
	}


	@Override
	public Integer getCountForReward(Reward reward){
		synchronized (this) {
			SharedPreferences prefs = app.getApplicationContext().getSharedPreferences(WOM_REWARDS_KEY, Context.MODE_PRIVATE);
			return prefs.getInt(Integer.toString(reward.getOfferId()), 0);
		}
	}

	@Override
	public void consumeReward(Reward reward){
		String key = String.format("%d", reward.getOfferId());
		synchronized (this) {
			SharedPreferences prefs = app.getApplicationContext().getSharedPreferences(WOM_REWARDS_KEY, Context.MODE_PRIVATE);

			prefs.edit()
				.putInt(key, prefs.getInt(key, 0) + 1)
				.apply();
		}
	}

	@Override
	public Callable<AdvertisingID> getAdIdFetcher(){
		return new AdvertisingIdFetcher(app);
	}

	@Override
	public ActivityEventSource getActivityEventSource() {

		// Using reflection, try to instantiate the ActivityCallbacks class.  ActivityCallbacks
		// is derived from a class only available in api 14, so we expect this to fail for any
		// android version prior to 4.  For older android versions, a dummy implementation is used.
		ActivityEventSource aes;
		try {
			Class<?> cls = Class.forName("com.tapstream.sdk.api14.ActivityCallbacks");
			Constructor<?> constructor = cls.getConstructor(Application.class);
			return (ActivityEventSource)constructor.newInstance(app);
		} catch(Exception e) {
			return new ActivityEventSource();
		}
	}
}
