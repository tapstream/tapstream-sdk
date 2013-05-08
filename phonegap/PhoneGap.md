# PhoneGap integration instructions

This document assumes you are using PhoneGap to target both Android and iOS.  "Android project" and "iOS project" will refer
to the native projects that you generated with the PhoneGap tools. "Tapstream PhoneGap SDK" will refer to the archive containing
the Tapstream SDK and PhoneGap plugin files that you are integrating into your projects.


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
        window.tapstream.create('TAPSTREAM_ACCOUNT_NAME', 'DEV_SECRET_KEY', {});

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

## Changing the default behavior of the Tapstream SDK

**Note**: Changing this behavior is not usually required.

To change the default Tapstream config, provide config overrides like this:

    :::javascript
    window.tapstream.create('TAPSTREAM_ACCOUNT_NAME', 'DEV_SECRET_KEY', {
        collectWifiMac: false,
        secureUdid: '<SecureUDID goes here>',
        idfa: '<IDFA goes here>',
        collectDeviceId: true,
        installEventName: 'custom-install-event-name',
    });

Consult the platform-specific SDK documentation to see what config variables are available.  Don't use accessor methods, just set the variables directly, using camel-case capitalization
