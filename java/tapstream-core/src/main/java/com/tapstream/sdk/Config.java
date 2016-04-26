package com.tapstream.sdk;

public class Config {

	private final String accountName;
	private final String developerSecret;

	public Config(String accountName, String developerSecret){
		this.accountName = accountName;
		this.developerSecret = developerSecret;
	}

	private Retry.Strategy dataCollectionRetryStrategy = Retry.DEFAULT_DATA_COLLECTION_STRATEGY;
	private Retry.Strategy userFacingRequestRetryStrategy = Retry.DEFAULT_USER_FACING_RETRY_STRATEGY;

	// Optional hardware identifiers that can be provided by the caller
	private String odin1 = null;
	private String openUdid = null;
	private String wifiMac = null;
	private String deviceId = null;
	private String androidId = null;

	// Set these if you want to override the names of the automatic events sent by the sdk
	private String installEventName = null;
	private String openEventName = null;
	
	// Unset these if you want to disable the sending of the automatic events
	private boolean fireAutomaticInstallEvent = true;
	private boolean fireAutomaticOpenEvent = true;

	// Unset this if you want to disable automatic collection of Android Advertising Id
	private boolean collectAdvertisingId = true;

	// Unset this to save a little memory by not using the WordOfMouth feature.
	private boolean useWordOfMouth = false;
	
	// These parameters will be automatically attached to all events fired by the sdk.
	private final Event.Params globalEventParams = new Event.Params();

	public String getAccountName(){
		return accountName;
	}

	public String getDeveloperSecret(){
		return developerSecret;
	}

	public void addGlobalEventParameter(String name, String value){
		globalEventParams.put(name, value);
	}

	public Event.Params getGlobalEventParams(){
		return globalEventParams;
	}

	public String getOdin1() { return odin1; }
	public void setOdin1(String odin1) { this.odin1 = odin1; }

	public String getOpenUdid() { return openUdid; }
	public void setOpenUdid(String openUdid) { this.openUdid = openUdid; }

	public String getDeviceId(){ return deviceId; }
	public void setDeviceId(String deviceId){ this.deviceId = deviceId; }

	public String getWifiMac(){ return wifiMac; }
	public void setWifiMac(String wifiMac){ this.wifiMac = wifiMac; }

	public String getAndroidId(){ return androidId; }
	public void setAndroidId(String androidId){ this.androidId = androidId; }

	public String getInstallEventName() { return installEventName; }
	public void setInstallEventName(String name) { installEventName = name; }

	public String getOpenEventName() { return openEventName; }
	public void setOpenEventName(String name) { openEventName = name; }

	public boolean getFireAutomaticInstallEvent() { return fireAutomaticInstallEvent; }
	public void setFireAutomaticInstallEvent(boolean fire) { fireAutomaticInstallEvent = fire; }

	public boolean getFireAutomaticOpenEvent() { return fireAutomaticOpenEvent; }
	public void setFireAutomaticOpenEvent(boolean fire) { fireAutomaticOpenEvent = fire; }

	public boolean getCollectAdvertisingId() { return collectAdvertisingId; }
	public void setCollectAdvertisingId(boolean collect) { collectAdvertisingId = collect; }

	public boolean getUseWordOfMouth(){return useWordOfMouth;}
	public void setUseWordOfMouth(boolean v){ useWordOfMouth = v;}

	public Retry.Strategy getDataCollectionRetryStrategy() {
		return dataCollectionRetryStrategy;
	}

	public void setDataCollectionRetryStrategy(Retry.Strategy dataCollectionRetryStrategy) {
		this.dataCollectionRetryStrategy = dataCollectionRetryStrategy;
	}

	public Retry.Strategy getUserFacingRequestRetryStrategy() {
		return userFacingRequestRetryStrategy;
	}

	public void setUserFacingRequestRetryStrategy(Retry.Strategy userFacingRequestRetryStrategy) {
		this.userFacingRequestRetryStrategy = userFacingRequestRetryStrategy;
	}
}
