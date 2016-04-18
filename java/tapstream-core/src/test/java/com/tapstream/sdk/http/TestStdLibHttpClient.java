package com.tapstream.sdk.http;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import fi.iki.elonen.NanoHTTPD;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class TestStdLibHttpClient {

    NanoHTTPD server;
    StdLibHttpClient client;

    private static final String OK_BODY = "BODY OK";
    private static final String ERROR_BODY = "BODY ERROR";
    private static final String NOT_FOUND_BODY = "BODY NOT FOUND";

    @Before
    public void setup() throws Exception{
        server = new NanoHTTPD(8080){

            @Override
            public Response serve(IHTTPSession session) {
                String body = NOT_FOUND_BODY;
                Response.IStatus status = Response.Status.NOT_FOUND;

                if (session.getMethod() == Method.GET){
                    if (session.getUri().equals("/get/200")){
                        status = Response.Status.OK;
                        body = OK_BODY;
                    } else if (session.getUri().equals("/get/500")) {
                        status = Response.Status.INTERNAL_ERROR;
                        body = ERROR_BODY;
                    }

                } else if (session.getMethod() == Method.POST){
                    int contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
                    InputStream is = session.getInputStream();
                    byte[] bodyBytes = new byte[contentLength];

                    try{
                        is.read(bodyBytes);
                    } catch ( IOException e) {
                        throw new RuntimeException(e);
                    }

                    if (session.getUri().equals("/post/200")){
                        status = Response.Status.OK;
                        body = new String(bodyBytes);
                    } else if (session.getUri().equals("/post/500")){
                        status = Response.Status.INTERNAL_ERROR;
                        body = ERROR_BODY;
                    }
                }

                return newFixedLengthResponse(status, MIME_PLAINTEXT, body);
            }

        };

        server.start();
        client = new StdLibHttpClient();
    }

    @After
    public void teardown() throws Exception{
        server.stop();
        client.close();
    }

    @Test
    public void testGetRequest200() throws Exception {
        HttpRequest req = new HttpRequest(new URL("http://localhost:8080/get/200"), HttpMethod.GET, null);
        HttpResponse resp = client.sendRequest(req);
        assertThat(resp.getStatus(), is(200));
        assertThat(resp.getMessage(), is("OK"));
        assertThat(resp.getBody(), is(OK_BODY.getBytes()));
        assertThat(resp.getBodyAsString(), is(OK_BODY));
    }

    @Test
    public void testGetRequest500() throws Exception{
        HttpRequest req = new HttpRequest(new URL("http://localhost:8080/get/500"), HttpMethod.GET, null);
        HttpResponse resp = client.sendRequest(req);
        assertThat(resp.getStatus(), is(500));
        assertThat(resp.getMessage(), is("Internal Server Error"));
        assertThat(resp.getBody(), is(ERROR_BODY.getBytes()));
        assertThat(resp.getBodyAsString(), is(ERROR_BODY));
    }

    @Test
    public void testPostRequest200() throws Exception{
        String body = "POST BODY 200";
        HttpRequest req = new HttpRequest(new URL("http://localhost:8080/post/200"), HttpMethod.POST, new ByteArrayRequestBody(body.getBytes()));
        HttpResponse resp = client.sendRequest(req);
        assertThat(resp.getStatus(), is(200));
        assertThat(resp.getMessage(), is("OK"));
        assertThat(resp.getBody(), is(body.getBytes()));
        assertThat(resp.getBodyAsString(), is(body));
    }

    @Test
    public void testPostRequest500() throws Exception{
        String body = "POST BODY 500";
        HttpRequest req = new HttpRequest(new URL("http://localhost:8080/post/500"), HttpMethod.POST, new ByteArrayRequestBody(body.getBytes()));
        HttpResponse resp = client.sendRequest(req);
        assertThat(resp.getStatus(), is(500));
        assertThat(resp.getMessage(), is("Internal Server Error"));
        assertThat(resp.getBody(), is(ERROR_BODY.getBytes()));
        assertThat(resp.getBodyAsString(), is(ERROR_BODY));
    }



}
