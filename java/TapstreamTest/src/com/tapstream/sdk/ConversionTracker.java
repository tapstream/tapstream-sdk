package com.tapstream.sdk;

import com.tapstream.sdk.Hit.CompletionHandler;

class ConversionTracker implements Api {

	private class DelegateImpl implements Delegate {
		public int getDelay() {
			return 0;
		}

		public boolean isRetryAllowed() {
			return false;
		}
	}

	private Delegate delegate;
	public Platform platform;
	private CoreListener listener;
	public Core core;

	public ConversionTracker(OperationQueue queue, String accountName, String developerSecret) {
		delegate = new DelegateImpl();
		platform = new PlatformImpl();
		listener = new CoreListenerImpl(queue);
		core = new Core(delegate, platform, listener, accountName, developerSecret, null);
	}

	public ConversionTracker(OperationQueue queue, String accountName, String developerSecret, String hardware) {
		delegate = new DelegateImpl();
		platform = new PlatformImpl();
		listener = new CoreListenerImpl(queue);
		core = new Core(delegate, platform, listener, accountName, developerSecret, hardware);
	}

	public void fireEvent(Event e) {
		core.fireEvent(e);
	}

	public void fireHit(Hit h, CompletionHandler completion) {
		core.fireHit(h, completion);
	}
}
