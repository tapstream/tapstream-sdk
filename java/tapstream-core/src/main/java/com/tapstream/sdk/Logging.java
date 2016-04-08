package com.tapstream.sdk;

import java.lang.reflect.Method;

public class Logging {
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;

	private static class DefaultLogger implements Logger {
		@Override
		public void log(int logLevel, String msg) {
			System.out.println(msg);
		}
	}

	private static Logger logger = new DefaultLogger();
	private static Method formatMethod;

	static {
		try {
			formatMethod = String.class.getDeclaredMethod("format", String.class, Object[].class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void setLogger(Logger logger) {
		Logging.logger = logger;
	}

	public static synchronized void log(int logLevel, String format, Object... args) {
		if (logger != null) {
			String msg = "";
			try {
				msg = (String) formatMethod.invoke(null, new Object[] { format, args });
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.log(logLevel, msg);
		}
	}
}
