package com.tapstream.example;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.tapstream.sdk.Config;
import com.tapstream.sdk.ConversionListener;
import com.tapstream.sdk.Event;
import com.tapstream.sdk.PurchaseEvent;
import com.tapstream.sdk.Tapstream;

public class MainActivity extends Activity {
	private static final String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyM2jltjS4SJofPAy7inRNNvXUh7pBL/hEtdomzyxT3s3di0kiIMaZeqS0HERpH3Ps2zAkmYSIqq7nCfFJkQ5kaT85jMBBUDBrzo97IdS8eCa5/+Yx+bk80R1RXDdrT/jXOWdhO2tUO5gxy0mtFMxMZm5XSq3AIiAHOGZHpULWZ3BWSoUKRKMHUU7GPN2YpxY/lEikXfb0OO8KUL3oK5aUBy57qr4tP0zzMkoCDWZgLWt2RXbudrS1B4muyNtYkydbRphUnO5c1hBob+U1nNOEHKe231PN9vqjcincggKHsGFiearwyI2DYd6xRUa0QULMzZSlTnLVAtPoWXlbZLXUQIDAQAB";
	protected static final String TAG = "MainActivity";
	
	private static final String SKU = "android.test.purchased";
	//private static final String SKU = "com.tapstream.catalog.tiddlywinks";
	
	private IabHelper mHelper;
	private IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
	private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
	private Inventory mInventory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Config config = new Config();
		
		config.globalEventParams.put("locale", "ENU");
		config.globalEventParams.put("user_id", "92429d82a41e");
		
		config.setOdin1("TestODINValue");
		config.setConversionListener(new ConversionListener() {
			@Override
			public void conversionInfo(String jsonInfo) {
				try {
					JSONArray obj = new JSONArray(jsonInfo);
					// Read some data from this json object, and modify your application's behaviour accordingly
					// ...
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
		Tapstream.create(getApplication(), "sdktest", "YGP2pezGTI6ec48uti4o1w", config);
				
		final Tapstream tracker = Tapstream.getInstance();

		Event e = new Event("test-event", false);
		e.addPair("player", "John Doe");
		e.addPair("score", 5);
		tracker.fireEvent(e);

		e = new Event("test-event-oto", true);
		tracker.fireEvent(e);
		
		final Activity self = this;
		
		mHelper = new IabHelper(this, pubKey);

		mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
			public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
				if (result.isFailure()) {
					Log.d(TAG, "QueryInventory failed: " + result);
				} else {
					
					// Important:
					// Store the inventory in a member variable so we will have access to this data
					// later when a purchase succeeds.
					mInventory = inventory;
					
					// Buy the first item we can find (this is just an example, of course)
					Iterator<Map.Entry<String, SkuDetails>> iter = inventory.mSkuMap.entrySet().iterator();
					if (iter.hasNext()) {
						Map.Entry<String, SkuDetails> entry = iter.next();
						String sku = entry.getKey();
						
						if (inventory.hasPurchase(sku)) {
				            mHelper.consumeAsync(inventory.getPurchase(sku), null);
				        }
						
						mHelper.launchPurchaseFlow(self, sku, 10001, mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
					}
				}
			}
		};
		
		mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
			public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
				if (result.isFailure()) {
					Log.d(TAG, "IabPurchase failed: " + result);
				} else {
					// Purchase success, fire a Tapstream purchase event.
					// We need to provide the product details json that we got when we first queried our inventory.
					// Fortunately, we stored the Inventory instance in a member variable so we can still access it.
					SkuDetails skuDetails = mInventory.mSkuMap.get(purchase.getSku());
					try {
						JSONObject purchaseJson = new JSONObject(purchase.mOriginalJson);
						JSONObject skuDetailsJson = new JSONObject(skuDetails.mJson);			
						PurchaseEvent event = new PurchaseEvent(purchaseJson, skuDetailsJson);
						Tapstream.getInstance().fireEvent(event);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (result.isFailure()) {
					Log.d(TAG, "IabSetup failed: " + result);
				} else {
					mHelper.queryInventoryAsync(true, Arrays.asList(new String[] {SKU}), mGotInventoryListener);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

	    // Pass on the activity result to the helper for handling
	    if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
	        // not handled, so handle it ourselves (here's where you'd
	        // perform any handling of activity results not related to in-app
	        // billing...
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	    else {
	        Log.d(TAG, "onActivityResult handled by IABUtil.");
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onStart() {
		Log.i("Test", "onStart");
		super.onStart();
	}
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}
}
