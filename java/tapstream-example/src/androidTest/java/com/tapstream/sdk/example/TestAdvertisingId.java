package com.tapstream.sdk.example;

import android.app.Application;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.tapstream.sdk.AdvertisingID;
import com.tapstream.sdk.AdvertisingIdFetcher;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class TestAdvertisingId {

    @Test
    public void testFetcher() throws Exception {
        Application app = (Application) InstrumentationRegistry.getTargetContext().getApplicationContext();
        AdvertisingIdFetcher fetcher = new AdvertisingIdFetcher(app);
        AdvertisingID id = fetcher.call();
        Log.i("Tapstream", id.toString());
    }
}
