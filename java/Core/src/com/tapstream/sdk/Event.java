package com.tapstream.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class Event {
	private double firstFiredTime = 0;
	private String uid;
	private String name;
	private String encodedName;
	private boolean oneTimeOnly;
	private StringBuilder postData = null;

	public Event(String name, boolean oneTimeOnly) {
		uid = makeUid();
		this.name = name.toLowerCase().trim();
		this.oneTimeOnly = oneTimeOnly;
		try {
			encodedName = URLEncoder.encode(this.name, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	// This constructor is only to be used for creating custom IAP events.
	public Event(String name, String transactionId, String productId, int quantity, int priceInCents, String currencyCode)
	{
		this(name, false);
		addPair("", "purchase-transaction-id", transactionId);
		addPair("", "purchase-product-id", productId);
		addPair("", "purchase-quantity", quantity);
		addPair("", "purchase-price", priceInCents);
		addPair("", "purchase-currency", currencyCode);
	}

	public void addPair(String key, Object value) {
		addPair("custom-", key, value);
	}

	public String getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public String getEncodedName() {
		return encodedName;
	}

	public boolean isOneTimeOnly() {
		return oneTimeOnly;
	}

	public String getPostData() {
		String data = postData != null ? postData.toString() : "";
		return String.format(Locale.US, "&created-ms=%.0f", firstFiredTime) + data;
	}

	void firing() {
		// Only record the time of the first fire attempt
		if (firstFiredTime == 0) {
			firstFiredTime = System.currentTimeMillis();
		}
	}

	private String makeUid() {
		return String.format(Locale.US, "%d:%f", System.currentTimeMillis(), Math.random());
	}

	private void addPair(String prefix, String key, Object value) {
		if(value == null) {
			return;
		}

		if (key.length() > 255) {
			Logging.log(Logging.WARN, "Tapstream Warning: Custom key exceeds 255 characters, this field will not be included in the post (key=%s)", key);
			return;
		}

		String encodedName = null;
		try {
			encodedName = URLEncoder.encode(prefix + key, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		String stringifiedValue = null;
		try {
			double d = (Double) value;
			double truncated = Math.floor(d);
			if (truncated == d) {
				stringifiedValue = String.format(Locale.US, "%.0f", d);
			} else {
				stringifiedValue = value.toString();
			}
		} catch (ClassCastException ex) {
			stringifiedValue = value.toString();
		}
	
		if (stringifiedValue.length() > 255) {
			Logging.log(Logging.WARN, "Tapstream Warning: Custom value exceeds 255 characters, this field will not be included in the post (value=%s)", value);
			return;
		}

		String encodedValue = null;
		try {
			encodedValue = URLEncoder.encode(stringifiedValue, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		if (postData == null) {
			postData = new StringBuilder();
		}
		postData.append("&");
		postData.append(encodedName);
		postData.append("=");
		postData.append(encodedValue);
	}
};