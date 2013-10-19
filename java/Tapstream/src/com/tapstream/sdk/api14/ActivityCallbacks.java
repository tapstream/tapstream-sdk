package com.tapstream.sdk.api14;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;

import com.tapstream.sdk.ActivityEventSource;

public class ActivityCallbacks extends ActivityEventSource implements ActivityLifecycleCallbacks {

	private final Context context;
	private int startedActivities = 0;
	
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
			startedActivities++;
			if(startedActivities == 1 && listener != null) {
				// Notify the listener when the application goes from zero
				// started activities to one.
				listener.onOpen();
			}
		}
	}

	@Override
	public void onActivityStopped(Activity activity) {
		if(this.context == activity.getApplicationContext()) {
			startedActivities--;
		}
	}
	
}