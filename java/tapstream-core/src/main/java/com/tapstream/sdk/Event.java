package com.tapstream.sdk;

import com.tapstream.sdk.http.FormPostBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Event {

	public static class Params {

		private final Map<String, String> params = new HashMap<String, String>();

		public void put(String key, long value){
			put(key, Long.toString(value));
		}

		public void put(String key, int value){
			put(key, Integer.toString(value));
		}

		public void put(String key, String value){
			put(key, value, true);
		}

		public void putWithoutTruncation(String key, String value){
			put(key, value, false);
		}

		private void put(String key, String value, boolean limitValueLength){
			if(key == null || value == null) {
				return;
			}

			if (key.length() > 255) {
				Logging.log(Logging.WARN, "key exceeds 255 characters, " +
						"this field will not be included in the post (key=%s)", key);
				return;
			}

			if (limitValueLength && value.length() > 255) {
				Logging.log(Logging.WARN, "value exceeds 255 characters, " +
						"this field will not be included in the post (value=%s)", value);
				return;
			}

			params.put(key, value);
		}

		public Map<String, String> toMap(){
			return params;
		}

	}


	// Purchase field names
	public static final String PURCHASE_TRANSACTION_ID = "purchase-transaction-id";
	public static final String PURCHASE_PRODUCT_ID = "purchase-product-id";
	public static final String PURCHASE_QUANTITY = "purchase-quantity";
	public static final String PURCHASE_PRICE = "purchase-price";
	public static final String PURCHASE_CURRENCY = "purchase-currency";
	public static final String RECEIPT_BODY = "receipt-body";

	private final Params params = new Params();
	private final Params customParams = new Params();

	private String name;
	private final boolean oneTimeOnly;
	private final boolean isTransaction;
	private String productSku;

	/**
	 * The default event constructor. This is what you want most of the time.
	 *
	 * @param name			this event's name.
	 * @param oneTimeOnly	true if this device should only fire an event with {@code name}.
     */
	public Event(String name, boolean oneTimeOnly) {
		this.name = name;
		this.oneTimeOnly = oneTimeOnly;
		this.isTransaction = false;
	}

	/**
	 * Only to be used for creating custom purchase events.
	 *
	 * @param orderId
	 * @param productSku
	 * @param quantity
     */
	public Event(String orderId, String productSku, int quantity) {
		this.name = "";
		this.oneTimeOnly = false;
		this.isTransaction = true;
		this.productSku = productSku;
		params.put(PURCHASE_TRANSACTION_ID, orderId);
		params.put(PURCHASE_PRODUCT_ID, productSku);
		params.put(PURCHASE_QUANTITY, quantity);
	}

	/**
	 * Only to be used for creating custom purchase events.
	 *
	 * @param orderId
	 * @param productSku
	 * @param quantity
	 * @param priceInCents
     * @param currencyCode
     */
	public Event(String orderId, String productSku, int quantity, int priceInCents, String currencyCode) {
		this.name = "";
		this.oneTimeOnly = false;
		this.isTransaction = true;
		this.productSku = productSku;
		params.put(PURCHASE_TRANSACTION_ID, orderId);
		params.put(PURCHASE_PRODUCT_ID, productSku);
		params.put(PURCHASE_QUANTITY, quantity);
		params.put(PURCHASE_PRICE, priceInCents);
		params.put(PURCHASE_CURRENCY, currencyCode);
	}

	/**
	 * Only to be used for creating IAB purchase events from PlayStore JSON.
	 *
	 * @param purchaseDataJson
	 * @param skuDetailsJson
	 * @param signature
	 * @throws JSONException
     */
	public Event(String purchaseDataJson, String skuDetailsJson, String signature) throws JSONException {
		this.name = "";
		this.oneTimeOnly = false;
		this.isTransaction = true;

		JSONObject skuDetails = new JSONObject(skuDetailsJson);
		JSONObject purchase = new JSONObject(purchaseDataJson);
		
		productSku = purchase.getString("productId");
		String orderId = purchase.getString("orderId");

		if (skuDetails.has("price_currency_code") && skuDetails.has("price_amount_micros")){
			String currencyCode = skuDetails.getString("price_currency_code");
			int priceMicros = skuDetails.getInt("price_amount_micros");
			int priceInCents = (int)Math.round(priceMicros / 10000.0);
			params.put(PURCHASE_TRANSACTION_ID, orderId);
			params.put(PURCHASE_PRODUCT_ID, productSku);
			params.put(PURCHASE_QUANTITY, 1);
			params.put(PURCHASE_PRICE, priceInCents);
			params.put(PURCHASE_CURRENCY, currencyCode);
		} else {
			// Older versions of the Google Play Store app don't send the currency and amount separately
			params.put(PURCHASE_TRANSACTION_ID, orderId);
			params.put(PURCHASE_PRODUCT_ID, productSku);
			params.put(PURCHASE_QUANTITY, 1);
		}

		JSONObject receipt = new JSONObject();
		receipt.put("purchase_data", purchaseDataJson);
		receipt.put("signature", signature);
		params.putWithoutTruncation(RECEIPT_BODY, receipt.toString());
	}

	/**
	 * Set a custom parameter for this event.
	 * @param key	parameter key
	 * @param value	parameter value
     */
	public void setCustomParameter(String key, String value){
		this.customParams.put(key, value);
	}

	/**
	 * Gets the name for this event. This corresponds to the TimelineEvent slug that you can interact with
	 * via the Tapstream dashboard API.
	 * @return
     */
	public String getName() {
		return name;
	}

	/**
	 * One time only flag.
	 * @return true if this device should only fire an event with this event's name at most once.
     */
	public boolean isOneTimeOnly() {
		return oneTimeOnly;
	}

	void setNameForPurchase(String appName) {
		name = String.format(Locale.US, "android-%s-purchase-%s", appName, productSku);
	}

	void prepare(String appName) {
		if (isTransaction){
			setNameForPurchase(appName);
		}
	}

	FormPostBody buildPostBody(final Params commonParams, final Params globalCustomParams){
		final FormPostBody body = new FormPostBody();

		body.add(commonParams.toMap());
		body.add(params.toMap());

		if (globalCustomParams != null){
			for(Map.Entry<String, String> entry : globalCustomParams.toMap().entrySet()) {
				body.add("custom-" + entry.getKey(), entry.getValue());
			}
		}

		if (customParams != null){
			for (Map.Entry<String, String> entry: customParams.toMap().entrySet()){
				body.add("custom-" + entry.getKey(), entry.getValue());
			}
		}

		body.add("created-ms", Long.toString(System.currentTimeMillis()));

		return body;
	}

	Params getParams(){
		return params;
	}

	Params getCustomParams(){
		return customParams;
	}

	boolean isTransaction(){
		return isTransaction;
	}

	String getProductSku(){
		return productSku;
	}
};