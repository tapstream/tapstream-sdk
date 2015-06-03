package com.tapstream.sdk;

import java.util.concurrent.ThreadFactory;

public class WorkerThread extends Thread {
	public static class Factory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			return new WorkerThread(r);
		}
	}

	public WorkerThread(Runnable r) {
		super(r);
	}
}