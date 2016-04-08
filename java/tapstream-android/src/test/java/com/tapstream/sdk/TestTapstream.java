package com.tapstream.sdk;

import android.app.Application;
import android.content.Context;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.tapstream.sdk.wordofmouth.Offer;
import com.tapstream.sdk.wordofmouth.Reward;
import com.tapstream.sdk.wordofmouth.WordOfMouth;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 15-05-09
 * Time: 10:49 AM
 */
@RunWith(RobolectricTestRunner.class)
public class TestTapstream extends TestCase {

    Tapstream ts;
    PlatformWithMockApi platform;
    final String OFFER_ENDPOINT = "https://app.tapstream.com/api/v1/word-of-mouth/offers/";
    final String REWARD_ENDPOINT = "https://app.tapstream.com/api/v1/word-of-mouth/rewards/";
    final String SDKTEST_SECRET = "YGP2pezGTI6ec48uti4o1w";

    private String readResource(String resourceName){
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    class PlatformWithMockApi extends PlatformImpl {
        Map<String, Response> responses = new HashMap<String, Response>();

        public PlatformWithMockApi(Context context) {
            super(context);
        }

        public void registerResponse(Response response, String method, String url){
            responses.put(method + "|" + url, response);
        }
        public Response request(String url, String data, String method) {
            Response resp = responses.get(method + "|" + url);
            if(resp == null){
                fail("No response for " + method + "|" + url);
            }
            return resp;
        }
    }

    static class DelegateImpl implements Delegate{

        @Override
        public void init(Core core) {  }

        @Override
        public int getDelay() { return 0; }

        @Override
        public void setDelay(int delay) {  }

        @Override
        public boolean isRetryAllowed() { return false; }
    }

    @Before
    public void setUp(){
        Application app = Robolectric.application;
        app.getApplicationInfo().name = "TapstreamTest";
        platform = new PlatformWithMockApi(app);
        ts = new Tapstream(
            new DelegateImpl(),
            platform,
            new CoreListenerImpl(),
            new ActivityEventSource(),
            new AdvertisingIdFetcher(app),
            "sdktest", SDKTEST_SECRET, new Config());
    }

    private Response jsonResponse(String jsonResourcePath){
        return new Response(200, "", readResource(jsonResourcePath));
    }

    private String getOfferUrl(String insertionPoint, String bundle){
        return OFFER_ENDPOINT + "?secret=" + SDKTEST_SECRET + "&insertion_point=" + insertionPoint + "&bundle=" + bundle;
    }

    private String getRewardUrl(String eventSession) {
        return REWARD_ENDPOINT + "?secret=" + SDKTEST_SECRET + "&event_session=" + eventSession;
    }

    @Test
    public void testAndroid() throws Exception{
        platform.registerResponse(jsonResponse("order.json"), "GET", getOfferUrl("wom", platform.getPackageName()));
        platform.registerResponse(jsonResponse("rewards.json"), "GET", getRewardUrl(platform.loadUuid()));

        WordOfMouth wm = ts.getWordOfMouth();
        Maybe<Offer> maybeOffer = wm.getOffer("wom").get();
        assertTrue(maybeOffer.isPresent());
        Offer offer = maybeOffer.get();
        assertEquals(offer.getMessage(), "This is the message");

        List<Reward> rewards = wm.getRewardList().get();
        assertEquals(rewards.size(), 1);
        assertEquals(rewards.get(0).getRewardSku(), "my reward sku");
    }

    @Test
    public void testRewardConsumption() throws Exception{
        platform.registerResponse(jsonResponse("rewards.json"), "GET", getRewardUrl(platform.loadUuid()));

        WordOfMouth wm = ts.getWordOfMouth();
        List<Reward> rewards = wm.getRewardList().get();
        assertEquals(rewards.size(), 1);

        assertFalse(wm.isConsumed(rewards.get(0)));
        wm.consumeReward(rewards.get(0));
        assertTrue(wm.isConsumed(rewards.get(0)));

        // Get it again
        rewards = wm.getRewardList().get();
        assertEquals(rewards.size(), 0);
    }
}
