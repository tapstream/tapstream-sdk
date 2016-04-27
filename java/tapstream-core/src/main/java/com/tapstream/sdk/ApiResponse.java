package com.tapstream.sdk;


import com.tapstream.sdk.http.HttpResponse;

public interface ApiResponse {

    /**
     * Returns the {@link HttpResponse} this instance was created from.
     *
     * @return the associated http response.
     */
    HttpResponse getHttpResponse();
}
