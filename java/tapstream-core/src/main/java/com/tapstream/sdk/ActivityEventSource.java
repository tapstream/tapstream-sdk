package com.tapstream.sdk;

public class ActivityEventSource {

	public interface ActivityListener {
		void onOpen();
	}
	
	protected ActivityListener listener;

	public ActivityEventSource() {
		listener = null;
	}

	public void setListener(ActivityListener listener) {
		this.listener = listener;
	}
}
