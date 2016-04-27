package com.tapstream.sdk;

import java.util.concurrent.Future;


public interface ApiFuture<T> extends Future<T> {

    /**
     * Set a {@link Callback} for this future removing the previous callback (if any existed).
     *
     * If the future has already been completed then the callback will be invoked immediately by
     * the calling thread. If the future has not yet been completed then the callback will be
     * called by a thread in the Tapstream client's internal thread pool. Because of this you need
     * to take care not do any long blocking operations in the callback.
     *
     * @param callback the callback object to associate with this future.
     */
    void setCallback(Callback<T> callback);
}
