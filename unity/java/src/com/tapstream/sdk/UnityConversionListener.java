package com.tapstream.sdk;

import java.lang.reflect.Method;

class UnityConversionListener implements ConversionListener{
	String callbackTarget;
	String callbackMethod;
	Class<?> unityPlayerCls = null;
	Method unitySendMessage = null;

	public static void getConversionData(String callbackTarget, String callbackMethod){
		Tapstream.getInstance().getConversionData(new UnityConversionListener(callbackTarget, callbackMethod));
	}

	public UnityConversionListener(String callbackTarget, String callbackMethod){
		this.callbackTarget = callbackTarget;
		this.callbackMethod = callbackMethod;
		try{
			unityPlayerCls = Class.forName("com.unity3d.player.UnityPlayer");
			unitySendMessage = unityPlayerCls.getMethod("UnitySendMessage", String.class, String.class, String.class);
		}catch(Exception e){
			Logging.log(Logging.INFO, "Could not find UnityPlayer.UnitySendMessage in UnityConversionListener");
		}
	}

	public void conversionData(String jsonInfo){
		if(unityPlayerCls != null && unitySendMessage != null){
			try{
				unitySendMessage.invoke(unityPlayerCls, callbackTarget, callbackMethod, jsonInfo);
			}catch(Exception e){
				Logging.log(Logging.INFO, "Got conversion data, but could not invoke UnitySendMessage");
			}
		}
	}
}
