package com.tapstream.sdk.http;


public interface RequestBody {
    byte[] toBytes();
    String contentType();
}
