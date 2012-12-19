package com.tapstream.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Core {
	public static final String VERSION = "2.0";
	private static final String EVENT_URL_TEMPLATE = "https://api.tapstream.com/%s/event/%s/";
	private static final String HIT_URL_TEMPLATE = "http://api.tapstream.com/%s/hit/%s.gif";
	private static final int MAX_THREADS = 1;

	private Delegate delegate;
	private Platform platform;
	private CoreListener listener;
	private String accountName;
	private ScheduledThreadPoolExecutor executor;
	private StringBuilder postData = null;
	private Set<String> firingEvents = new HashSet<String>(16);
	private Set<String> firedEvents = new HashSet<String>(16);
	private String failingEventId = null;
	private int delay = 0;

	Core(Delegate delegate, Platform platform, CoreListener listener, String accountName, String developerSecret, String hardware) {
		this.delegate = delegate;
		this.platform = platform;
		this.listener = listener;

		this.accountName = clean(accountName);
		makePostArgs(developerSecret, hardware);

		firedEvents = platform.loadFiredEvents();

		executor = new ScheduledThreadPoolExecutor(MAX_THREADS, platform.makeWorkerThreadFactory());
		executor.prestartAllCoreThreads();
	}

	public synchronized void fireEvent(final Event e) {
		// Notify the event that we are going to fire it so it can record the
		// time
		e.firing();

		if (e.isOneTimeOnly()) {
			if (firedEvents.contains(e.getName())) {
				Logging.log(Logging.INFO, "ConversionTracker ignoring event named \"%s\" because it is a one-time-only event that has already been fired", e.getName());
				listener.reportOperation("event-ignored-already-fired");
				listener.reportOperation("job-ended");
				return;
			} else if (firingEvents.contains(e.getName())) {
				Logging.log(Logging.INFO, "ConversionTracker ignoring event named \"%s\" because it is a one-time-only event that is already in progress", e.getName());
				listener.reportOperation("event-ignored-already-in-progress");
				listener.reportOperation("job-ended");
				return;
			}

			firingEvents.add(e.getName());
		}

		final Core self = this;
		final String url = String.format(EVENT_URL_TEMPLATE, accountName, e.getEncodedName());
		final String data = postData.toString() + e.getPostData();

		Runnable task = new Runnable() {
			public void innerRun() {
				Response response = platform.request(url, data);
				boolean failed = response.status < 200 || response.status >= 300;
				boolean shouldRetry = response.status < 0 || (response.status >= 500 && response.status < 600);

				synchronized (self) {
					if (e.isOneTimeOnly()) {
						self.firingEvents.remove(e.getName());
					}

					if (failed) {
						// Only increase delays if we actually intend to retry
						// the event
						if (shouldRetry) {
							// Not every job that fails will increase the retry
							// delay. It will be the responsibility of
							// the first failed job to increase the delay after
							// every failure.
							if (self.delay == 0) {
								// This is the first job to fail, it must be the
								// one to manage delay timing
								self.failingEventId = e.getUid();
								self.increaseDelay();
							} else if (self.failingEventId == e.getUid()) {
								// This job is failing for a subsequent time
								self.increaseDelay();
							}
						}
					} else {
						if (e.isOneTimeOnly()) {
							self.firedEvents.add(e.getName());

							self.platform.saveFiredEvents(self.firedEvents);
							self.listener.reportOperation("fired-list-saved", e.getUid());
						}

						// Success of any event resets the delay
						self.delay = 0;
					}
				}

				if (failed) {
					if (response.status < 0) {
						Logging.log(Logging.ERROR, "ConversionTracker Error: Failed to fire event, error=%s", response.message);
					} else if (response.status == 404) {
						Logging.log(Logging.ERROR, "ConversionTracker Error: Failed to fire event, http code %d\nDoes your event name contain characters that are not url safe? This event will not be retried.", response.status);
					} else if (response.status == 403) {
						Logging.log(Logging.ERROR, "ConversionTracker Error: Failed to fire event, http code %d\nAre your account name and application secret correct?  This event will not be retried.", response.status);
					} else {
						String retryMsg = "";
						if (!shouldRetry) {
							retryMsg = "  This event will not be retried.";
						}
						Logging.log(Logging.ERROR, "ConversionTracker Error: Failed to fire event, http code %d.%s", response.status, retryMsg);
					}

					self.listener.reportOperation("event-failed", e.getUid());
					if (shouldRetry) {
						self.listener.reportOperation("retry", e.getUid());
						self.listener.reportOperation("job-ended");
						if (self.delegate.isRetryAllowed()) {
							self.fireEvent(e);
						}
						return;
					}
				} else {
					Logging.log(Logging.INFO, "ConversionTracker fired event named \"%s\"", e.getName());
					self.listener.reportOperation("event-succeeded");
				}

				self.listener.reportOperation("job-ended");
			}
			public void run() {
				try {
					innerRun();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		// Always ask the delegate what the delay should be, regardless of what
		// our delay member says.
		// The delegate may wish to override it if this is a testing scenario.
		int delay = delegate.getDelay();
		executor.schedule(task, delay, TimeUnit.SECONDS);
	}

	public void fireHit(final Hit h, final Hit.CompletionHandler completion) {
		final String url = String.format(HIT_URL_TEMPLATE, accountName, h.getEncodedTrackerName());
		final String data = h.getPostData();
		Runnable task = new Runnable() {
			public void run() {
				Response response = platform.request(url, data);
				if (response.status < 200 || response.status >= 300) {
					Logging.log(Logging.ERROR, "ConversionTracker Error: Failed to fire hit, http code: %d", response.status);
					listener.reportOperation("hit-failed");
				} else {
					Logging.log(Logging.INFO, "ConversionTracker fired hit to tracker: %s", h.getTrackerName());
					listener.reportOperation("hit-succeeded");
				}
				if (completion != null) {
					completion.complete(response);
				}
			}
		};
		executor.schedule(task, 0, TimeUnit.SECONDS);
	}

	public String getPostData() {
		return postData.toString();
	}

	public int getDelay() {
		return delay;
	}

	private String clean(String s) {
		try {
			return URLEncoder.encode(s.toLowerCase().trim(), "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	private void increaseDelay() {
		if (delay == 0) {
			// First failure
			delay = 2;
		} else {
			// 2, 4, 8, 16, 32, 60, 60, 60...
			int newDelay = (int) Math.pow(2, Math.round(Math.log(delay) / Math.log(2)) + 1);
			delay = newDelay > 60 ? 60 : newDelay;
		}
		listener.reportOperation("increased-delay");
	}

	private void appendPostPair(String key, String value) {
		String encodedName = null;
		try {
			encodedName = URLEncoder.encode(key, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		String encodedValue = null;
		try {
			encodedValue = URLEncoder.encode(value, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		if (postData == null) {
			postData = new StringBuilder();
		} else {
			postData.append("&");
		}
		postData.append(encodedName);
		postData.append("=");
		postData.append(encodedValue);
	}

	private void makePostArgs(String secret, String hardware) {
		appendPostPair("secret", secret);
		appendPostPair("sdkversion", VERSION);
		if (hardware != null) {
			if (hardware.length() > 255) {
				Logging.log(Logging.WARN, "ConversionTracker Warning: Hardware argument exceeds 255 characters, it will not be included with fired events");
			} else {
				appendPostPair("hardware", hardware);
			}
		}

		appendPostPair("uuid", platform.loadUuid());
		appendPostPair("platform", "Android");
		appendPostPair("vendor", platform.getManufacturer());
		appendPostPair("model", platform.getModel());
		appendPostPair("os", platform.getOs());
		appendPostPair("resolution", platform.getResolution());
		appendPostPair("locale", platform.getLocale());

		int offsetFromUtc = TimeZone.getDefault().getOffset((new Date()).getTime()) / 1000;
		appendPostPair("gmtoffset", Integer.toString(offsetFromUtc));
	}
}
