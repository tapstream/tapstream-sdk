# PhoneGap integration instructions

This document assumes you are using PhoneGap to target both Android and iOS.  "Android project" and "iOS project" will refer
to the native projects that you generated with the PhoneGap tools. "Tapstream PhoneGap SDK" will refer to the archive containing
the Tapstream SDK and PhoneGap plugin files that you are integrating into your projects.

## Preventing conflicts with Tapstream's JavaScript

If you're using your website inside of PhoneGap, and your website loads Tapstream's JavaScript, you need to modify your Tapstream JavaScript before proceeding. (Otherwise, proceed to the next section.)

First, add the following JavaScript snippet in PhoneGap so that it fires before `onload`:

    :::javascript
    window.__ts_suppress = true;

Then, modify your site's Tapstream JavaScript from this:

    :::javascript
    <script type="text/javascript">

    var _tsq = _tsq || [];
    _tsq.push(["setAccountName", "ergerg"]);
    _tsq.push(["fireHit", "javascript_tracker", []]);

    (function() {
        function z(){
            var s = document.createElement("script");
            s.type = "text/javascript";
    ...

to this:

    :::javascript
    <script type="text/javascript">

    var _tsq = _tsq || [];
    _tsq.push(["setAccountName", "ergerg"]);
    _tsq.push(["fireHit", "javascript_tracker", []]);

    (function() {
        function z(){
            // Return if the PhoneGap-only window variable is set
            if(window.__ts_suppress) return;
            var s = document.createElement("script");
            s.type = "text/javascript";
    ...

This will prevent Tapstream's JavaScript from firing hits from within your app.

## For the Android project

* Add an entry for the Tapstream plugin to the plugins list in `config.xml`

        :::xml
        <plugin name="Tapstream" value="com.tapstream.phonegap.TapstreamPlugin"/>

* Add the following domain whitelist entry to `config.xml`

        :::xml
        <access origin="https://api.tapstream.com" />

* Copy `Tapstream.jar` from the Tapstream PhoneGap SDK into the `libs` folder of your Android project.
* Add the following permissions to the `AndroidManifest.xml` file in your Android project:

        :::xml
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
        <uses-permission android:name="android.permission.READ_PHONE_STATE" />

* Copy the `java_plugin/com` folder from the Tapstream PhoneGap SDK and paste it into your Android project's `src` folder,
merging the two folder structures.


## For the iOS project

* Add an entry for the Tapstream plugin to the plugins list in `config.xml`

        :::xml
        <plugin name="Tapstream" value="TSTapstreamPlugin" />

* Add the following domain whitelist entry to `config.xml`

        :::xml
        <access origin="https://api.tapstream.com" />

* Open your iOS project in Xcode.
* Drag `objc_plugin/TSTapstreamPlugin.h` and `objc_plugin/TSTapstreamPlugin.m` from the Tapstream PhoneGap SDK
and drop them into Xcode, depositing them in the `Plugins` folder.
* Drag the `Tapstream` folder from the Tapstream PhoneGap SDK and drop it into Xcode, as a child of the root project node.



## In your PhoneGap JavaScript files

* Copy `tapstream.js` from the Tapstream PhoneGap SDK and paste it in the `www/js` folder with the rest of your JavaScript source files.

* In your html file, __before__ importing the JavaScript for your various pages, import the Tapstream javascript file:

        :::xml
        <script type="text/javascript" src="js/tapstream.js"></script>

This will cause an object called `tapstream` to be attached to the global window object.

* Initialize Tapstream from your `onDeviceReady:` function like this:

        :::javascript
        window.tapstream.create('TAPSTREAM_ACCOUNT_NAME', 'TAPSTREAM_SDK_SECRET', {});

### Firing extra events

By default, Tapstream fires an event whenever a user runs the app. You can define further events for recording key actions in your app by using the syntax below:

    :::javascript
    // Regular event:
    window.tapstream.fireEvent('test-event', false);

    // Regular event with custom params:
    window.tapstream.fireEvent('test-event', false, {
        'my-custom-param': 3,
    });

    // One-time-only event:
    window.tapstream.fireEvent('install', true);

    // One-time-only event with custom params:
    window.tapstream.fireEvent('install', true, {
        'my-custom-param': 'hello world',
    });

**Note**: Custom event parameters are not exposed in Tapstream's dashboard. The key/value pairs are exposed via Tapstream's postback system and Conversion API. Custom parameters are usually used for integration with a third-party ad network or your in-house dashboard or CRM.

## Changing the default behavior of the Tapstream SDK

**Note**: Changing this behavior is not usually required.

To change the default Tapstream config, provide config overrides like this:

    :::javascript
    window.tapstream.create('TAPSTREAM_ACCOUNT_NAME', 'TAPSTREAM_SDK_SECRET', {
        collectWifiMac: false,
        secureUdid: '<SecureUDID goes here>',
        idfa: '<IDFA goes here>',
        collectDeviceId: true,
        installEventName: 'custom-install-event-name',
    });

Consult the platform-specific SDK documentation to see what config variables are available.  Don't use accessor methods, just set the variables directly, using camel-case capitalization

