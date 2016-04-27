package com.tapstream.sdk.wordofmouth;

import android.app.Activity;
import android.view.View;

/**
 * Date: 15-05-01
 * Time: 1:46 PM
 */
public interface WordOfMouth{
    /**
     * Displays the Word-of-Mouth offer overlay for the provided offer as configured in Tapstream's
     * dashboard. The overlay is attached to the parent view provided.
     *
     * @param mainActivity Your application's main activity.
     * @param parent The parent view to attach the overlay to.
     * @param o The offer for which to show the sharing overlay.
     */
    void showOffer(Activity mainActivity, View parent, Offer o);

    /**
     * Given a Reward, report whether the reward has been consumed. The number of rewards earned by
     * a user is (number of installs driven)/(minimum installs per reward), the latter being configurable
     * on Tapstream's dashboard. If (number of rewards earned) is >= (number of rewards consumed),
     * isConsumed will return true.
     *
     * @param reward The Reward to check
     * @return True if this user is *not* owed a reward.
     */
    boolean isConsumed(Reward reward);

    /**
     * Increments the (number of rewards consumed), stored on the device. Call this method after you
     * have delived a reward SKU to the user.
     *
     * @param reward The Reward to consume
     */
    void consumeReward(Reward reward);
}
