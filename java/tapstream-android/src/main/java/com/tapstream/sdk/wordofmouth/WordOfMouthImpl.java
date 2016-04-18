package com.tapstream.sdk.wordofmouth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;

import com.tapstream.sdk.Logging;
import com.tapstream.sdk.Maybe;
import com.tapstream.sdk.Platform;
import com.tapstream.sdk.http.HttpMethod;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Date: 15-05-01
 * Time: 1:50 PM
 */
public class WordOfMouthImpl implements WordOfMouth{
    final Platform platform;
    final ExecutorService executor;
    final String sdkSecret;
    final String bundle;
    final String OFFER_ENDPOINT = "https://app.tapstream.com/api/v1/word-of-mouth/offers/";
    final String REWARD_ENDPOINT = "https://app.tapstream.com/api/v1/word-of-mouth/rewards/";

    public static WordOfMouth getInstance(ExecutorService executor, Platform platform, String sdkSecret, String bundle){
        return new WordOfMouthImpl(executor, platform, sdkSecret, bundle);
    }

    private WordOfMouthImpl(ExecutorService executor, Platform platform, String sdkSecret, String bundle){
        this.executor = executor;
        this.platform = platform;
        this.sdkSecret = sdkSecret;
        this.bundle = bundle;
    }

    @Override
    public void consumeReward(Reward reward){
        platform.consumeReward(reward);
    }

    @Override
    public boolean isConsumed(Reward reward){
        int rewardCount = reward.getInstallCount() / reward.getMinimumInstalls();
        int consumed = platform.getCountForReward(reward);
        return consumed >= rewardCount;
    }

    public void showOffer(final Activity mainActivity, View parent, final Offer o){
        /**
         * - Build PopupWindow
         * - Make WebView (given Context)
         * - Populate WebView with HTML postBody
         * - Add webview to PopupWindow
         * - Build WebViewClient
         *   - WebViewClient builds intent
         *   - WebViewClient sends intent (given mainActivity)
         */
        final Context applicationContext = mainActivity.getApplicationContext();
        WebView wv = new WebView(applicationContext);
        final PopupWindow window = new PopupWindow(
            wv,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        window.showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);


        wv.loadDataWithBaseURL("https://tapstream.com/", o.getMarkup(), "text/html", null, "https://tapstream.com/");
        wv.setBackgroundColor(Color.TRANSPARENT);
        final String uuid = platform.loadUuid();

        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.endsWith("accept")){
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, o.prepareMessage(uuid));
                    sendIntent.setType("text/plain");
                    mainActivity.startActivity(sendIntent);
                }
                window.dismiss();
                return true;
            }
        });
    }



    private Maybe<Offer> getOfferSync(String insertionPoint){
        String uri = Uri.parse(OFFER_ENDPOINT).buildUpon()
            .appendQueryParameter("secret", sdkSecret)
            .appendQueryParameter("insertion_point", insertionPoint)
            .appendQueryParameter("bundle", bundle).build().toString();


        try {
            HttpResponse resp = platform.sendRequest(new HttpRequest(new URL(uri), HttpMethod.GET, null));
            if(resp.status != 200){
                return Maybe.nope();
            }

            JSONObject responseObject = new JSONObject(resp.getBodyAsString());
            return Maybe.yup(Offer.fromApiResponse(responseObject));
        }catch(Exception e){
            e.printStackTrace();
            Logging.log(Logging.WARN, e.getMessage());
        }
        return Maybe.nope();
    }


    private List<Reward> getRewardListSync(){
        String eventSession = platform.loadUuid();

        String uri = Uri.parse(REWARD_ENDPOINT).buildUpon()
            .appendQueryParameter("secret", sdkSecret)
            .appendQueryParameter("event_session", eventSession).build().toString();


        try {
            HttpResponse resp = platform.sendRequest(new HttpRequest(new URL(uri), HttpMethod.GET, null));

            JSONArray responseObject = new JSONArray(resp.getBodyAsString());
            List<Reward> result = new ArrayList<Reward>(responseObject.length());
            for(int ii=0; ii<responseObject.length(); ii++) {
                Reward reward = Reward.fromApiResponse(responseObject.getJSONObject(ii));
                if(!isConsumed(reward)) {
                    result.add(reward);
                }
            }
            return result;
        }catch(Exception e){
            Logging.log(Logging.WARN, e.getMessage());
            return new ArrayList<Reward>();
        }
    }

    // Async

    public Future<Maybe<Offer>> getOffer(final String insertionPoint){

        return executor.submit(new Callable<Maybe<Offer>>() {
            public Maybe<Offer> call(){
                return getOfferSync(insertionPoint);
            }
        });
    }

    public Future<List<Reward>> getRewardList(){
        return executor.submit(new Callable<List<Reward>>() {
            public List<Reward> call() {
                return getRewardListSync();
            }
        });
    }
}
