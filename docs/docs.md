# {{ pretty_platform }} integration instructions





## Adding the SDK to your project

{% if platform == 'android' %}

* Extract Tapstream.jar from the SDK zip file.
* Copy Tapstream.jar into the "libs" subdirectory of your project (just create "libs" if it does not exist).
* In Eclipse's package explorer pane, right click on your project and choose "Refresh".
* To verify that Eclipse has discovered the Tapstream SDK, expand your project's "Android Dependencies" category.  It should now contain a reference to Tapstream.jar.
* Tapstream requires that your project request the "INTERNET" permission.  If your AndroidManifest.xml does not already contain it, add the following line as a child of the `<manifest>` element:

&nbsp;

    :::java
    <uses-permission android:name="android.permission.INTERNET" />

{% elif platform == 'ios' or platform == 'mac' %}

* Extract the SDK zip file.
* Copy the Tapstream folder and paste it into your project directory.
* Open your Xcode project.
* Drag the Tapstream folder from the Finder window and drop it into Xcode's Project Navigator.  It should be placed as a child of root project node.

{% elif platform == 'win8' %}

* Extract TapstreamMetrics.winmd from the SDK zip file.
* Open your project in Visual Studio.
* Right click on your project in the Solution Explorer, and select "Add Reference...".
* Click the "Browse" button and select TapstreamMetrics.winmd from the location you extracted it to.

{% elif platform == 'winphone' %}

* Extract TapstreamMetrics.dll from the SDK zip file.
* Open your project in Visual Studio.
* Right click on your project in the Solution Explorer, and select "Add Reference...".
* Click the "Browse" button and select TapstreamMetrics.dll from the location you extracted it to.
* In your WMAppManifest.xml file, make sure that you have requested the ID_CAP_NETWORKING and ID_CAP_IDENTIFY_DEVICE capabilities.

{% endif %}







## Importing and initializing the SDK

{% if platform == 'android' %}

In your project's main activity file, import the Tapstream SDK:

    :::java
    import com.tapstream.sdk.*;

Then, in the `onCreate` method of your main activity, create the `Tapstream` singleton with the account name and developer secret that you've setup on the Tapstream website:

    :::java
    Config config = new Config();
    Tapstream.create(getApplicationContext(), "TAPSTREAM_ACCOUNT_NAME", "DEV_SECRET_KEY", config);

{% elif platform == 'ios' or platform == 'mac' %}

In your project's AppDelegate.m file, import the Tapstream SDK:

    :::objective-c
    #import "TSTapstream.h"

Then, in the {% if platform == 'ios' %}`-application:didFinishLaunchingWithOptions:`{% else %}`-applicationDidFinishLaunching:`{% endif %} method of the AppDelegate, create the `TSTapstream` singleton with the account name and developer secret that you've setup on the Tapstream website:

    :::objective-c
    TSConfig *config = [TSConfig configWithDefaults];
    [TSTapstream createWithAccountName:@"TAPSTREAM_ACCOUNT_NAME" developerSecret:@"DEV_SECRET_KEY" config:config];

{% elif platform == 'win8' or platform == 'winphone' %}

In your project's main activity file, import the TapStream SDK:

    :::csharp
    using Tapstream.Sdk;

Then, in the constructor of your main application class, create the `Tapstream` singleton with the account name and
developer secret that you've setup on the Tapstream website:

    :::csharp
    Config = new Config();
    Tapstream.Create("TAPSTREAM_ACCOUNT_NAME", "DEV_SECRET_KEY", config);

{% endif %}




## Collecting hardware identifiers

{% if platform != 'winphone' %}
The Tapstream SDK can send various hardware identifiers to the Tapstream server with each event.  Some of these hardware identifiers are
collected automatically, and you must opt-out if you do not wish to collect them.  Others are opt-in, and must be collected by you and
provided explicitly to the SDK.

To control which hardware identifiers are attached to events, you may modify the config object
that you instantiated the SDK with.

Here's an example:
{% endif %}

{% if platform == 'android' %}

    :::java
    Config config = new Config();

    // These hardware identifiers will be automatically collected and sent
    // unless you opt-out by setting them to false, as shown below.

    config.setCollectWifiMac(false);
    config.setCollectDeviceId(false);
    config.setCollectAndroidId(false);

    // These hardware identifiers are not collected automatically.
    // If you wish to send them, you must opt-in by providing values, as shown here:
    config.setOdin1("<ODIN-1 value goes here>");
    config.setOpenUdid("<OpenUDID value goes here>");

    Tapstream.create(getApplicationContext(), "TAPSTREAM_ACCOUNT_NAME", "DEV_SECRET_KEY", config);

To generate the device's ODIN-1 identifier, please see the [ODIN-1 documentation](https://code.google.com/p/odinmobile/wiki/ODIN1).

To get the device's OpenUDID identifier, please see the [OpenUDID source code](https://github.com/ylechelle/OpenUDID).

{% elif platform == 'ios' %}

    :::objective-c
    Config *config = [Config configWithDefaults];

    // This hardware identifier will be automatically collected and sent
    // unless you opt-out by setting it to NO, as shown here:
    config.collectWifiMac = NO;

    // These hardware identifiers are not collected automatically.
    // If you wish to send them, you must opt-in by providing values, as shown here:
    config.odin1 = @"<ODIN-1 value goes here>";
    config.udid = @"<UDID value goes here>";
    config.idfa = @"<IDFA value goes here>";
    config.openUdid = @"<OpenUDID value goes here>";
    config.secureUdid = @"<SecureUDID value goes here>";

    [TSTapstream createWithAccountName:@"TAPSTREAM_ACCOUNT_NAME" developerSecret:@"DEV_SECRET_KEY" config:config];

**We strongly recommend that you collect and provide the IDFA value to the Tapstream SDK.** Please see [Apple's documentation on collecting the IDFA](http://developer.apple.com/library/ios/#documentation/AdSupport/Reference/ASIdentifierManager_Ref/ASIdentifierManager.html).

To generate the device's ODIN-1 identifier, please see the [ODIN-1 documentation](https://code.google.com/p/odinmobile/wiki/ODIN1).

To get the device's OpenUDID identifier, please see the [OpenUDID source code](https://github.com/ylechelle/OpenUDID).

To get the device's SecureUDID identifier, please see the [Official SecureUDID page](http://www.secureudid.org/).

{% elif platform == 'mac' %}

    :::objective-c
    Config *config = [Config configWithDefaults];

    // This hardware identifier will be automatically collected and sent
    // unless you opt-out by setting it to NO, as shown here:
    config.collectWifiMac = NO;

    // These hardware identifiers are not collected automatically.
    // If you wish to send them, you must opt-in by providing values, as shown here:
    config.odin1 = @"<ODIN-1 value goes here>";
    config.serialNumber = @"<Serial number value goes here>";

    [TSTapstream createWithAccountName:@"TAPSTREAM_ACCOUNT_NAME" developerSecret:@"DEV_SECRET_KEY" config:config];

To generate the device's ODIN-1 identifier, please see the [ODIN-1 documentation](https://code.google.com/p/odinmobile/wiki/ODIN1).

{% elif platform == 'win8' %}

    :::csharp
    Config config = new Config();

    // This hardware identifier will be automatically collected and sent
    // unless you opt-out by setting it to false, as shown here:
    config.CollectAppSpecificHardwareId = false;

    // This hardware identifier will not be collected automatically.
    // If you wish to send it, you must opt-in by providing a value, as shown here:
    config.Odin1 = "<ODIN-1 value goes here>";

    Tapstream.Create("TAPSTREAM_ACCOUNT_NAME", "DEV_SECRET_KEY", config);

To generate the device's ODIN-1 identifier, please see the [ODIN-1 documentation](https://code.google.com/p/odinmobile/wiki/ODIN1).

{% elif platform == 'winphone' %}

The Tapstream SDK can send various hardware identifiers to the Tapstream server with each event.

To control which hardware identifiers are attached to events, you may modify the config object that you instantiated
the SDK with.

Here's an example:

    :::csharp
    Config config = new Config();

    // This hardware identifier will be automatically collected and sent
    // unless you opt-out by setting it to false, as shown here:
    config.CollectDeviceUniqueId = false;

    // This hardware identifier will not be collected automatically.
    // If you wish to send it, you must opt-in by providing a value, as shown here:
    config.Odin1 = "<ODIN-1 value goes here>";

    Tapstream.Create("TAPSTREAM_ACCOUNT_NAME", "DEV_SECRET_KEY", config);

To generate the device's ODIN-1 identifier, please see the [ODIN-1 documentation](https://code.google.com/p/odinmobile/wiki/ODIN1).

{% endif %}




## Firing SDK events

The SDK will fire two types of events automatically:

* An install event, the first time the app runs
* An open event, every time the app runs.

The install event is called `[platform]-[appname]-install`, and the open event is called `[platform]-[appname]-open`, where `[platform]` is the device's platform (iOS, Android, etc.) and `[appname]` is your app's shortname.

These automatic events can be renamed or suppressed entirely by modifying the config object that you instantiated the sdk with.  For example, if you wanted to rename the install event and suppress the open event you would set the config object like this:

{% if platform == 'android' %}
    :::java
    config.setInstallEventName("my-install-event");
    config.setFireAutomaticOpenEvent(false);

{% elif platform == 'ios' or platform == 'mac' %}
    :::objective-c
    config.installEventName = @"my-install-event";
    config.fireAutomaticOpenEvent = NO;

{% elif platform == 'win8' or platform == 'winphone' %}
    :::csharp
    config.InstallEventName = "my-install-event";
    config.FireAutomaticOpenEvent = false;

{% endif %}


### Additional SDK events

Now that the SDK is initialized, you may fire events from anywhere in your code.  This is useful for tracking engagement events, in-app purchases, and other LTV metrics.

Firing an event is simple and can be done like this:

{% if platform == 'android' %}
    :::java
    Event *e = new Event("purchase", false);
    Tapstream.getInstance().fireEvent(e);

{% elif platform == 'ios' or platform == 'mac' %}
    :::objective-c
    TSEvent *e = [TSEvent eventWithName:@"purchase" oneTimeOnly:NO];
    [[TSTapstream instance] fireEvent:e];

{% elif platform == 'win8' or platform == 'winphone' %}
    :::csharp
    Event *e = new Event("purchase", false);
    Tapstream.Instance.FireEvent(e);

{% endif %}

This example fires an event called `purchase`, which might be an appropriate name for an in-app purchase event, but you may call your events anything you like. Event names are case insensitive.

The ***Tapstream SDK is threadsafe***, so you may fire events from any thread you wish.




## Firing events with custom parameters

Tapstream also allows you to attach key/value pairs to your events.  The keys and values must be no more than 255 characters each (once in string form).

In the following example, an event called `level-complete` with custom parameters for `score` and `skill` is fired.

{% if platform == 'android' %}
    :::java
    Tapstream tracker = Tapstream.getInstance();

    Event *e = new Event("level-complete", false);
    e.addPair("score", 15000);
    e.addPair("skill", "easy");
    tracker.fireEvent(e);

{% elif platform == 'ios' or platform == 'mac' %}
    :::objective-c
    TSTapstream *tracker = [TSTapstream instance];

    TSEvent *e = [TSEvent eventWithName:@"level-complete" oneTimeOnly:NO];
    [e addIntegerValue:15000 forKey:@"score"];
    [e addValue:@"easy" forKey:@"skill"];
    [tracker fireEvent:e];

{% elif platform == 'win8' or platform == 'winphone' %}
    :::csharp
    Tapstream tracker = Tapstream.Instance;

    Event *e = new Event("level-complete", false);
    e.AddPair("score", 15000);
    e.AddPair("skill", "easy")
    tracker.FireEvent(e);

{% endif %}








## Controlling logging

The log output of Tapstream can be redirected (or quelled) by providing a handler to receive the messages.

Here's how you might redirect Tapstream messages to a custom logging system:

{% if platform == 'android' %}
    :::java
    Logging.setLogger(new Logger() {
        @Override
        public void log(int logLevel, String message) {
            MyCustomLoggingSystem(message);
        }
    });

{% elif platform == 'ios' or platform == 'mac' %}
    :::objective-c
    // You'll need:  #import "TSLogging.h"
    [TSLogging setLogger:^(int logLevel, NSString *message) {
        MyCustomLoggingSystem(message);
    }];

{% elif platform == 'win8' or platform == 'winphone' %}
    :::csharp
    class ConsoleLogger : Logger
    {
        public void Log(LogLevel level, string msg)
        {
            MyCustomLoggingSystem(msg);
        }
    }

    ...

    Logging.SetLogger(new ConsoleLogger());

{% endif %}




