package com.tapstream.sdk;

import com.google.common.collect.ImmutableMap;
import com.tapstream.sdk.http.FormPostBody;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


public class TestEvent {

    @Test
    public void testStandardCtor() throws Exception {
        Event event = new Event("eventName", false);
        assertThat(event.getName(), is("eventName"));
        assertThat(event.isTransaction(), is(false));
        assertThat(event.isOneTimeOnly(), is(false));
        assertThat(event.getCustomParams().toMap().isEmpty(), is(true));
        assertThat(event.getParams().toMap().isEmpty(), is(true));
        assertThat(event.getProductSku(), is(nullValue()));
    }

    @Test
    public void testPurchaseNoPriceCtor() throws Exception {
        String orderId = "order123abc";
        String sku = "com.product.sku";
        int qty = 2;

        Event event = new Event(orderId, sku, qty);
        assertThat(event.getName(), is(""));
        assertThat(event.isTransaction(), is(true));
        assertThat(event.isOneTimeOnly(), is(false));
        assertThat(event.getCustomParams().toMap().isEmpty(), is(true));

        Map<String, String> expectedParams = ImmutableMap.of(
                Event.PURCHASE_TRANSACTION_ID, orderId,
                Event.PURCHASE_PRODUCT_ID, sku,
                Event.PURCHASE_QUANTITY, Integer.toString(qty)
        );

        assertThat(event.getParams().toMap(), is(expectedParams));
    }

    @Test
    public void testPurchaseWithPriceCtor() throws Exception {
        String orderId = "order123abc";
        String sku = "com.product.sku";
        int qty = 2;
        int priceInCents = 299;
        String currency = "USD";

        Event event = new Event(orderId, sku, qty, priceInCents, currency);
        assertThat(event.getName(), is(""));
        assertThat(event.isTransaction(), is(true));
        assertThat(event.isOneTimeOnly(), is(false));
        assertThat(event.getCustomParams().toMap().isEmpty(), is(true));

        Map<String, String> expectedParams = ImmutableMap.of(
                Event.PURCHASE_TRANSACTION_ID, orderId,
                Event.PURCHASE_PRODUCT_ID, sku,
                Event.PURCHASE_QUANTITY, Integer.toString(qty),
                Event.PURCHASE_PRICE, Integer.toString(priceInCents),
                Event.PURCHASE_CURRENCY, currency
        );

        assertThat(event.getParams().toMap(), is(expectedParams));
    }

    @Test
    public void testPurchaseWithJsonCtor() throws Exception {
        String purchaseJson = "{\"orderId\": \"order123abc\", \"productId\": \"com.product.sku\"}";
        String skuJson = "{\"productId\": \"com.product.sku\", \"type\": \"inapp\", \"price\": \"$2.99\", \"title\": \"Gold Coins\", " +
                "\"Description\": \"Coins to buy stuff with\", \"price_amount_micros\": 2990000, \"price_currency_code\": \"USD\"}";
        String signature = "theSignature";

        String orderId = "order123abc";
        String sku = "com.product.sku";
        int qty = 1;
        int priceInCents = 299;
        String currency = "USD";

        Event event = new Event(purchaseJson, skuJson, signature);
        assertThat(event.getName(), is(""));
        assertThat(event.isTransaction(), is(true));
        assertThat(event.isOneTimeOnly(), is(false));
        assertThat(event.getCustomParams().toMap().isEmpty(), is(true));

        Map<String, String> expectedParams = new HashMap<String, String>();
        expectedParams.put(Event.PURCHASE_TRANSACTION_ID, orderId);
        expectedParams.put(Event.PURCHASE_PRODUCT_ID, sku);
        expectedParams.put(Event.PURCHASE_QUANTITY, Integer.toString(qty));
        expectedParams.put(Event.PURCHASE_PRICE, Integer.toString(priceInCents));
        expectedParams.put(Event.PURCHASE_CURRENCY, currency);
        expectedParams.put(Event.RECEIPT_BODY, "{\"signature\":\"theSignature\",\"purchase_data\":\"{\\\"orderId\\\": \\\"order123abc\\\", \\\"productId\\\": \\\"com.product.sku\\\"}\"}");
        assertThat(event.getParams().toMap(), is(expectedParams));
    }

    @Test
    public void testSetCustomParameter() throws Exception {
        Event event = new Event("name", false);
        event.setCustomParameter("paramName", "paramValue");
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("paramName", "paramValue");
        assertThat(event.getCustomParams().toMap(), is(expected));
    }

    @Test
    public void testGetName() throws Exception {
        String expected = "theName";
        Event event = new Event(expected, false);
        assertThat(event.getName(), is(expected));
    }

    @Test
    public void testIsOneTimeOnly() throws Exception {
        Event trueEvent = new Event("name", true);
        assertThat(trueEvent.isOneTimeOnly(), is(true));
        Event falseEvent = new Event("name", false);
        assertThat(falseEvent.isOneTimeOnly(), is(false));
    }

    @Test
    public void testSetNameForPurchase() throws Exception {
        String appName = "appName";
        Event event = new Event("orderId", "sku", 1);
        event.setNameForPurchase(appName);
        assertThat(event.getName(), is("android-" + appName + "-purchase-sku"));
    }

    @Test
    public void testPrepare() throws Exception {
        String appName = "appName";

        Event event = spy(new Event("name", false));
        event.prepare(appName);
        verify(event, never()).setNameForPurchase(appName);

        Event purchase = spy(new Event("orderId", "sku", 1));
        purchase.prepare(appName);
        verify(purchase).setNameForPurchase(appName);
    }

    @Test
    public void testBuildPostBody() throws Exception {
        String orderId = "orderId";
        String sku = "sku";
        int qty = 1;

        Event event = new Event(orderId, sku, qty);
        event.setCustomParameter("param1", "value1");
        event.setCustomParameter("param2", "value2");

        Event.Params commonParams = new Event.Params();
        commonParams.put("common1", "commonValue1");

        Event.Params globalCustomParams = new Event.Params();
        globalCustomParams.put("global1", "globalValue1");

        FormPostBody body = event.buildPostBody(commonParams, globalCustomParams);

        assertThat(body.getParams(), allOf(
                hasEntry("custom-param1", "value1"),
                hasEntry("custom-param2", "value2"),
                hasEntry("common1", "commonValue1"),
                hasEntry("custom-global1", "globalValue1"),
                hasEntry(Event.PURCHASE_TRANSACTION_ID, orderId),
                hasEntry(Event.PURCHASE_PRODUCT_ID, sku),
                hasEntry(Event.PURCHASE_QUANTITY, Integer.toString(qty))
        ));

    }
}