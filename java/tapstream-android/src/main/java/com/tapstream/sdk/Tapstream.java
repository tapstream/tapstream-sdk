package com.tapstream.sdk;

import android.app.Application;

import com.tapstream.sdk.wordofmouth.WordOfMouth;
import com.tapstream.sdk.wordofmouth.WordOfMouthImpl;

import org.json.JSONObject;

import java.lang.reflect.Constructor;

public class Tapstream implements AndroidApi {
	private static Tapstream instance;
	private WordOfMouth wom;

	private Platform platform;
	private CoreListener listener;
	private Core core;
	private String secret;

	public static void create(Application app, String accountName, String developerSecret, Config config) {
		synchronized (Tapstream.class) {
			if (instance == null) {
				// Using reflection, try to instantiate the ActivityCallbacks class.  ActivityCallbacks
				// is derived from a class only available in api 14, so we expect this to fail for any
				// android version prior to 4.  For older android versions, a dummy implementation is used.
				ActivityEventSource aes;
				try {
					Class<?> cls = Class.forName("com.tapstream.sdk.api14.ActivityCallbacks");
					Constructor<?> constructor = cls.getConstructor(Application.class);
					aes = (ActivityEventSource)constructor.newInstance(app);
				} catch(Exception e) {
					aes = new ActivityEventSource();
				}

				instance = new Tapstream(
						new AndroidPlatform(app),
						aes,
						accountName,
						developerSecret,
						config
				);
			} else {
				Logging.log(Logging.WARN, "Tapstream Warning: Tapstream already instantiated, it cannot be re-created.");
			}
		}
	}

	synchronized public static Tapstream getInstance() {
		if (instance == null) {
			throw new RuntimeException("You must first call Tapstream.create");
		}
		return instance;
	}

	Tapstream(Platform platform, ActivityEventSource aes,
			  String accountName, String developerSecret, Config config){
		this.core = new Core(platform, aes, accountName, developerSecret, config);
		core.start();
	}

	@Override
	public void fireEvent(Event e) {
		core.fireEvent(e);
	}

	@Override
	public void getConversionData(Callback<JSONObject> completion) {
		core.getConversionData(completion);
	}

	@Override
	synchronized public WordOfMouth getWordOfMouth(){
		if(wom == null) {
			wom = WordOfMouthImpl.getInstance(core, platform, secret, platform.getPackageName());
		}
		return wom;
	}
}
