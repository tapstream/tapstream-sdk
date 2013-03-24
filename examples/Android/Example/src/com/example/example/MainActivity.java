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

		Config config = new Config();
		config.setOdin1("TestODINValue");
		Tapstream.create(getApplicationContext(), "sdktest", "YGP2pezGTI6ec48uti4o1w", config);
		
		Tapstream tracker = Tapstream.getInstance();

		Event e = new Event("test-event", false);
        e.addPair("player", "John Doe");
        e.addPair("score", 5);
        tracker.fireEvent(e);

        e = new Event("test-event-oto", true);
        tracker.fireEvent(e);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
