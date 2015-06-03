package com.tapstream.sdk;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;

import com.tapstream.sdk.Hit.CompletionHandler;
import com.tapstream.sdk.wordofmouth.WordOfMouth;
import com.tapstream.sdk.wordofmouth.WordOfMouthImpl;

public class Tapstream implements Api {
	private static Tapstream instance;
	private Map<String, WordOfMouth> womInstances = new HashMap<String, WordOfMouth>();

	private Delegate delegate;
	private Platform platform;
	private CoreListener listener;
	private Core core;

	private String secret;

	private static class DelegateImpl implements Delegate {
		Core core;
		public void init(Core core){
			this.core = core;
		}
		public int getDelay() {
			return core.getDelay();
		}

		public void setDelay(int delay) {
		}

		public boolean isRetryAllowed() {
			return true;
		}
	}

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
					new DelegateImpl(),
					new PlatformImpl(app),
					new CoreListenerImpl(),
					aes,
					new AdvertisingIdFetcher(app),
					accountName, developerSecret, config
				);
			} else {
				Logging.log(Logging.WARN, "Tapstream Warning: Tapstream already instantiated, it cannot be re-created.");
			}
		}
	}

	public static Tapstream getInstance() {
		synchronized (Tapstream.class) {
			if (instance == null) {
				throw new RuntimeException("You must first call Tapstream.create");
			}
			return instance;
		}
	}

	Tapstream(Delegate delegate, Platform platform, CoreListener listener, ActivityEventSource aes,
			  AdvertisingIdFetcher aif, String accountName, String developerSecret, Config config){
		this.delegate = delegate;
		this.platform = platform;
		this.listener = listener;
		this.secret = developerSecret;
		this.core = new Core(delegate, platform, listener, aes, aif, accountName, developerSecret, config);
		core.start();
	}

	public void fireEvent(Event e) {
		core.fireEvent(e);
	}

	public void fireHit(Hit h, CompletionHandler completion) {
		core.fireHit(h, completion);
	}
	
	public void getConversionData(ConversionListener completion) {
		core.getConversionData(completion);
	}

	public WordOfMouth getWordOfMouth(){
		return getWordOfMouth(platform.getPackageName());
	}
	public WordOfMouth getWordOfMouth(String bundle){
		WordOfMouth womInstance = womInstances.get(bundle);
		if(womInstance == null) {
			womInstance = WordOfMouthImpl.getInstance(core, platform, secret, bundle);
			womInstances.put(bundle, womInstance);
		}
		return womInstance;
	}
}
