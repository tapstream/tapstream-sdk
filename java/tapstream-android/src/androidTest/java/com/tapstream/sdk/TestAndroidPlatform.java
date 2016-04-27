package com.tapstream.sdk;

import android.content.Intent;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import com.tapstream.sdk.api14.ActivityCallbacks;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;


@RunWith(AndroidJUnit4.class)
public class TestAndroidPlatform extends BaseAndroidTest {

    AndroidPlatform platform;

    @Before
    public void setup() throws Exception {
        platform = new AndroidPlatform(app);
    }

    @Test
    public void testLoadSessionId() throws Exception {
        assertThat(app.getSharedPreferences(AndroidPlatform.UUID_KEY, 0).getString("uuid", null), is(nullValue()));
        String sessionId = platform.loadSessionId();
        assertThat(sessionId, notNullValue());
        assertThat(app.getSharedPreferences(AndroidPlatform.UUID_KEY, 0).getString("uuid", null), is(sessionId));
        assertThat(platform.loadSessionId(), is(sessionId));
        assertThat(app.getSharedPreferences(AndroidPlatform.UUID_KEY, 0).getString("uuid", null), is(sessionId));
    }

    @Test
    public void testPersistFiredEvents() throws Exception {
        Set<String> firedEvents = platform.loadFiredEvents();
        assertThat(firedEvents, is(Matchers.<String>empty()));
        firedEvents.add("eventName");
        platform.saveFiredEvents((firedEvents));
        assertThat(platform.loadFiredEvents(), is(firedEvents));
    }

    @Test
    public void testGetAppName() throws Exception {
        assertThat(platform.getAppName(), is("tapstream-android"));
    }

    @Test
    public void testGetPackageName() throws Exception {
        assertThat(platform.getPackageName(), is("com.tapstream.sdk.test"));
    }

    @Test
    public void testGetReferrer() throws Exception {
        String expected = "theReferrer";
        ReferrerReceiver r = new ReferrerReceiver();
        String url = "intent://scan/#Intent;scheme=zxing;package=com.google.zxing.client.android;S.referrer=" + expected + ";end" + expected;
        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        r.onReceive(app, intent);
        String actual = platform.getReferrer();
        assertThat(actual, is(expected));
    }

    @Test
    public void testGetCountForReward() throws Exception {
        /* TODO: Add implementation */
    }

    @Test
    public void testConsumeReward() throws Exception {
        /* TODO: Add implementation */
    }

    @Test
    public void testGetAdIdFetcher() throws Exception {
        Object fetcher = platform.getAdIdFetcher();
        assertThat(fetcher, allOf(notNullValue(), instanceOf(AdvertisingIdFetcher.class)));
    }

    @Test
    public void testGetActivityEventSource() throws Exception {
        if (Build.VERSION.SDK_INT >= 14){
            // Should return a real implementation
            Object aes = platform.getActivityEventSource();
            assertThat(aes, is(notNullValue()));
            assertThat(aes, instanceOf(ActivityCallbacks.class));
        } else {
            // Should return a no-op implementation
            Object aes = platform.getActivityEventSource();
            assertThat(aes, is(notNullValue()));
            assertThat(ActivityEventSource.class == aes.getClass(), is(true));
        }
    }
}