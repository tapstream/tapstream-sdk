package com.tapstream.sdk;

import java.util.HashMap;
import java.util.Map;

public class Config {	

	// Deprecated, hardware-id field
	private String hardware = null;

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
	
	// Unset this if you want to disable the collection of taste data
	private boolean collectTasteData = true;

	// Unset this if you want to disable automatic collection of Android Advertising Id
	private boolean collectAdvertisingId = true;
	
	// These parameters will be automatically attached to all events fired by the sdk.
	public Map<String, Object> globalEventParams = new HashMap<String, Object>();

	// Accessors for the private members above:
	public String getHardware() { return hardware; }
	public void setHardware(String hardware) { this.hardware = hardware; }

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

        /**
         * @deprecated Wifi mac, device id, and android id are no longer collected
         * automatically. You may provide them using `setWifiMac`, `setDeviceId`,
         * and `setAndroidId`.
         */
	@Deprecated
	public boolean getCollectWifiMac() { return false; }
	@Deprecated
	public void setCollectWifiMac(boolean collect) {}

	@Deprecated
	public boolean getCollectDeviceId() { return false; }
	@Deprecated
	public void setCollectDeviceId(boolean collect) {}

	@Deprecated
	public boolean getCollectAndroidId() { return false; }
	@Deprecated
	public void setCollectAndroidId(boolean collect) { }

	public String getInstallEventName() { return installEventName; }
	public void setInstallEventName(String name) { installEventName = name; }

	public String getOpenEventName() { return openEventName; }
	public void setOpenEventName(String name) { openEventName = name; }

	public boolean getFireAutomaticInstallEvent() { return fireAutomaticInstallEvent; }
	public void setFireAutomaticInstallEvent(boolean fire) { fireAutomaticInstallEvent = fire; }

	public boolean getFireAutomaticOpenEvent() { return fireAutomaticOpenEvent; }
	public void setFireAutomaticOpenEvent(boolean fire) { fireAutomaticOpenEvent = fire; }
	
	public boolean getCollectTasteData() { return collectTasteData; }
	public void setCollectTasteData(boolean collect) { collectTasteData = collect; }

	public boolean getCollectAdvertisingId() { return collectAdvertisingId; }
	public void setCollectAdvertisingId(boolean collect) { collectAdvertisingId = collect; }
}
