package com.tapstream.sdk.http;


public class ByteArrayRequestBody implements RequestBody{

    private final byte[] body;

    public ByteArrayRequestBody() {
        this(new byte[0]);
    }

    public ByteArrayRequestBody(byte[] body){
        this.body = body;
    }

    @Override
    public byte[] toBytes() {
        return body;
    }

    @Override
    public String contentType() {
        return "byte array";
    }
}
