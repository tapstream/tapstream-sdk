package com.tapstream.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class Utils {
	public static String encodeString(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String stringify(Object value) {
		double d;
		try {
			d = (Double) value;			
		} catch (ClassCastException ex) {
			return value.toString();
		}
		
		double truncated = Math.floor(d);
		if (truncated == d) {
			return String.format(Locale.US, "%.0f", d);
		}
		return value.toString();
	}
	
	public static String encodeEventPair(String prefix, String key, Object value) {
		if(key == null || value == null) {
			return null;
		}

		if (key.length() > 255) {
			Logging.log(Logging.WARN, "Tapstream Warning: Event key exceeds 255 characters, this field will not be included in the post (key=%s)", key);
			return null;
		}

		String encodedName = Utils.encodeString(prefix + key);
		if(encodedName == null) {
			return null;
		}

		String stringifiedValue = Utils.stringify(value);
		if (stringifiedValue.length() > 255) {
			Logging.log(Logging.WARN, "Tapstream Warning: Event value exceeds 255 characters, this field will not be included in the post (value=%s)", value);
			return null;
		}

		String encodedValue = Utils.encodeString(stringifiedValue);
		if(encodedValue == null) {
			return null;
		}
		
		return encodedName + "=" + encodedValue;
	}
}
