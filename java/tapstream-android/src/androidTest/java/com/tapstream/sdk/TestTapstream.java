package com.tapstream.sdk;

import org.junit.Before;

import android.support.test.runner.AndroidJUnit4;

import com.tapstream.sdk.http.HttpClient;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.wordofmouth.Reward;
import com.tapstream.sdk.wordofmouth.RewardApiResponse;
import com.tapstream.sdk.wordofmouth.WordOfMouth;
import com.tapstream.sdk.wordofmouth.WordOfMouthImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class TestTapstream extends BaseAndroidTest {

    HttpClient httpClient;
    AndroidPlatform platform;
    Tapstream ts;
    Config config;

    final String ACCOUNT_NAME = "sdktest";
    final String SDKTEST_SECRET = "YGP2pezGTI6ec48uti4o1w";

    @Before
    public void setUp() throws Exception{
        app.getApplicationInfo().name = "TapstreamTest";

        httpClient = mock(HttpClient.class);
        platform = new AndroidPlatform(app);

        config = new Config(ACCOUNT_NAME, SDKTEST_SECRET);
        WordOfMouth wom = WordOfMouthImpl.getInstance(platform);
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
        HttpApiClient client = new HttpApiClient(platform, config, httpClient, ex);
        ts = new Tapstream(client, wom);
    }


    @Test
    public void testRewardConsumption() throws Exception{
        when(httpClient.sendRequest((HttpRequest) any()))
                .thenReturn(jsonResponse(com.tapstream.sdk.test.R.raw.rewards));

        ApiFuture<RewardApiResponse> futureRewards = ts.getWordOfMouthRewardList();
        List<Reward> rewards = futureRewards.get().getRewards();
        assertThat(rewards.size(), is(1));

        WordOfMouth wm = ts.getWordOfMouth();

        assertThat(wm.isConsumed(rewards.get(0)), is(false));
        wm.consumeReward(rewards.get(0));
        assertThat(wm.isConsumed(rewards.get(0)), is(true));

        // Get it again
        futureRewards = ts.getWordOfMouthRewardList();
        rewards = futureRewards.get().getRewards();
        assertThat(rewards.size(), is(0));
    }


}
