package com.tapstream.sdk.wordofmouth;

import android.app.Activity;
import android.view.View;

/**
 * Date: 15-05-01
 * Time: 1:46 PM
 */
public interface WordOfMouth{
    public void showOffer(Activity mainActivity, View parent, Offer o);
    public boolean isConsumed(Reward reward);
    public void consumeReward(Reward reward);
}
