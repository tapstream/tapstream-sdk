package com.tapstream.sdk.api14;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import com.tapstream.sdk.ActivityEventSource;

@TargetApi(14)
public class ActivityCallbacks extends ActivityEventSource implements ActivityLifecycleCallbacks {

	private final Application app;

	private int startedActivities = 0;
	
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

}