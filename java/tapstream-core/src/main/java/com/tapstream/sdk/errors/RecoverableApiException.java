package com.tapstream.sdk.errors;

import com.tapstream.sdk.http.HttpResponse;

/**
 * Created by adam on 2016-04-20.
 */


public class RecoverableApiException extends ApiException {

    final private HttpResponse response;

    public RecoverableApiException(HttpResponse response) {
        this.response = response;
    }

    public RecoverableApiException(HttpResponse response, String message) {
        super(message);
        this.response = response;
    }

    public RecoverableApiException(HttpResponse response, String message, Throwable cause) {
        super(message, cause);
        this.response = response;
    }

    public RecoverableApiException(HttpResponse response, Throwable cause) {
        super(cause);
        this.response = response;
    }

    public RecoverableApiException(HttpResponse response, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.response = response;
    }

    public HttpResponse getHttpResponse(){
        return response;
    }
}
