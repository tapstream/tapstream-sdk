package com.tapstream.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class Event {
	private double firstFiredTime = 0;
	private String uid;
	protected String name;
	private String encodedName;
	private boolean oneTimeOnly;
	private StringBuilder postData = null;

	public Event(String name, boolean oneTimeOnly) {
		uid = makeUid();
		this.oneTimeOnly = oneTimeOnly;
		setName(name);
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

	void setName(String name) {
		this.name = name.toLowerCase().trim();
		try {
			encodedName = URLEncoder.encode(this.name, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private String makeUid() {
		return String.format(Locale.US, "%d:%f", System.currentTimeMillis(), Math.random());
	}

	protected void addPair(String prefix, String key, Object value) {
		String encodedPair = Utils.encodeEventPair(prefix, key, value);
		if(encodedPair == null) {
			return;
		}
		if(postData == null) {
			postData = new StringBuilder();
		}
		postData.append("&");
		postData.append(encodedPair);
	}
};