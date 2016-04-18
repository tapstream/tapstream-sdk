package com.tapstream.sdk.errors;


public class EventAlreadyFiredException extends ApiException {

    public EventAlreadyFiredException() {
    }

    public EventAlreadyFiredException(String message) {
        super(message);
    }

    public EventAlreadyFiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventAlreadyFiredException(Throwable cause) {
        super(cause);
    }

    public EventAlreadyFiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
