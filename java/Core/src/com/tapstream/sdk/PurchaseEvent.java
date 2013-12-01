package com.tapstream.sdk;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseEvent extends Event {
	private String productSku;

	public PurchaseEvent(String orderId, String productSku, int quantity)
	{
		super(String.format(Locale.US, "purchase-%s", productSku), false);
		Initialize(orderId, productSku, quantity);
	}

	public PurchaseEvent(String orderId, String productSku, int quantity, int priceInCents, String currencyCode)
	{
		super(String.format(Locale.US, "purchase-%s", productSku), false);
		Initialize(orderId, productSku, quantity, priceInCents, currencyCode);
	}

	public PurchaseEvent(JSONObject purchase, JSONObject skuDetails) throws JSONException
	{
		super("", false);
		productSku = purchase.getString("productId");
		String orderId = purchase.getString("orderId");

		try
		{
			String currencyCode = skuDetails.getString("price_currency_code");
			int priceMicros = skuDetails.getInt("price_amount_micros");
			int priceCenti = (int)Math.round(priceMicros / 10000.0);
			Initialize(orderId, productSku, 1, priceCenti, currencyCode);
		}
		catch (JSONException e)
		{
			// Older versions of the Google Play Store app don't send the currency and amount separately
			Initialize(orderId, productSku, 1);
		}
	}

	void SetNamePrefix(String appName)
	{
		setName(String.format(Locale.US, "android-%s-purchase-%s", appName, productSku));
	}

	private void Initialize(String orderId, String productSku, int quantity)
	{
		addPair("", "purchase-transaction-id", orderId);
		addPair("", "purchase-product-id", productSku);
		addPair("", "purchase-quantity", quantity);
	}

	private void Initialize(String orderId, String productSku, int quantity, int priceInCents, String currencyCode)
	{
		Initialize(orderId, productSku, quantity);
		addPair("", "purchase-price", priceInCents);
		addPair("", "purchase-currency", currencyCode);
	}
};
