package com.tapstream.sdk;


import com.tapstream.sdk.http.HttpResponse;

public class EventApiResponse implements ApiResponse{

    private final HttpResponse response;

    public EventApiResponse(HttpResponse response){
        this.response = response;
    }

    @Override
    public HttpResponse getHttpResponse() {
        return response;
    }
}
