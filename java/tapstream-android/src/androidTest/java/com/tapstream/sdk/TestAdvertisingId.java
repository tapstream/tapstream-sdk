package com.tapstream.sdk;

import org.junit.runner.RunWith;

import android.os.Build;
import android.app.Application;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public class TestAdvertisingId extends BaseAndroidTest {

    @Test
    public void testFetcher() throws Exception {
        Assume.assumeTrue(Build.VERSION.SDK_INT >= 17);
        AdvertisingIdFetcher fetcher = new AdvertisingIdFetcher(app);
        AdvertisingID id = fetcher.call();
        assertThat(id, notNullValue());
        assertThat(id.getId(), not(isEmptyOrNullString()));
        assertThat(id.isLimitAdTracking(), is(false));
    }
}
