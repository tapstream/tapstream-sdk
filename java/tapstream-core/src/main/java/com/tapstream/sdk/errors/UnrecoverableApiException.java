package com.tapstream.sdk.errors;


import com.tapstream.sdk.http.HttpResponse;

public class UnrecoverableApiException extends ApiException {

    final private HttpResponse response;

    public UnrecoverableApiException(HttpResponse response) {
        this.response = response;
    }

    public UnrecoverableApiException(HttpResponse response, String message) {
        super(message);
        this.response = response;
    }

    public UnrecoverableApiException(HttpResponse response, String message, Throwable cause) {
        super(message, cause);
        this.response = response;
    }

    public UnrecoverableApiException(HttpResponse response, Throwable cause) {
        super(cause);
        this.response = response;
    }

    public UnrecoverableApiException(HttpResponse response, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.response = response;
    }

    public HttpResponse getHttpResponse(){
        return response;
    }
}
