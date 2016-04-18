package com.tapstream.sdk.http;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestFormPostBody {

    @Test
    public void testFormEncoding() throws Exception {
        FormPostBody body = new FormPostBody();
        body.add("key 1", "value 1");
        body.add("key ∂", "value å");

        byte[] actual = body.toBytes();
        byte[] expected = "key+1=value+1&key+%E2%88%82=value+%C3%A5".getBytes("UTF-8");

        assertThat(actual, is(expected));
    }

    @Test
    public void testContentTYpe() throws Exception {
        FormPostBody body = new FormPostBody();
        assertThat(body.contentType(), is("application/x-www-form-urlencoded; charset=utf-8"));
    }
}
