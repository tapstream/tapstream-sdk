package com.tapstream.exampleasdf234fsad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReferrerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		new com.tapstream.sdk.ReferrerReceiver().onReceive(context,  intent);
		new com.google.analytics.tracking.android.CampaignTrackingReceiver().onReceive(context, intent);
	}

}
