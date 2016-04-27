package com.tapstream.sdk.http;

import com.tapstream.sdk.ApiResponse;
import com.tapstream.sdk.Retry;
import com.tapstream.sdk.SettableApiFuture;
import com.tapstream.sdk.errors.ApiException;
import com.tapstream.sdk.errors.RetriesExhaustedException;
import com.tapstream.sdk.errors.UnrecoverableApiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class TestAsyncHttpClient {

    ScheduledExecutorService executor;
    HttpClient syncClient;
    AsyncHttpClient asyncClient;

    @Before
    public void setup() throws Exception {
        syncClient = mock(HttpClient.class);
        executor = Executors.newSingleThreadScheduledExecutor();
        asyncClient = new AsyncHttpClient(syncClient, executor);
    }

    @After
    public void teardown() throws Exception {
        executor.shutdownNow();
    }

    class SimpleApiResponse implements ApiResponse {

        HttpResponse response;

        public SimpleApiResponse(HttpResponse response) {
            this.response = response;
        }

        @Override
        public HttpResponse getHttpResponse() {
            return response;
        }
    }

    @Test
    public void testSendRequest() throws Exception {
        HttpRequest req = new HttpRequest(new URL("http://tapstream.com"), HttpMethod.GET, null);
        HttpResponse resp = new HttpResponse(200, "OK");

        when(syncClient.sendRequest(req))
                .thenReturn(new HttpResponse(500, "ERROR"))
                .thenReturn(resp);

        SettableApiFuture<SimpleApiResponse> responseFuture = new SettableApiFuture<SimpleApiResponse>();

        AsyncHttpRequest.Handler<SimpleApiResponse> handler = new AsyncHttpRequest.Handler<SimpleApiResponse>() {
            @Override
            protected SimpleApiResponse checkedRun(HttpResponse resp) throws IOException, ApiException {
                return new SimpleApiResponse(resp);
            }
        };

        asyncClient.sendRequest(req, new Retry.FixedDelay(0, 2), handler, responseFuture);
        SimpleApiResponse response = responseFuture.get();
        assertThat(response.getHttpResponse(), is(resp));
    }

    @Test(expected = RetriesExhaustedException.class)
    public void testWithRetriesExhusted() throws Exception {
        HttpRequest req = new HttpRequest(new URL("http://tapstream.com"), HttpMethod.GET, null);
        HttpResponse resp = new HttpResponse(200, "OK");

        when(syncClient.sendRequest(req))
                .thenReturn(new HttpResponse(500, "ERROR"))
                .thenReturn(resp);

        SettableApiFuture<SimpleApiResponse> responseFuture = new SettableApiFuture<SimpleApiResponse>();

        AsyncHttpRequest.Handler<SimpleApiResponse> handler = new AsyncHttpRequest.Handler<SimpleApiResponse>() {
            @Override
            protected SimpleApiResponse checkedRun(HttpResponse resp) throws IOException, ApiException {
                return new SimpleApiResponse(resp);
            }
        };

        asyncClient.sendRequest(req, new Retry.FixedDelay(0, 1), handler, responseFuture);

        try {
            SimpleApiResponse response = responseFuture.get();
        } catch (ExecutionException e){
            throw (Exception)e.getCause();
        }
    }

    @Test(expected = UnrecoverableApiException.class)
    public void testWithHandlerException() throws Exception {
        HttpRequest req = new HttpRequest(new URL("http://tapstream.com"), HttpMethod.GET, null);
        HttpResponse resp = new HttpResponse(200, "OK");

        when(syncClient.sendRequest(req))
                .thenReturn(resp);

        SettableApiFuture<SimpleApiResponse> responseFuture = new SettableApiFuture<SimpleApiResponse>();

        AsyncHttpRequest.Handler<SimpleApiResponse> handler = new AsyncHttpRequest.Handler<SimpleApiResponse>() {
            @Override
            protected SimpleApiResponse checkedRun(HttpResponse resp) throws IOException, ApiException {
                throw new UnrecoverableApiException(resp, "Test exception");
            }
        };

        asyncClient.sendRequest(req, Retry.NEVER_RETRY, handler, responseFuture);

        try {
            SimpleApiResponse response = responseFuture.get();
        } catch (ExecutionException e){
            throw (Exception)e.getCause();
        }
    }
}