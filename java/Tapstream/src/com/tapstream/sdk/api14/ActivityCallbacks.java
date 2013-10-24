package com.tapstream.sdk.api14;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;
import android.os.Bundle;

import com.tapstream.sdk.ActivityEventSource;

public class ActivityCallbacks extends ActivityEventSource implements ActivityLifecycleCallbacks {

	private final Application app;
	
	// For Android, the Tapstream SDK is initialized in the main activity's onCreate method, which
	// is called before the activity is shown, so it will be followed by a call to onStart.
	//
	// For iOS, the Tapstream SDK is initialized in the didFinishLaunchingWithOptions method, which
	// is not followed by a call to applicationWillEnterForeground.
	//
	// In order to maintain consistency with the iOS SDK, and to ensure proper behaviour on pre-API-14
	// versions of Android, we need to ignore the first onActivityStarted call that we get (since the
	// SDK invariably fires an open event when it is initialized).  We'll do this by setting the 
	// startedActivities counter to negative one.
	private int startedActivities = -1;
	
	public ActivityCallbacks(Application app) {
		super();
		this.app = app;
		app.registerActivityLifecycleCallbacks(this);
	}
	
	@Override
	public void onActivityCreated(Activity activity, Bundle bundle) {}

	@Override
	public void onActivityDestroyed(Activity activity) {}

	@Override
	public void onActivityPaused(Activity activity) {}

	@Override
	public void onActivityResumed(Activity activity) {}
	
	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

	@Override
	public void onActivityStarted(Activity activity) {
		if(this.app == activity.getApplication()) {
			synchronized(this) {
				startedActivities++;
				if(startedActivities == 1 && listener != null) {
					// Notify the listener when the application goes from zero
					// started activities to one.
					listener.onOpen();
				}
			}
		}
	}

	@Override
	public void onActivityStopped(Activity activity) {
		if(this.app == activity.getApplication()) {
			synchronized(this) {
				startedActivities--;
				if(startedActivities < 0) {
					startedActivities = 0;
				}
			}
		}
	}
	

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK) {
			if(data.getIntExtra("RESPONSE_CODE", -1) == 0) {
				String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
				if(purchaseData != null) {
					try {
						JSONObject jo = new JSONObject(purchaseData);
						String transactionId = jo.getString("orderId");
						String productId = jo.getString("productId");
						
						
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}