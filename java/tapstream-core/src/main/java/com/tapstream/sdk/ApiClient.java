package com.tapstream.sdk;

import java.io.Closeable;

public interface ApiClient extends Closeable {
	ApiFuture<EventApiResponse> fireEvent(Event e);
	ApiFuture<TimelineApiResponse> lookupTimeline();
}
