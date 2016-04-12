package com.tapstream.sdk;

import org.json.JSONObject;

public interface Api {
	void fireEvent(Event e);
	void getConversionData(Callback<JSONObject> completion);
}
