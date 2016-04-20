package com.tapstream.sdk.errors;


public class RetriesExhaustedException extends ApiException {

    public RetriesExhaustedException() {
    }

    public RetriesExhaustedException(String message) {
        super(message);
    }

    public RetriesExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetriesExhaustedException(Throwable cause) {
        super(cause);
    }

    public RetriesExhaustedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
