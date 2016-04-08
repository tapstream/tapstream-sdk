package com.tapstream.sdk;

interface Delegate {
	public void init(Core core);
	public int getDelay();
	public void setDelay(int delay);
	public boolean isRetryAllowed();
}
