package com.tapstream.sdk.http;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;


public class StdLibHttpClient implements HttpClient{

    @Override
    public HttpResponse sendRequest(HttpRequest request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)request.getURL().openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        switch (request.getMethod()){
            case GET:
                connection.setRequestMethod("GET");
                break;
            case POST:
                connection.setRequestMethod("POST");

                if (request.getBody() != null){
                    connection.setRequestProperty("Content-Type", request.getBody().contentType());
                    connection.setDoOutput(true);
                    OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                    try{
                        os.write(request.getBody().toBytes());
                    } finally {
                        os.close();
                    }
                }

                break;
        }

        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 8];
        InputStream is = connection.getInputStream();
        try{
            int bytesRead;
            do {
                bytesRead = is.read(buffer);
                if (bytesRead != -1){
                    responseBody.write(buffer, 0, bytesRead);
                }
            } while (bytesRead != -1);

        } finally {
            is.close();
        }

        return new HttpResponse(connection.getResponseCode(), connection.getResponseMessage(), responseBody.toByteArray());
    }
}
