package com.tapstream.sdk;

public class Config {
	// Deprecated, hardware-id field
	private String hardware = null;

	// Optional hardware identifiers that can be provided by the caller
	private String odin1 = null;
	private String openUdid = null;

	// Set these to false if you do NOT want to collect this data.
	private boolean collectWifiMac = true;
	private boolean collectDeviceId = true;
	private boolean collectAndroidId = true;

	// Set these if you want to override the names of the automatic events sent by the sdk
	private String installEventName = null;
	private String openEventName = null;

	// Unset these if you want to disable the sending of the automatic events
	private boolean fireAutomaticInstallEvent = true;
	private boolean fireAutomaticOpenEvent = true;


	// Accessors for the private members above:
	public String getHardware() { return hardware; }
	public void setHardware(String hardware) { this.hardware = hardware; }

	public String getOdin1() { return odin1; }
	public void setOdin1(String odin1) { this.odin1 = odin1; }

	public String getOpenUdid() { return openUdid; }
	public void setOpenUdid(String openUdid) { this.openUdid = openUdid; }

	public boolean getCollectWifiMac() { return collectWifiMac; }
	public void setCollectWifiMac(boolean collect) { this.collectWifiMac = collect; }

	public boolean getCollectDeviceId() { return collectDeviceId; }
	public void setCollectDeviceId(boolean collect) { this.collectDeviceId = collect; }

	public boolean getCollectAndroidId() { return collectAndroidId; }
	public void setCollectAndroidId(boolean collect) { this.collectAndroidId = collect; }

	public String getInstallEventName() { return installEventName; }
	public void setInstallEventName(String name) { this.installEventName = name; }

	public String getOpenEventName() { return openEventName; }
	public void setOpenEventName(String name) { this.openEventName = name; }

	public boolean getFireAutomaticInstallEvent() { return fireAutomaticInstallEvent; }
	public void setFireAutomaticInstallEvent(boolean fire) { this.fireAutomaticInstallEvent = fire; }

	public boolean getFireAutomaticOpenEvent() { return fireAutomaticOpenEvent; }
	public void setFireAutomaticOpenEvent(boolean fire) { this.fireAutomaticOpenEvent = fire; }
}