package com.tapstream.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ReferrerReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String referrer = intent.getStringExtra("referrer");
		if(referrer != null) {
			String decoded = "";

			try {
				decoded = URLDecoder.decode(referrer, "utf-8");
			} catch(UnsupportedEncodingException e) {
				return;
			}

			if(decoded.length() > 0) {
				SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(AndroidPlatform.UUID_KEY, 0);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("referrer", decoded);
				editor.apply();
			}
		}
	}

}
