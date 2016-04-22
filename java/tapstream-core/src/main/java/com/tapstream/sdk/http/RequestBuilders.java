package com.tapstream.sdk.http;


public class RequestBuilders {

    public static final String DEFAULT_SCHEME = "https";
    public static final String API_TAPSTREAM_COM = "api.tapstream.com";
    public static final String REPORTING_TAPSTREAM_COM = "reporting.tapstream.com";
    public static final String APP_TAPSTREAM_COM = "app.tapstream.com";

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

    public static HttpRequest.Builder timelineLookupRequestBuilder(String secret, String eventSession){

        return new HttpRequest.Builder()
                .method(HttpMethod.GET)
                .scheme(DEFAULT_SCHEME)
                .host(REPORTING_TAPSTREAM_COM)
                .path("/v1/timelines/lookup")
                .addQueryParameter("secret", secret)
                .addQueryParameter("event_session", eventSession)
                .addQueryParameter("blocking", "true");
    }

    public static HttpRequest.Builder wordOfMouthOfferRequestBuilder(String secret, String insertionPoint, String bundle) {
        return new HttpRequest.Builder()
                .method(HttpMethod.GET)
                .scheme(DEFAULT_SCHEME)
                .host(APP_TAPSTREAM_COM)
                .path("/api/v1/word-of-mouth/offers/")
                .addQueryParameter("secret", secret)
                .addQueryParameter("insertion_point", insertionPoint)
                .addQueryParameter("bundle", bundle);
    }
    public static HttpRequest.Builder wordOfMouthRewardRequestBuilder(String secret, String eventSession) {
        return new HttpRequest.Builder()
                .method(HttpMethod.GET)
                .scheme(DEFAULT_SCHEME)
                .host(APP_TAPSTREAM_COM)
                .path("/api/v1/word-of-mouth/rewards/")
                .addQueryParameter("secret", secret)
                .addQueryParameter("event_session", eventSession);
    }
}
