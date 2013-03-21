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
}