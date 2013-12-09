package com.tapstream.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class Event {
	private double firstFiredTime = 0;
	private String uid;
	private String name;
	private String encodedName;
	private boolean oneTimeOnly;
	private StringBuilder postData = null;

	private boolean isTransaction = false;
	private String productSku;

	public Event(String name, boolean oneTimeOnly) {
		uid = makeUid();
		this.oneTimeOnly = oneTimeOnly;
		setName(name);
	}

	// Only to be used for creating custom purchase events
	public Event(String orderId, String productSku, int quantity) {
		this("", false);
		isTransaction = true;
		this.productSku = productSku;
		initialize(orderId, productSku, quantity);
	}

	// Only to be used for creating custom purchase events
	public Event(String orderId, String productSku, int quantity, int priceInCents, String currencyCode) {
		this("", false);
		isTransaction = true;
		this.productSku = productSku; 
		initialize(orderId, productSku, quantity, priceInCents, currencyCode);
	}

	// Only to be used for creating IAB purchase events
	public Event(JSONObject purchase, JSONObject skuDetails) throws JSONException {
		this("", false);
		isTransaction = true;
		productSku = purchase.getString("productId");
		String orderId = purchase.getString("orderId");

		try {
			String currencyCode = skuDetails.getString("price_currency_code");
			int priceMicros = skuDetails.getInt("price_amount_micros");
			int priceCenti = (int)Math.round(priceMicros / 10000.0);
			initialize(orderId, productSku, 1, priceCenti, currencyCode);
		} catch (JSONException e) {
			// Older versions of the Google Play Store app don't send the currency and amount separately
			initialize(orderId, productSku, 1);
		}
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

	boolean isTransaction() {
		return isTransaction;
	}

	void setName(String name) {
		this.name = name.toLowerCase().trim();
		try {
			encodedName = URLEncoder.encode(this.name, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	void setNamePrefix(String appName) {
		setName(String.format(Locale.US, "android-%s-purchase-%s", appName, productSku));
	}

	private void initialize(String orderId, String productSku, int quantity) {
		addPair("", "purchase-transaction-id", orderId);
		addPair("", "purchase-product-id", productSku);
		addPair("", "purchase-quantity", quantity);
	}

	private void initialize(String orderId, String productSku, int quantity, int priceInCents, String currencyCode) {
		addPair("", "purchase-transaction-id", orderId);
		addPair("", "purchase-product-id", productSku);
		addPair("", "purchase-quantity", quantity);
		addPair("", "purchase-price", priceInCents);
		addPair("", "purchase-currency", currencyCode);
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