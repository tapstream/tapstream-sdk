package com.tapstream.sdk;


import android.app.Application;
import android.content.SharedPreferences;

import com.tapstream.sdk.http.HttpResponse;

import java.io.InputStream;

public class TestUtils {

    public static void clearPrefs(Application app, String key){
        SharedPreferences prefs = app.getApplicationContext().getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearState(Application app){
        clearPrefs(app, AndroidPlatform.FIRED_EVENTS_KEY);
        clearPrefs(app, AndroidPlatform.UUID_KEY);
        clearPrefs(app, AndroidPlatform.WOM_REWARDS_KEY);
    }

    public static HttpResponse jsonResponse(Application app, int resId) throws Exception{
        InputStream is = app.getResources().openRawResource(resId);
        byte[] body = Utils.readFully(is);
        return new HttpResponse(200, "", body);
    }
}
