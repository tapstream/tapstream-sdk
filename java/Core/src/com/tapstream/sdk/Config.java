package com.tapstream.sdk;

public class Config {
	// Deprecated, hardware-id field
	private String hardware = null;

	// Set these to false if you do NOT want to collect this data.
	private boolean collectWifiMac = true;
	private boolean collectDeviceId = true;
	private boolean collectAndroidId = true;



	public String getHardware() { return hardware; }
	public void setHardware(String hardware) { this.hardware = hardware; }

	public boolean getCollectWifiMac() { return collectWifiMac; }
	public void setCollectWifiMac(boolean collect) { this.collectWifiMac = collect; }

	public boolean getCollectDeviceId() { return collectDeviceId; }
	public void setCollectDeviceId(boolean collect) { this.collectDeviceId = collect; }

	public boolean getCollectAndroidId() { return collectAndroidId; }
	public void setCollectAndroidId(boolean collect) { this.collectAndroidId = collect; }
}