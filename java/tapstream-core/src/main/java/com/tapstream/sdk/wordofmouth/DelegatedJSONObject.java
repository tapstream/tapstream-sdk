package com.tapstream.sdk.wordofmouth;

import org.json.JSONException;
import org.json.JSONObject;

class DelegatedJSONObject {
    protected final JSONObject delegate;

    protected DelegatedJSONObject(JSONObject delegate){
        this.delegate = delegate;
    }

    protected String getOrDefault(String key, String d){
        try {
            return delegate.getString(key);
        }catch(JSONException e){
            return d;
        }
    }

    protected int getOrDefault(String key, int d){
        try {
            return delegate.getInt(key);
        }catch(JSONException e){
            return d;
        }
    }
}
