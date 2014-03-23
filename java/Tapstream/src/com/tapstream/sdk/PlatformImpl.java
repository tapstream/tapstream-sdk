package com.tapstream.sdk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreProtocolPNames;

import android.content.Context;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


class PlatformImpl implements Platform {
	private static final String FIRED_EVENTS_KEY = "TapstreamSDKFiredEvents";
	private static final String UUID_KEY = "TapstreamSDKUUID";

	private Context context;

	public PlatformImpl(Context context) {
		this.context = context;
	}

	public ThreadFactory makeWorkerThreadFactory() {
		return new WorkerThread.Factory();
	}

	public String loadUuid() {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(UUID_KEY, 0);
		String uuid = prefs.getString("uuid", null);
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("uuid", uuid);
			editor.commit();
		}
		return uuid;
	}

	public Set<String> loadFiredEvents() {
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences(FIRED_EVENTS_KEY, 0);
		Map<String, ?> fired = settings.getAll();
		return new HashSet<String>(fired.keySet());
	}

	public void saveFiredEvents(Set<String> firedEvents) {
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences(FIRED_EVENTS_KEY, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		for (String name : firedEvents) {
			editor.putString(name, "");
		}
		editor.commit();
	}

	public String getResolution() {
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return String.format(Locale.US, "%dx%d", metrics.widthPixels, metrics.heightPixels);
	}

	public String getManufacturer() {
		try {
			return Build.MANUFACTURER;
		} catch (Exception e) {
			return null;
		}
	}

	public String getModel() {
		return Build.MODEL;
	}

	public String getOs() {
		return String.format(Locale.US, "Android %s", Build.VERSION.RELEASE);
	}

	public String getLocale() {
		return Locale.getDefault().toString();
	}

	public String getWifiMac() {
		try {
			WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = manager.getConnectionInfo();
			return info.getMacAddress();
		} catch (SecurityException e) {
			Logging.log(Logging.ERROR, "Tapstream Error: Failed to get wifi mac address - you need to add the ACCESS_WIFI_STATE permission to your manifest.");
			return null;
		}
	}

	public String getDeviceId() {
		try {
			TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			return manager.getDeviceId();
		} catch (SecurityException e) {
			Logging.log(Logging.ERROR, "Tapstream Error: Failed to get device id - you need to add the READ_PHONE_STATE permission to your manifest.");
			return null;
		}
	}

	public String getAndroidId() {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	public String getAppName() {
		PackageManager pm = context.getPackageManager();
		String appName;
		try {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
			appName = pm.getApplicationLabel(ai).toString();
		} catch (NameNotFoundException e) {
			appName = context.getPackageName();
		}
		return appName;
	}
	
	public String getAppVersion() {
		PackageManager pm = context.getPackageManager();
		try {
			return pm.getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	public String getPackageName() {
		return context.getPackageName();
	}

	public Response request(String url, String data, String method) {
		WorkerThread th = (WorkerThread) Thread.currentThread();

		HttpRequestBase req;
		if(method == "POST") {
			req = new HttpPost(url);
			if(data != null) {
				StringEntity se = null;
				try {
					se = new StringEntity(data);	
				} catch (UnsupportedEncodingException e) {
					return new Response(-1, e.toString(), null);
				}
				se.setContentType("application/x-www-form-urlencoded");
				((HttpPost)req).setEntity(se);
			}
		} else {
			req = new HttpGet(url);
		}
		req.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

		HttpResponse response = null;
		try {
			response = th.client.execute(req);
		} catch (Exception e) {
			return new Response(-1, e.toString(), null);
		}

		StatusLine statusLine = response.getStatusLine();
		String responseData = null;
		try {
			InputStream is = response.getEntity().getContent();
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				StringBuilder sb = new StringBuilder();
				String line;
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
				responseData = sb.toString();
			} finally {
				is.close();
			}
		} catch (Exception e) {
			return new Response(-1, e.toString(), null);
		}

		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
			return new Response(200, null, responseData);
		}
		return new Response(statusLine.getStatusCode(), statusLine.getReasonPhrase(), null);
	}
	
	public Set<String> getProcessSet() {
		ActivityManager actvityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();
		Set<String> set = new HashSet<String>();
		for(RunningAppProcessInfo info : procInfos) {
			set.add(info.processName);
		}
		return set;
	}
	
	public String getReferrer() {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(UUID_KEY, 0);
		return prefs.getString("referrer", null);
	}
	
	public String getAdvertisingId() {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(UUID_KEY, 0);
		return prefs.getString("advertisingId", null);
	}

	public Boolean getLimitAdTracking() {
		SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(UUID_KEY, 0);
		return prefs.contains("limitAdTracking") ? prefs.getBoolean("limitAdTracking", false) : null;
	}
}