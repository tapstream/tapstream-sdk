package com.tapstream.sdk;

public class Logging {
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;

	public interface Logger {
		void log(int logLevel, String msg);
	}

	private static class DefaultLogger implements Logger {
		@Override
		public void log(int logLevel, String msg) {
			System.out.println(msg);
		}
	}

	private static Logger logger = new DefaultLogger();

	synchronized public static void setLogger(Logger logger) {
		Logging.logger = logger;
	}

	synchronized public static void log(int logLevel, String format, Object... args) {
		if (logger != null && format != null) {
			try {
				String msg = format == null ? null : String.format(format, args);
				logger.log(logLevel, msg);
			} catch (Exception e){
				logger.log(ERROR, "Unhandled exception in the logging system. " +
						"This should never happen.");
			}

		}
	}


}
