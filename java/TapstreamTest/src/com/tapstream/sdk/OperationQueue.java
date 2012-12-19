package com.tapstream.sdk;

import java.util.concurrent.ArrayBlockingQueue;

import junit.framework.TestCase;

class Operation {
	public String name;
	public String arg;

	public Operation(String name, String arg) {
		this.name = name;
		this.arg = arg;
	}
}

public class OperationQueue extends ArrayBlockingQueue<Operation> {
	private static final long serialVersionUID = 1L;

	public OperationQueue() {
		super(32);
	}

	public void expect(String opName) throws InterruptedException {
		Operation op = take();
		TestCase.assertEquals(opName, op.name);
	}
}
