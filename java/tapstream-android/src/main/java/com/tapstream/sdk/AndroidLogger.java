package com.tapstream.sdk;


public class AndroidLogger implements Logging.Logger{

    private static final String TAG = "Tapstream";

    @Override
    public void log(int logLevel, String msg) {
        final int priority;

        switch (logLevel){
            case Logging.INFO:
                priority = android.util.Log.INFO;
                break;
            case Logging.WARN:
                priority = android.util.Log.WARN;
                break;
            case Logging.ERROR:
                priority = android.util.Log.ERROR;
                break;
            default:
                priority = android.util.Log.ERROR;
                break;
        }

        android.util.Log.println(priority, TAG, msg);
    }
}
