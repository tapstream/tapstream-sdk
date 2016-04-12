package com.tapstream.sdk;


public interface Callback<T> {
    void success(T obj);
    void error(Throwable reason);
}
