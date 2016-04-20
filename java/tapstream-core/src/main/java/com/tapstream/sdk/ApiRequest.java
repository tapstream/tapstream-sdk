package com.tapstream.sdk;

import com.tapstream.sdk.errors.ApiException;
import com.tapstream.sdk.errors.RecoverableApiException;
import com.tapstream.sdk.errors.RetriesExhaustedException;
import com.tapstream.sdk.errors.UnrecoverableApiException;
import com.tapstream.sdk.http.HttpClient;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ApiRequest<T> implements Runnable {

    public abstract static class Handler<T> {
        abstract T checkedRun(HttpResponse resp) throws IOException, ApiException;
        void onFailure(){};
        void onRetry(){};
    }

    final HttpClient client;
    final ApiFuture<T> responseFuture;
    final Retry.Retryable<HttpRequest> retryable;
    final Handler<T> handler;
    final ScheduledExecutorService executor;

	public static <T> void submit(ScheduledExecutorService executor,
                                  HttpClient client,
                                  ApiFuture<T> responseFuture,
                                  Retry.Retryable<HttpRequest> retryable,
                                  Handler<T> handler) {

        try {
            Future<?> requestFuture = executor.submit(new ApiRequest<T>(responseFuture, retryable, handler, executor, client));
            responseFuture.propagateCancellationTo(requestFuture);
        } catch (RuntimeException e){
            responseFuture.setException(e);
            handler.onFailure();
        }
    }

    public ApiRequest(ApiFuture<T> responseFuture, Retry.Retryable<HttpRequest> retryable, Handler<T> handler, ScheduledExecutorService executor, HttpClient client) {
        this.responseFuture = responseFuture;
        this.retryable = retryable;
        this.handler = handler;
        this.executor = executor;
        this.client = client;
    }

    private void fail(Throwable e){
        handler.onFailure();
        responseFuture.setException(e);
    }

    final public void run() {
        try {
            HttpResponse response = client.sendRequest(retryable.get());
            response.throwOnError();
            responseFuture.set(handler.checkedRun(response));
        } catch (RecoverableApiException e) {
            if (retryable.shouldRetry()) {
                Logging.log(Logging.WARN, "Failure during request, retrying (http code %d).", e.getHttpResponse().status);
                retryable.incrementAttempt();
                handler.onRetry();
                Future<?> requestFuture = executor.schedule(this, retryable.getDelayMs(), TimeUnit.MILLISECONDS);
                responseFuture.propagateCancellationTo(requestFuture);
            } else {
                Logging.log(Logging.WARN, "No more retries, failing permanently (http code %d).", e.getHttpResponse().status);
                fail(new RetriesExhaustedException());
            }
        } catch (UnrecoverableApiException e) {
            Logging.log(Logging.ERROR, "Unrecoverable request error");
            fail(e);
        } catch (IOException e){
            Logging.log(Logging.ERROR, "IO Error during API call");
            fail(e);
        } catch (Exception e) {
            Logging.log(Logging.ERROR, e.getMessage());
            fail(e);
        }
    }
}
