package com.tapstream.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
	private double firstFiredTime = 0;
	private String uid;
	private String name;
	private String encodedName;
	private boolean oneTimeOnly;
	private StringBuilder postData = new StringBuilder();
	private boolean isTransaction = false;
	private String productSku;
	Map<String, Object> customFields = new HashMap<String, Object>();

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

		addPair("", "purchase-transaction-id", orderId, true);
		addPair("", "purchase-product-id", productSku, true);
		addPair("", "purchase-quantity", quantity, true);
	}

	// Only to be used for creating custom purchase events
	public Event(String orderId, String productSku, int quantity, int priceInCents, String currencyCode) {
		this("", false);
		isTransaction = true;
		this.productSku = productSku; 

		addPair("", "purchase-transaction-id", orderId, true);
		addPair("", "purchase-product-id", productSku, true);
		addPair("", "purchase-quantity", quantity, true);
		addPair("", "purchase-price", priceInCents, true);
		addPair("", "purchase-currency", currencyCode, true);
	}

	// Only to be used for creating IAB purchase events
	public Event(String purchaseDataJson, String skuDetailsJson, String signature) throws JSONException {
		this("", false);
		isTransaction = true;
		
		JSONObject skuDetails = new JSONObject(skuDetailsJson);
		JSONObject purchase = new JSONObject(purchaseDataJson);
		
		productSku = purchase.getString("productId");
		String orderId = purchase.getString("orderId");

		try {
			String currencyCode = skuDetails.getString("price_currency_code");
			int priceMicros = skuDetails.getInt("price_amount_micros");
			int priceCenti = (int)Math.round(priceMicros / 10000.0);

			addPair("", "purchase-transaction-id", orderId, true);
			addPair("", "purchase-product-id", productSku, true);
			addPair("", "purchase-quantity", 1, true);
			addPair("", "purchase-price", priceCenti, true);
			addPair("", "purchase-currency", currencyCode, true);
			
		} catch (JSONException e) {
			// Older versions of the Google Play Store app don't send the currency and amount separately
			addPair("", "purchase-transaction-id", orderId, true);
			addPair("", "purchase-product-id", productSku, true);
			addPair("", "purchase-quantity", 1, true);
		}
		
		JSONObject receipt = new JSONObject();
		receipt.put("purchase_data", purchaseDataJson);
		receipt.put("signature", signature);
		
		addPair("", "receipt-body", receipt.toString(), false);
	}

	public void addPair(String key, Object value) {
		customFields.put(key, value);
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
		return postData != null ? postData.toString() : "";
	}

	void prepare(final Map<String, Object> globalEventParams) {
		// Only record the time of the first fire attempt
		if (firstFiredTime == 0) {
			firstFiredTime = System.currentTimeMillis();
			
			for(Map.Entry<String, Object> entry : globalEventParams.entrySet()) {
				if(!customFields.containsKey(entry.getKey())) {
					customFields.put(entry.getKey(), entry.getValue());
				}
			}

			postData.append(String.format(Locale.US, "&created-ms=%.0f", firstFiredTime));
			
			for(Map.Entry<String, Object> entry : customFields.entrySet()) {
				addPair("custom-", entry.getKey(), entry.getValue(), true);
			}
		}
	}

	boolean isTransaction() {
		return isTransaction;
	}

	void setName(String name) {
		this.name = name.toLowerCase().trim().replace(".", "_");
		try {
			encodedName = URLEncoder.encode(this.name, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	void setNamePrefix(String appName) {
		setName(String.format(Locale.US, "android-%s-purchase-%s", appName, productSku));
	}

	private String makeUid() {
		return String.format(Locale.US, "%d:%f", System.currentTimeMillis(), Math.random());
	}
	
	protected void addPair(String prefix, String key, Object value, boolean limitValueLength) {
        String encodedPair = Utils.encodeEventPair(prefix, key, value, limitValueLength);
        if(encodedPair != null) {
	        postData.append("&");
	        postData.append(encodedPair);
        }
	}
};