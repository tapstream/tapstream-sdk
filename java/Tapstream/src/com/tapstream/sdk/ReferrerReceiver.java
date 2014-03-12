package com.tapstream.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ReferrerReceiver extends BroadcastReceiver {
	private static final String UUID_KEY = "TapstreamSDKUUID";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String referrer = intent.getStringExtra("referrer");
		if(referrer != null) {
			String decoded = "";
			try {
				decoded = URLDecoder.decode(referrer, "utf-8");
			} catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Log.d("ReferrerReceiver", decoded);
			if(decoded.length() > 0) {
				SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(UUID_KEY, 0);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("referrer", decoded);
				editor.commit();
			}
		}
	}

}
