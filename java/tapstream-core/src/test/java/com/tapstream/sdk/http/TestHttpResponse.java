package com.tapstream.sdk.http;


import com.tapstream.sdk.errors.ApiException;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class TestHttpResponse {

    private static final int successStatus = 200;
    private static final int retryableStatus = 500;
    private static final int hardErrorStatus = 400;

    private static final String message = "theMessage";
    private static final String bodyString = "bodyString";
    private static final byte[] body = bodyString.getBytes();

    HttpResponse successResponse;
    HttpResponse retryableResponse;
    HttpResponse hardErrorResponse;

    @Before
    public void setup() throws Exception {
        this.successResponse = new HttpResponse(successStatus, message, body);
        this.retryableResponse = new HttpResponse(retryableStatus, message, body);
        this.hardErrorResponse = new HttpResponse(hardErrorStatus, message, body);
    }

    @Test
    public void testGetStatus() throws Exception {
        assertThat(successResponse.getStatus(), is(successStatus));
    }

    @Test
    public void testGetMessage() throws Exception {
        assertThat(successResponse.getMessage(), is(message));
    }

    @Test
    public void testGetBody() throws Exception {
        assertThat(successResponse.getBody(), is(body));
    }

    @Test
    public void testGetBodyAsString() throws Exception {
        assertThat(successResponse.getBodyAsString(), is(bodyString));
    }

    @Test
    public void testSucceeded() throws Exception {
        assertThat(successResponse.succeeded(), is(true));
        assertThat(retryableResponse.succeeded(), is(false));
        assertThat(hardErrorResponse.succeeded(), is(false));
    }

    @Test
    public void testFailed() throws Exception {
        assertThat(successResponse.failed(), is(false));
        assertThat(retryableResponse.failed(), is(true));
        assertThat(hardErrorResponse.failed(), is(true));
    }

    @Test
    public void testShouldRetry() throws Exception {
        assertThat(successResponse.shouldRetry(), is(false));
        assertThat(retryableResponse.shouldRetry(), is(true));
        assertThat(hardErrorResponse.shouldRetry(), is(false));
    }

    @Test
    public void testThrowOnError() throws Exception {
        try {
            successResponse.throwOnError();
        } catch (ApiException e){
            fail("should never happen");
        }

        try {
            retryableResponse.throwOnError();
            fail("should never happen");
        } catch (ApiException e){ }

        try {
            hardErrorResponse.throwOnError();
            fail("should never happen");
        } catch (ApiException e){ }
    }
}
