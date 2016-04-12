package com.tapstream.sdk;


import com.tapstream.sdk.http.HttpMethod;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.URLEncoding;

public class RequestBuilders {

    public static final String DEFAULT_SCHEME = "https";
    public static final String API_TAPSTREAM_COM = "api.tapstream.com";
    public static final String REPORTING_TAPSTREAM_COM = "reporting.tapstream.com";

    public static HttpRequest.Builder eventRequestBuilder(String accountName, String eventName){

        final String path =
                "/"
                + URLEncoding.QUERY_STRING_ENCODER.encode(accountName)
                + "/event/"
                + URLEncoding.QUERY_STRING_ENCODER.encode(eventName);

        return new HttpRequest.Builder()
                .method(HttpMethod.POST)
                .scheme(DEFAULT_SCHEME)
                .host(API_TAPSTREAM_COM)
                .path(path);

    }

    public static HttpRequest.Builder hitRequestBuilder(String accountName, String hitName){

        final String path =
                "/"
                + URLEncoding.QUERY_STRING_ENCODER.encode(accountName)
                + "/hit/"
                + URLEncoding.QUERY_STRING_ENCODER.encode(hitName)
                + ".gif";

        return new HttpRequest.Builder()
                .method(HttpMethod.GET)
                .scheme(DEFAULT_SCHEME)
                .host(API_TAPSTREAM_COM)
                .path(path);
    }

    public static HttpRequest.Builder timelineLookupRequestBuilder(String secret, String eventSession){

        return new HttpRequest.Builder()
                .method(HttpMethod.GET)
                .scheme(DEFAULT_SCHEME)
                .host(REPORTING_TAPSTREAM_COM)
                .path("/v1/timelines/lookup")
                .addQueryParameter("secret", secret)
                .addQueryParameter("event_session", eventSession);
    }

}
