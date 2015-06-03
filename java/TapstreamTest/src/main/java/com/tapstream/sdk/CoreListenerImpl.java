package com.tapstream.sdk;

class CoreListenerImpl implements CoreListener {
	public OperationQueue queue;

	public CoreListenerImpl(OperationQueue queue) {
		this.queue = queue;
	}

	public void reportOperation(String op) {
		queue.add(new Operation(op, null));
	}

	public void reportOperation(String op, String arg) {
		queue.add(new Operation(op, arg));
	}
}