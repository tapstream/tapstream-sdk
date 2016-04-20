package com.tapstream.sdk;

import com.tapstream.sdk.errors.RecoverableApiException;
import com.tapstream.sdk.errors.RetriesExhaustedException;
import com.tapstream.sdk.errors.UnrecoverableApiException;
import com.tapstream.sdk.http.HttpClient;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by adam on 2016-04-20.
 */
public class ApiRunnable<T> implements Runnable {
    final HttpClient client;
    final ApiFuture<T> responseFuture;
    final Retry.Retryable<HttpRequest> retryable;
    final CheckedApiRunnable<T> inner;
    final ScheduledExecutorService executor;


	public static <T> void createAndStart(ApiFuture<T> responseFuture,
                                          Retry.Retryable<HttpRequest> retryable,
                                          CheckedApiRunnable<T> inner,
                                          ScheduledExecutorService executor,
                                          HttpClient client) {

        try {
            executor.submit(new ApiRunnable<T>(responseFuture, retryable, inner, executor, client));
        } catch (RuntimeException e){
            responseFuture.setException(e);
            inner.onFailure();
        }
    }

    public ApiRunnable(ApiFuture<T> responseFuture, Retry.Retryable<HttpRequest> retryable, CheckedApiRunnable<T> inner, ScheduledExecutorService executor, HttpClient client) {
        this.responseFuture = responseFuture;
        this.retryable = retryable;
        this.inner = inner;
        this.executor = executor;
        this.client = client;
    }

    private void fail(Throwable e){
        inner.onFailure();
        responseFuture.setException(e);
    }

    public void run() {
        try {
            HttpResponse response = client.sendRequest(retryable.get());
            response.throwOnError();
            responseFuture.set(inner.checkedRun(response));
        } catch (RecoverableApiException e) {
            if (retryable.shouldRetry()) {
                Logging.log(Logging.WARN, "Failure during request, retrying (http code %d).", e.getHttpResponse().status);
                retryable.incrementAttempt();
                inner.onRetry();
                executor.schedule(this, retryable.getDelayMs(), TimeUnit.MILLISECONDS);
            } else {
                Logging.log(Logging.WARN, "No more retries, failing permanently (http code %d).", e.getHttpResponse().status);
                fail(new RetriesExhaustedException());
            }
        } catch (UnrecoverableApiException e) {
            Logging.log(Logging.ERROR, "Request failed hard, wtf?"); // TODO
            fail(e);
        } catch (Exception e) {
            Logging.log(Logging.WARN, e.getMessage());
            fail(e);
        }
    }
}
