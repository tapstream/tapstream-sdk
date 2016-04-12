package com.tapstream.sdk;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class DeamonThreadFactory implements ThreadFactory {

    private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

    @Override
    public Thread newThread(Runnable r) {
        Thread t = defaultThreadFactory.newThread(r);
        t.setDaemon(true);
        return t;
    }
}
