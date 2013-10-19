package com.tapstream.sdk;

public class ActivityEventSource {
	public interface ActivityListener {
		void onOpen();
	}
	
	protected ActivityListener listener = null;
	
	public ActivityEventSource() {}
	public void setListener(ActivityListener listener) {
		this.listener = listener;
	}
}
