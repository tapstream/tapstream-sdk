package com.tapstream.sdk;

public class Response {
	public int status;
	public String message;
	public String data;

	public Response(int status, String message, String data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}
};