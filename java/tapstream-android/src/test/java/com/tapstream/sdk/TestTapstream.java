package com.tapstream.sdk;

import android.app.Application;

import com.google.common.io.Resources;

import com.tapstream.sdk.http.HttpClient;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;
import com.tapstream.sdk.wordofmouth.Reward;
import com.tapstream.sdk.wordofmouth.WordOfMouth;
import com.tapstream.sdk.wordofmouth.WordOfMouthImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Date: 15-05-09
 * Time: 10:49 AM
 */
@RunWith(RobolectricGradleTestRunner.class)
@org.robolectric.annotation.Config(constants = BuildConfig.class, sdk=21)
public class TestTapstream {

    @Mock HttpClient httpClient;
    Platform platform;
    Tapstream ts;
    Config config;

    final String OFFER_ENDPOINT = "https://app.tapstream.com/api/v1/word-of-mouth/offers/";
    final String REWARD_ENDPOINT = "https://app.tapstream.com/api/v1/word-of-mouth/rewards/";
    final String ACCOUNT_NAME = "sdktest";
    final String SDKTEST_SECRET = "YGP2pezGTI6ec48uti4o1w";
    static HttpResponse jsonResponse(String jsonResourcePath) throws IOException {
        return new HttpResponse(200, "", Resources.toByteArray(Resources.getResource(jsonResourcePath)));
    }

    @Before
    public void setUp(){
        initMocks(this);
        Application app = RuntimeEnvironment.application;
        app.getApplicationInfo().name = "TapstreamTest";
        platform = new AndroidPlatform(app);
        config = new Config(ACCOUNT_NAME, SDKTEST_SECRET);
        WordOfMouth wom = WordOfMouthImpl.getInstance(platform);
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
        HttpApiClient client = new HttpApiClient(platform, config, httpClient, ex);

        ts = new Tapstream(client, wom);
    }

    @Test
    public void testRewardConsumption() throws Exception{
        when(httpClient.sendRequest((HttpRequest) any())).thenReturn(jsonResponse("rewards.json"));

        ApiFuture<List<Reward>> futureRewards = ts.getWordOfMouthRewardList();
        List<Reward> rewards = futureRewards.get();
        assertThat(rewards.size(), is(1));

        WordOfMouth wm = ts.getWordOfMouth();

        assertThat(wm.isConsumed(rewards.get(0)), is(false));
        wm.consumeReward(rewards.get(0));
        assertThat(wm.isConsumed(rewards.get(0)), is(true));

        // Get it again
        futureRewards = ts.getWordOfMouthRewardList();
        rewards = futureRewards.get();
        assertThat(rewards.size(), is(0));
    }


}
