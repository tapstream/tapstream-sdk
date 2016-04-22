package com.tapstream.sdk;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SettableApiFuture<T> implements ApiFuture<T> {

    T obj;
    Throwable error;
    Callback<T> callback;
    Future<?> propagateCancellationTo;

    int state = STATE_INITIAL;

    private static final int STATE_INITIAL = 0;
    private static final int STATE_CANCELLED = 1;
    private static final int STATE_DONE = 2;
    private static final int STATE_ERROR = 3;


    @Override
    synchronized public boolean cancel(boolean mayInterruptIfRunning) {
        if (state != STATE_INITIAL)
            return false;

        state = STATE_CANCELLED;

        if (propagateCancellationTo != null)
            propagateCancellationTo.cancel(mayInterruptIfRunning);

        this.notifyAll();
        return true;
    }

    synchronized public boolean isError() {
        return state == STATE_ERROR;
    }

    @Override
    synchronized public boolean isCancelled() {
        return state == STATE_CANCELLED;
    }

    @Override
    synchronized public boolean isDone() {
        return state == STATE_CANCELLED
                || state == STATE_DONE
                || state == STATE_ERROR;
    }

    synchronized public boolean set(T obj){
        if (state != STATE_INITIAL)
            return false;

        state = STATE_DONE;
        this.obj = obj;
        this.notifyAll();
        safeCallbackSuccess(callback, obj);
        return true;
    }

    synchronized public boolean setException(Throwable t){
        if (state != STATE_INITIAL)
            return false;

        state = STATE_ERROR;
        this.error = t;
        this.notifyAll();
        safeCallbackError(callback, t);
        return true;
    }

    synchronized public void propagateCancellationTo(Future<?> future){
        this.propagateCancellationTo = future;
    }

     private static <T> void safeCallbackSuccess(Callback<T> callback, T obj){
        if (callback != null){
            try {
                callback.success(obj);
            } catch (Exception e){
                Logging.log(Logging.WARN, "Failed to execute callback success: " + e.toString());
            }
        }
    }

    private static <T> void safeCallbackError(Callback<T> callback, Throwable error){
        if (callback != null){
            try {
                callback.error(error);
            } catch (Exception e){
                Logging.log(Logging.WARN, "Failed to execute callback error: " + e.toString());
            }
        }
    }

    @Override
    synchronized public void setCallback(Callback<T> callback){
        switch (state) {
            case STATE_INITIAL:
                this.callback = callback;
                break;
            case STATE_ERROR:
                safeCallbackError(callback, error);
                break;
            case STATE_DONE:
                safeCallbackSuccess(callback, obj);
                break;
            case STATE_CANCELLED:
                safeCallbackError(callback, new CancellationException());
                break;
        }
    }

    @Override
    synchronized public T get() throws InterruptedException, ExecutionException {
        while (true) {
            switch (state) {
                case STATE_DONE:
                    return obj;
                case STATE_ERROR:
                    throw new ExecutionException(error);
                case STATE_CANCELLED:
                    throw new CancellationException();
                default:
                    this.wait();
            }
        }
    }

    @Override
    synchronized public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        long timeoutMillis = unit.toMillis(timeout);

        while (true) {
            switch (state) {
                case STATE_DONE:
                    return obj;
                case STATE_ERROR:
                    throw new ExecutionException(error);
                case STATE_CANCELLED:
                    throw new CancellationException();
                default:
                    long timeDelta = System.currentTimeMillis() - start;
                    long timeRemaining = timeoutMillis - timeDelta;
                    if (timeRemaining > 0){
                        this.wait(timeRemaining);
                    } else {
                        throw new TimeoutException();
                    }
            }

        }
    }
}
