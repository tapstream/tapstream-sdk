package com.tapstream.sdk;


public interface Callback<T> {

    /**
     * Called when the associated action has been completed successfully.
     * @param result the result of an async action.
     */
    void success(T result);

    /**
     * Called when the associated action did completed with an error.
     * @param reason the exception thrown during processing.
     */
    void error(Throwable reason);
}
