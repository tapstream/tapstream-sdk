package com.tapstream.sdk;

import java.lang.reflect.Constructor;

import android.app.Application;

import com.tapstream.sdk.Hit.CompletionHandler;

public class Tapstream implements Api {
	private static Tapstream instance;

	public static void create(Application app, String accountName, String developerSecret, Config config) {
		synchronized (Tapstream.class) {
			if (instance == null) {
				instance = new Tapstream(app, accountName, developerSecret, config);
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

	private class DelegateImpl implements Delegate {
		public int getDelay() {
			return core.getDelay();
		}

		public void setDelay(int delay) {
		}

		public boolean isRetryAllowed() {
			return true;
		}
	}

	private Delegate delegate;
	private Platform platform;
	private CoreListener listener;
	private Core core;

	private Tapstream(Application app, String accountName, String developerSecret, Config config) {
		delegate = new DelegateImpl();
		platform = new PlatformImpl(app);
		listener = new CoreListenerImpl();
				
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
		
		core = new Core(delegate, platform, listener, aes, new AdvertisingIdFetcher(app), accountName, developerSecret, config);
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
}
