package com.tapstream.sdk;

import com.tapstream.sdk.timeline.TimelineApiResponse;

import java.io.Closeable;

public interface ApiClient extends Closeable {
	void fireEvent(Event e);
	void lookupTimeline(Callback<TimelineApiResponse> completion);
}
