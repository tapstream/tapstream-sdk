package com.tapstream.sdk.http;

import com.tapstream.sdk.ApiResponse;
import com.tapstream.sdk.Logging;
import com.tapstream.sdk.Retry;
import com.tapstream.sdk.SettableApiFuture;
import com.tapstream.sdk.errors.ApiException;
import com.tapstream.sdk.errors.RecoverableApiException;
import com.tapstream.sdk.errors.RetriesExhaustedException;
import com.tapstream.sdk.errors.UnrecoverableApiException;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AsyncHttpRequest<T extends ApiResponse> implements Runnable {

    public abstract static class Handler<T> {
        protected abstract T checkedRun(HttpResponse resp) throws IOException, ApiException;
        protected void onFailure(){};
        protected void onRetry(){};
    }

    final HttpClient client;
    final SettableApiFuture<T> responseFuture;
    final Retry.Retryable<HttpRequest> retryable;
    final Handler<T> handler;
    final ScheduledExecutorService executor;

    public AsyncHttpRequest(SettableApiFuture<T> responseFuture, Retry.Retryable<HttpRequest> retryable, Handler<T> handler, ScheduledExecutorService executor, HttpClient client) {
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
            if (responseFuture.isCancelled()){
                Logging.log(Logging.INFO, "API request cancelled");
            } else if (retryable.shouldRetry()) {
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
            Logging.log(Logging.ERROR, e.toString());
            fail(e);
        }
    }
}
