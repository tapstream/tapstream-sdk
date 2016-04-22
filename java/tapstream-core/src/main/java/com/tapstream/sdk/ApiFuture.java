package com.tapstream.sdk;

import java.util.concurrent.Future;


public interface ApiFuture<T> extends Future<T> {
    void setCallback(Callback<T> callback);
}
