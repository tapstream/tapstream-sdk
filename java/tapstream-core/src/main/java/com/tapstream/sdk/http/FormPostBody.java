package com.tapstream.sdk.http;


import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormPostBody implements RequestBody {

    final private Map<String, String> params;

    public FormPostBody(){
        this.params = new LinkedHashMap<String, String>();
    }

    public FormPostBody add(String name, String value){
        params.put(name, value);
        return this;
    }

    public FormPostBody add(Map<String, String> updatedParams) {
        this.params.putAll(updatedParams);
        return this;
    }

    @Override
    public String contentType(){
        return "application/x-www-form-urlencoded; charset=utf-8";
    }

    @Override
    public byte[] toBytes() {
        try{
            return URLEncoding.buildFormBody(params).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "FormPostBody{" +
                "params=" + params +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormPostBody that = (FormPostBody) o;

        return params != null ? params.equals(that.params) : that.params == null;

    }

    @Override
    public int hashCode() {
        return params != null ? params.hashCode() : 0;
    }
}
