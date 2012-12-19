package com.tapstream.sdk;

interface Delegate {
	public int getDelay();

	public boolean isRetryAllowed();
}
