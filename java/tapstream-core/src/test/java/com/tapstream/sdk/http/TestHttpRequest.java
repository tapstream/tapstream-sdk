package com.tapstream.sdk.http;

import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestHttpRequest {


    @Test
    public void testRequestBuilder() throws Exception {
        HttpMethod method = HttpMethod.GET;
        String scheme = "http";
        String host = "host";
        String path = "/path";
        String fragment = "fragment";
        RequestBody body = new ByteArrayRequestBody();

        HttpRequest request = new HttpRequest.Builder()
                .method(method)
                .scheme(scheme)
                .host(host)
                .path(path)
                .fragment(fragment)
                .addQueryParameter("key å", "value ∑")
                .addQueryParameter("another", "value")
                .postBody(body)
                .build();

        assertThat(request.getMethod(), is(method));
        assertThat(request.getBody(), is(body));

        URL url = request.getURL();
        assertThat(url.getProtocol(), is(scheme));
        assertThat(url.getHost(), is(host));
        assertThat(url.getPath(), is(path));
        assertThat(url.getRef(), is(fragment));
        assertThat(url.getQuery(), is("key%20%C3%A5=value%20%E2%88%91&another=value"));
    }


}
