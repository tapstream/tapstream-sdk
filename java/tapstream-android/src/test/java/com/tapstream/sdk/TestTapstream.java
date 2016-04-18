package com.tapstream.sdk;

import android.app.Application;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;
import com.tapstream.sdk.http.StdLibHttpClient;
import com.tapstream.sdk.wordofmouth.Offer;
import com.tapstream.sdk.wordofmouth.Reward;
import com.tapstream.sdk.wordofmouth.WordOfMouth;
import com.tapstream.sdk.wordofmouth.WordOfMouthImpl;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Date: 15-05-09
 * Time: 10:49 AM
 */
@RunWith(RobolectricGradleTestRunner.class)
@org.robolectric.annotation.Config(constants = BuildConfig.class, sdk=21)
public class TestTapstream extends TestCase {

    static class HttpClientWithMockApi extends StdLibHttpClient {
        Map<String, HttpResponse> responses = new HashMap<String, HttpResponse>();

        public void registerResponse(HttpResponse response, String method, String url){
            responses.put(method + "|" + url, response);
        }
        public HttpResponse sendRequest(HttpRequest request) throws IOException {
            HttpResponse resp = responses.get(request.getMethod() + "|" + request.getURL());
            if(resp == null){
                fail("No response for " + request.getMethod() + "|" + request.getURL());
            }
            return resp;
        }
    }

    Tapstream ts;
    HttpClientWithMockApi httpClient;
    Platform platform;
    final String OFFER_ENDPOINT = "https://app.tapstream.com/api/v1/word-of-mouth/offers/";
    final String REWARD_ENDPOINT = "https://app.tapstream.com/api/v1/word-of-mouth/rewards/";
    final String ACCOUNT_NAME = "sdktest";
    final String SDKTEST_SECRET = "YGP2pezGTI6ec48uti4o1w";

    private String readResource(String resourceName){
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Before
    public void setUp(){
        Application app = RuntimeEnvironment.application;
        app.getApplicationInfo().name = "TapstreamTest";
        platform = new AndroidPlatform(app);
        httpClient = new HttpClientWithMockApi();
        Config config = new Config(ACCOUNT_NAME, SDKTEST_SECRET);
        WordOfMouth wom = WordOfMouthImpl.getInstance(platform);
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
        HttpApiClient client = new HttpApiClient(platform, config, httpClient, ex);

        ts = new Tapstream(client, wom);
    }

    private HttpResponse jsonResponse(String jsonResourcePath){
        return new HttpResponse(200, "", readResource(jsonResourcePath).getBytes());
    }

    private String getOfferUrl(String insertionPoint, String bundle){
        return OFFER_ENDPOINT + "?secret=" + SDKTEST_SECRET + "&insertion_point=" + insertionPoint + "&bundle=" + bundle;
    }

    private String getRewardUrl(String eventSession) {
        return REWARD_ENDPOINT + "?secret=" + SDKTEST_SECRET + "&event_session=" + eventSession;
    }

    @Test
    public void testWordOfMouth() throws Exception{
        httpClient.registerResponse(jsonResponse("order.json"), "GET", getOfferUrl("wom", platform.getPackageName()));
        httpClient.registerResponse(jsonResponse("rewards.json"), "GET", getRewardUrl(platform.loadUuid()));
        
        ApiFuture<Offer> futureOffer = ts.getWordOfMouthOffer("wom");
        Offer offer = futureOffer.get();
        assertNotNull(offer);
        assertEquals(offer.getMessage(), "This is the message");

        final ApiFuture<List<Reward>> futureRewards = ts.getWordOfMouthRewardList();

        List<Reward> rewards = futureRewards.get();
        assertNotNull(rewards);
        assertEquals(rewards.size(), 1);
        assertEquals(rewards.get(0).getRewardSku(), "my reward sku");
    }

    @Test
    public void testRewardConsumption() throws Exception{
        httpClient.registerResponse(jsonResponse("rewards.json"), "GET", getRewardUrl(platform.loadUuid()));

        ApiFuture<List<Reward>> futureRewards = ts.getWordOfMouthRewardList();
        List<Reward> rewards = futureRewards.get();
        assertEquals(rewards.size(), 1);

        WordOfMouth wm = ts.getWordOfMouth();

        assertFalse(wm.isConsumed(rewards.get(0)));
        wm.consumeReward(rewards.get(0));
        assertTrue(wm.isConsumed(rewards.get(0)));

        // Get it again
        futureRewards = ts.getWordOfMouthRewardList();
        rewards = futureRewards.get();
        assertEquals(rewards.size(), 0);
    }
}
