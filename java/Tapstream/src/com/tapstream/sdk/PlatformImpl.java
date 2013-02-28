package com.tapstream.sdk;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
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
		}
		return "unknown";
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

	public Response request(String url, String data) {
		WorkerThread th = (WorkerThread) Thread.currentThread();

		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new StringEntity(data));
		} catch (UnsupportedEncodingException e) {
			return new Response(-1, e.toString());
		}

		HttpResponse response = null;
		try {
			response = th.client.execute(post);
		} catch (Exception e) {
			return new Response(-1, e.toString());
		}

		StatusLine statusLine = response.getStatusLine();
		try {
			response.getEntity().getContent().close();
		} catch (Exception e) {
			return new Response(-1, e.toString());
		}

		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
			return new Response(200, null);
		}
		return new Response(statusLine.getStatusCode(), statusLine.getReasonPhrase());
	}
}