package com.tapstream.sdk;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import com.tapstream.sdk.http.HttpResponse;

import org.junit.Before;

import java.io.InputStream;

abstract public class BaseAndroidTest {

    protected Application app;

    @Before
    final public void setupApp() throws Exception {
        app = (Application) InstrumentationRegistry.getTargetContext().getApplicationContext();
        clearState();
    }

    public void clearPrefs(String key){
        SharedPreferences prefs = app.getApplicationContext().getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public void clearState(){
        clearPrefs(AndroidPlatform.FIRED_EVENTS_KEY);
        clearPrefs(AndroidPlatform.UUID_KEY);
        clearPrefs(AndroidPlatform.WOM_REWARDS_KEY);
    }

    public HttpResponse jsonResponse(int resId) throws Exception{
        InputStream is = app.getResources().openRawResource(resId);
        byte[] body = Utils.readFully(is);
        return new HttpResponse(200, "", body);
    }
}
