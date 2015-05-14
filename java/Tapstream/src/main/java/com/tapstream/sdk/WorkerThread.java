package com.tapstream.sdk;

import java.util.concurrent.ThreadFactory;

import org.apache.http.impl.client.DefaultHttpClient;

public class WorkerThread extends Thread {
	public static class Factory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			return new WorkerThread(r);
		}
	}

	public DefaultHttpClient client = new DefaultHttpClient();

	public WorkerThread(Runnable r) {
		super(r);
	}
}