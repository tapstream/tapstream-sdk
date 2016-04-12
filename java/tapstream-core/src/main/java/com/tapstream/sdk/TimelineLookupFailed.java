package com.tapstream.sdk;


public class TimelineLookupFailed extends Exception {

    public TimelineLookupFailed() {
    }

    public TimelineLookupFailed(String message) {
        super(message);
    }

    public TimelineLookupFailed(String message, Throwable cause) {
        super(message, cause);
    }

    public TimelineLookupFailed(Throwable cause) {
        super(cause);
    }

    public TimelineLookupFailed(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
