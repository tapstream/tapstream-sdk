package com.tapstream.sdk;

public interface Api {
	void fireEvent(Event e);
	void fireHit(Hit h, Hit.CompletionHandler completion);
}
