package com.tapstream.sdk.http;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestByteArrayRequestBody {

    @Test
    public void testToBytes() throws Exception {
        byte[] expected = "abc".getBytes();
        ByteArrayRequestBody body = new ByteArrayRequestBody(expected);
        byte[] actual = body.toBytes();
        assertThat(expected, is(actual));
    }

    @Test
    public void testContentType() throws Exception {
        ByteArrayRequestBody body = new ByteArrayRequestBody(new byte[]{});
        assertThat(body.contentType(), is("byte array"));
    }
}