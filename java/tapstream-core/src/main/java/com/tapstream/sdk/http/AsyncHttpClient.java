package com.tapstream.sdk.http;

import com.tapstream.sdk.ApiFuture;
import com.tapstream.sdk.ApiResponse;
import com.tapstream.sdk.Retry;
import com.tapstream.sdk.SettableApiFuture;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class AsyncHttpClient implements Closeable {

    private final HttpClient httpClient;
    private final ScheduledExecutorService executor;

    public AsyncHttpClient(HttpClient httpClient, ScheduledExecutorService executor) {
        this.httpClient = httpClient;
        this.executor = executor;
    }

    public <T extends ApiResponse> ApiFuture<T> sendRequest(HttpRequest request, Retry.Strategy retryStrategy, AsyncHttpRequest.Handler<T> handler, SettableApiFuture<T> responseFuture){

        try {
            AsyncHttpRequest<T> asyncRequest = new AsyncHttpRequest<T>(
                    responseFuture,
                    new Retry.Retryable<HttpRequest>(request, retryStrategy),
                    handler,
                    executor,

                    httpClient);

            Future<?> requestFuture = executor.submit(asyncRequest);
            responseFuture.propagateCancellationTo(requestFuture);
        } catch (RuntimeException e){
            responseFuture.setException(e);
            handler.onFailure();
        }

        return responseFuture;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
