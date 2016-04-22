package com.tapstream.sdk.http;


import org.junit.Test;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestRequestBuilders {


    @Test
    public void testEventRequestBuilder() throws Exception {
        String accountName = "account / name";
        String eventName = "event / name";

        HttpRequest request = RequestBuilders.eventRequestBuilder(accountName, eventName)
                .postBody(new FormPostBody().add("key", "value"))
                .build();
        assertThat(request.getURL(), is(new URL("https://api.tapstream.com/account%20%2F%20name" +
                "/event/event%20%2F%20name")));
        assertThat(request.getMethod(), is(HttpMethod.POST));
        assertThat(request.getBody().toBytes(), is("key=value".getBytes()));

    }

    @Test
    public void testTimelineLookupRequestBuilder() throws Exception {
        String secret = "theSecret";
        String eventSession = "theSession";

        HttpRequest request = RequestBuilders.timelineLookupRequestBuilder(secret, eventSession).build();
        assertThat(request.getURL(), is(new URL("https://reporting.tapstream.com/v1/timelines/lookup" +
                "?secret=theSecret&event_session=theSession&blocking=true")));
        assertThat(request.getMethod(), is(HttpMethod.GET));

    }

    @Test
    public void testWordOfMouthOfferRequestBuilder() throws Exception {
        String secret = "theSecret";
        String insertionPoint = "theInsertionPoint";
        String bundle = "theBundle";
        HttpRequest request = RequestBuilders.wordOfMouthOfferRequestBuilder(secret, insertionPoint, bundle).build();
        assertThat(request.getURL(), is(new URL("https://app.tapstream.com/api/v1/word-of-mouth/offers/?secret=theSecret&insertion_point=theInsertionPoint&bundle=theBundle")));
        assertThat(request.getMethod(), is(HttpMethod.GET));

    }

    @Test
    public void testWordOfMouthRewardRequestBuilder() throws Exception {
        String secret = "theSecret";
        String eventSession = "theSession";
        HttpRequest request = RequestBuilders.wordOfMouthRewardRequestBuilder(secret, eventSession).build();
        assertThat(request.getURL(), is(new URL("https://app.tapstream.com/api/v1/word-of-mouth/rewards/?secret=theSecret&event_session=theSession")));
        assertThat(request.getMethod(), is(HttpMethod.GET));
    }
}
