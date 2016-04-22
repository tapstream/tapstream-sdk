package com.tapstream.sdk;

import com.tapstream.sdk.http.HttpResponse;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestEventApiResponse {

    @Test
    public void test() throws Exception {
        HttpResponse httpResp = new HttpResponse(200, "OK");
        EventApiResponse resp = new EventApiResponse(httpResp);
        assertThat(resp.getHttpResponse(), is(httpResp));
    }

}
