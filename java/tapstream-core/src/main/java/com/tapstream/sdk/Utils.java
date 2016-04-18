package com.tapstream.sdk;


import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static byte[] readFully(InputStream is) throws IOException {
        ByteArrayOutputStream responseBodyStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 8];
        int bytesRead;
        do {
            bytesRead = is.read(buffer);
            if (bytesRead != -1){
                responseBodyStream.write(buffer, 0, bytesRead);
            }
        } while (bytesRead != -1);

        return responseBodyStream.toByteArray();
    }

    public static void closeQuietly(Closeable obj){
        try {
            obj.close();
        } catch (Exception e){
            // Swallow any exceptions while closing
        }
    }

}
