# PhoneGap integration instructions

## For the Android project:

* Add the following to your plugin list in config.xml:

&nbsp;

	:::xml
	<plugin name="Tapstream" value="com.tapstream.phonegap.TapstreamPlugin"/>

* Download and extract the latest version of the Tapstream Android SDK.
* Copy Tapstream.jar from the Android SDK into your libs folder
* In your AndroidManifest.xml, add the following permissions:

&nbsp;

	:::xml
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	<uses-permission android:name="android.permission.READ_PHONE_STATE" />

* Copy the source code for the native java plugin (get the whole folder structure: com/tapstream/phonegap/TapstreamPlugin.java)
and paste it into the project's src directory, merging the folder structures.


## For the iOS project:

* Add the following to your plugin list in config.xml:

&nbsp;

	:::xml
	<plugin name="Tapstream" value="TSTapstreamPlugin" />

* In XCode, drag and drop TSTapstreamPlugin.h and TSTapstreamPlugin.m into your project's Plugins folder
* Download and extract the lastest version of the Tapstream iOS SDK.
* In Xcode, drag the Tapstream folder from the iOS SDK and drop it into your project.



## In your PhoneGap files:

* Add a domain whitelist entry to your config.xml:
<access origin="https://api.tapstream.com" />

* In your html file, before importing the javascript for your various pages, import the tapstream javascript file. Eg:

&nbsp;

	:::xml
	<script type="text/javascript" src="js/tapstream.js"></script>

This will attach a tapstream object to the window object.

* Initialize the Tapstream sdk from your code like this:

&nbsp;

	:::javascript
	window.tapstream.create('sdktest', 'YGP2pezGTI6ec48uti4o1w', {});

* To change the default Tapstream config, provide config overrides like this:

&nbsp;

	:::javascript
	window.tapstream.create('sdktest', 'YGP2pezGTI6ec48uti4o1w', {
		collectWifiMac: false,
		secureUdid: '<udid goes here>',
		idfa: '<idfa goes here>',
		collectDeviceId: true,
		installEventName: 'custom-install-event-name',
	});

(Consult the platform specific sdk documentation to see what config variables are available.  Don't use accessor methods, just set the variables directly, using camel-case)



* To fire an event, do something like this:

&nbsp;

	:::javascript
	window.tapstream.fireEvent('test-event', false);
	// or:
	window.tapstream.fireEvent('test-event', false, {
	    'optional-param': 3,
	});

* To fire a one-time-only event:

&nbsp;

	:::javascript
	window.tapstream.fireEvent('install', true);
	// or:
	window.tapstream.fireEvent('install', true, {
	    'optional-param': 3,
	});