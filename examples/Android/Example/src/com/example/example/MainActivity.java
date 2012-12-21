package com.example.example;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import com.tapstream.sdk.*;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Tapstream.create(getApplicationContext(), "sdktest", "YGP2pezGTI6ec48uti4o1w");
		
		Tapstream tracker = Tapstream.getInstance();

		Event e = new Event("test-event", false);
        e.addPair("player", "John Doe");
        e.addPair("score", 5);
        tracker.fireEvent(e);

        e = new Event("test-event-oto", true);
        tracker.fireEvent(e);
		
        Hit h = new Hit("test-java");
        h.addTag("tag1");
        h.addTag("tag2");
        tracker.fireHit(h, new Hit.CompletionHandler() {
			@Override
			public void complete(Response response) {
				if(response.status >= 200 && response.status < 300) {
                    // Success
					Log.i("tag", "success");
                } else {
                    // Error
                	Log.e("tag", response.message);
                }
			}
		});
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
