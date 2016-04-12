package com.tapstream.sdk;


import java.util.HashMap;
import java.util.Map;

public class EventParams {
    private final Map<String, String> params = new HashMap<String, String>();

    public void put(String key, long value){
        put(key, Long.toString(value));
    }

    public void put(String key, int value){
        put(key, Integer.toString(value));
    }

    public void put(String key, String value){
        put(key, value, true);
    }

    public void putWithoutTruncation(String key, String value){
        put(key, value, false);
    }

    private void put(String key, String value, boolean limitValueLength){
        if(key == null || value == null) {
            return;
        }

        if (key.length() > 255) {
            Logging.log(Logging.WARN, "Tapstream Warning: Event key exceeds 255 characters, this field will not be included in the post (key=%s)", key);
            return;
        }

        if (limitValueLength && value.length() > 255) {
            Logging.log(Logging.WARN, "Tapstream Warning: Event value exceeds 255 characters, this field will not be included in the post (value=%s)", value);
            return;
        }

        params.put(key, value);
    }

    public Map<String, String> toMap(){
        return params;
    }

}
