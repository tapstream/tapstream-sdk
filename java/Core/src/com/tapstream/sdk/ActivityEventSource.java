package com.tapstream.sdk;

public class ActivityEventSource {
	public interface ActivityListener {
		void onOpen();
		void onTransaction(String transactionId, String productId, int quantity, int priceCents, String currencyCode);
	}
	
	protected ActivityListener listener = null;
	
	public ActivityEventSource() {}
	public void setListener(ActivityListener listener) {
		this.listener = listener;
	}
}
