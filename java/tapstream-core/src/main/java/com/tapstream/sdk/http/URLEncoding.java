package com.tapstream.sdk.http;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class URLEncoding {

    public interface StringEncoder {
        String encode(String raw);
    }

    public static class FormFieldEncoder implements StringEncoder {

        private final boolean plusForSpace;

        public FormFieldEncoder(boolean plusForSpace){
            this.plusForSpace = plusForSpace;
        }

        @Override
        public String encode(String raw) {
            try{
                String encoded = URLEncoder.encode(raw, "UTF-8");
                if (plusForSpace){
                    return encoded;
                } else {
                    return encoded.replace("+", "%20");
                }
            } catch (UnsupportedEncodingException e){
                throw new RuntimeException(e);
            }
        }
    }

    public static final StringEncoder QUERY_STRING_ENCODER = new FormFieldEncoder(false);
    public static final StringEncoder FORM_FIELD_ENCODER = new FormFieldEncoder(true);


    public static String joinAndEncodeParams(Map<String, String> params, StringEncoder encoder){
        if (params == null){
            return "";
        }

        StringBuilder queryBuilder = new StringBuilder();

        Iterator<Map.Entry<String, String>> qsIter = params.entrySet().iterator();
        while(qsIter.hasNext()){
            Map.Entry<String, String> qsEntry = qsIter.next();
            queryBuilder.append(encoder.encode(qsEntry.getKey()));
            if (qsEntry.getValue() != null){
                queryBuilder.append("=");
                queryBuilder.append(encoder.encode(qsEntry.getValue()));
            }
            if (qsIter.hasNext()){
                queryBuilder.append("&");
            }
        }

        return queryBuilder.toString();
    }


    public static String buildQueryString(Map<String, String> params){
        return joinAndEncodeParams(params, QUERY_STRING_ENCODER);
    }

    public static String buildFormBody(Map<String, String> params){
        return joinAndEncodeParams(params, FORM_FIELD_ENCODER);
    }

}
