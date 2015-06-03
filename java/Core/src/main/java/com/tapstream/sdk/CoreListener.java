package com.tapstream.sdk;

interface CoreListener {
	public void reportOperation(String op);

	public void reportOperation(String op, String arg);
}
