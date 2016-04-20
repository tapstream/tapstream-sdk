package com.tapstream.sdk;

import com.tapstream.sdk.errors.ApiException;
import com.tapstream.sdk.http.HttpRequest;
import com.tapstream.sdk.http.HttpResponse;

import java.io.IOException;

/**
 * Created by adam on 2016-04-20.
 */
public abstract class CheckedApiRunnable<T> {
    public abstract T checkedRun(HttpResponse resp) throws IOException, ApiException;
    public void onFailure(){};
    public void onRetry(){};
}
