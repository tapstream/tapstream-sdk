package com.tapstream.sdk.http;

import com.tapstream.sdk.errors.ApiException;
import com.tapstream.sdk.errors.RecoverableApiException;
import com.tapstream.sdk.errors.UnrecoverableApiException;

import java.io.UnsupportedEncodingException;

public class HttpResponse {
	final public int status;
	final public String message;
	final public byte[] body;

	public HttpResponse(int status, String message){
		this(status, message, new byte[0]);
	}

	public HttpResponse(int status, String message, byte[] body) {
		this.status = status;
		this.message = message;
		this.body = body;
	}

	public int getStatus(){
		return status;
	}

	public String getMessage(){
		return message;
	}

	public byte[] getBody(){
		return body;
	}

	public String getBodyAsString() {
		try{
			return new String(body, "UTF-8");
		} catch (UnsupportedEncodingException e){
			// Should never happen
			throw new RuntimeException(e);
		}
	}

	public boolean succeeded(){
		return status >= 200 && status < 300;
	}

	public boolean failed(){
		return !succeeded();
	}

	public boolean shouldRetry(){

		return status >= 500 && status < 600;
	}

	public void throwOnError() throws ApiException{
		if(this.succeeded()){
			return;
		}

		if(this.shouldRetry()) {
			throw new RecoverableApiException(this);
		}
        throw new UnrecoverableApiException(this, "Word of Mouth reward lookup failed.");
	}
};