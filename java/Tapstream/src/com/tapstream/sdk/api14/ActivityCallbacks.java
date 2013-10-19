package com.tapstream.sdk.api14;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;

import com.tapstream.sdk.ActivityEventSource;

public class ActivityCallbacks extends ActivityEventSource implements ActivityLifecycleCallbacks {

	private final Context context;
	
	// For Android, the Tapstream SDK is initialized in the main activity's onCreate method, which
	// is called before the activity is shown.
	//
	// For iOS, the Tapstream SDK is initialized in the didFinishLaunchingWithOptions method, which
	// is called after the app is shown.
	//
	// In order to maintain consistency with the iOS SDK, and to ensure proper behaviour on pre-API-14
	// versions of Android, we need to ignore the first onActivityStarted call that we get (since the
	// SDK invariably fires an open event when it is initialized).  We'll do this by setting the 
	// startedActivities counter to negative one.
	private int startedActivities = -1;
	
	public ActivityCallbacks(Context context) {
		super();
		this.context = context;
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
		if(this.context == activity.getApplicationContext()) {
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
		if(this.context == activity.getApplicationContext()) {
			synchronized(this) {
				startedActivities--;
				if(startedActivities < 0) {
					startedActivities = 0;
				}
			}
		}
	}
	
}