package com.tapstream.sdk.http;

import java.io.IOException;

public interface HttpClient {
    HttpResponse sendRequest(HttpRequest request) throws IOException;
}
