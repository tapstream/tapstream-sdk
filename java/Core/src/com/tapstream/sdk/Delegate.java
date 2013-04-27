package com.tapstream.sdk;

interface Delegate {
	public int getDelay();
	public void setDelay(int delay);
	public boolean isRetryAllowed();
}
