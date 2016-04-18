package com.tapstream.sdk.http;

import com.tapstream.sdk.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;


public class StdLibHttpClient implements HttpClient{

    public static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    public static final int DEFAULT_READ_TIMEOUT = 5000;


    @Override
    public HttpResponse sendRequest(HttpRequest request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)request.getURL().openConnection();
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        connection.setUseCaches(false);

        switch (request.getMethod()){
            case GET:
                connection.setRequestMethod("GET");
                break;
            case POST:
                connection.setRequestMethod("POST");

                if (request.getBody() != null){
                    String contentType = request.getBody().contentType();
                    byte[] bodyBytes = request.getBody().toBytes();
                    connection.setFixedLengthStreamingMode(bodyBytes.length);
                    connection.setRequestProperty("Content-Type", contentType);
                    connection.setDoOutput(true);

                    OutputStream os = connection.getOutputStream();
                    try {
                        os.write(bodyBytes);
                    } finally {
                        if (os != null)
                            os.close();
                    }

                }

                break;
        }


        InputStream is;
        try {
            is = connection.getInputStream();
        } catch (IOException e){
            is = connection.getErrorStream();
        }

        byte[] responseBody;
        try{
            responseBody = Utils.readFully(is);
        } finally {
            if (is != null)
                is.close();
        }

        return new HttpResponse(
                connection.getResponseCode(),
                connection.getResponseMessage(),
                responseBody);
    }

    @Override
    public void close() throws IOException {
    }
}
