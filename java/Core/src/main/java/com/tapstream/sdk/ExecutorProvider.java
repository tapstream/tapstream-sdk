package com.tapstream.sdk;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Date: 15-05-01
 * Time: 1:47 PM
 */
public interface ExecutorProvider {
    public <T> Future<T> submit(Callable<T> task, int time, TimeUnit unit);
}
