package com.tapstream.phonegap;

import android.util.Log;
import com.tapstream.sdk.*;
import java.lang.reflect.Method;
import java.util.*;
import org.apache.cordova.api.*;
import org.json.*;

public class TapstreamPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if(action.equals("create")) {
            String accountName = args.getString(0);
            String developerSecret = args.getString(1);
            JSONObject config = args.optJSONObject(2);
            this.create(accountName, developerSecret, config);
            return true;
        } else if(action.equals("fireEvent")) {
            String eventName = args.getString(0);
            boolean oneTimeOnly = args.getBoolean(1);
            JSONObject params = args.optJSONObject(2);
            this.fireEvent(eventName, oneTimeOnly, params);
            return true;
        }
        return false;
    }

    private void create(String accountName, String developerSecret, JSONObject configVals) throws JSONException {
        Config config = new Config();

        if(configVals != null) {

            Method[] methods = null;
            try {
                methods = Config.class.getDeclaredMethods();
            } catch(Exception e) {
                Log.e(getClass().getSimpleName(), "Error getting declared methods of Config class. " + e.getMessage());
                return;
            }

            if(methods == null) {
                Log.e(getClass().getSimpleName(), "Failed to get declared methods of Config class.");
                return;
            }
            
            Iterator<?> iter = configVals.keys();
            while(iter.hasNext()) {
                String key = (String)iter.next();
                Object value = configVals.get(key);

                if(value == null) {
                    Log.e(getClass().getSimpleName(), "Config object will not accept null values, skipping field named: " + key);
                    continue;
                }

                String methodName = ("set"+key).toLowerCase(Locale.US);
                Method method = null;
                for(Method m : methods) {
                    if(m.getName().toLowerCase(Locale.US).equals(methodName)) {
                        method = m;
                        break;
                    }
                }
                if(method == null) {
                    Log.e(getClass().getSimpleName(), "Config object has no field named: " + (String)value);
                    continue;
                }

                try {
                    if(value instanceof String) {
                        method.invoke(config, (String)value);
                    } else if(value instanceof Boolean) {
                        method.invoke(config, (Boolean)value);
                    } else {
                        Log.e(getClass().getSimpleName(), "Config object will not accept type: " + value.getClass().toString());
                    }
                } catch(Exception e) {
                    Log.e(getClass().getSimpleName(), "Error setting field on config object (key=" + key + "). " + e.getMessage());
                }
            }
        }

        Tapstream.create(this.cordova.getActivity().getApplicationContext(), accountName, developerSecret, config);
    }

    private void fireEvent(String eventName, boolean oneTimeOnly, JSONObject params) throws JSONException {
        Event e = new Event(eventName, oneTimeOnly);
        if(params != null) {
            Iterator<?> iter = params.keys();
            while(iter.hasNext()) {
                String key = (String)iter.next();
                e.addPair(key, params.get(key));
            }
        }
        Tapstream.getInstance().fireEvent(e);
    }

}