# {{ pretty_platform }} integration instructions





## Adding the SDK to your project

{% if platform == 'android' %}

* Extract Tapstream.jar from the SDK zip file.
* Copy Tapstream.jar into the "libs" subdirectory of your project (just create "libs" if it does not exist).
* In Eclipse's package explorer pane, right click on your project and choose "Refresh".
* To verify that Eclipse has discovered the Tapstream SDK, expand your project's "Android Dependencies" category.  It should now contain a reference to Tapstream.jar.
* Tapstream requires that your project request the "INTERNET" permission.  If your AndroidManifest.xml does not already contain it, add the following line as a child of the `<manifest>` element:

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

{% endif %}







## Importing and initializing the SDK

{% if platform == 'android' %}

In your project's main activity file, import the Tapstream SDK:

    :::java
    import com.tapstream.sdk.*;

Then, in the `onCreate` method of your main activity, create the `Tapstream` singleton with the account name and
developer secret that you've setup on the Tapstream website:

    :::java
    Tapstream.create(getApplicationContext(), "TAPSTREAM_ACCOUNT_NAME", "DEV_SECRET_KEY");

If you have access to some kind of unique hardware identifier, you may provide this upon creation.
The hardware identifier must be no more than 255 characters in length.  In this case, instantiation would look like this:

    :::java
    Tapstream.create(getApplicationContext(), "TAPSTREAM_ACCOUNT_NAME", "DEV_SECRET_KEY", "SOME_UNIQUE_HARDWARE_ID");

{% elif platform == 'ios' or platform == 'mac' %}

In your project's AppDelegate.m file, import the Tapstream SDK:

    :::objective-c
    #import "TSTapstream.h"

Then, in the {% if platform == 'ios' %}`-application:didFinishLaunchingWithOptions:`{% else %}`-applicationDidFinishLaunching:`{% endif %} method of the AppDelegate,
create the `TSTapstream` singleton with the account name and developer secret that you've setup on the Tapstream website:

    :::objective-c
    [TSTapstream createWithAccountName:@"TAPSTREAM_ACCOUNT_NAME" developerSecret:@"DEV_SECRET_KEY"];


If you have access to some kind of unique hardware identifier, you may provide this upon creation.
The hardware identifier must be no more than 255 characters in length.  In this case, instantiation would look like this:

    :::objective-c
    [TSTapstream createWithAccountName:@"TAPSTREAM_ACCOUNT_NAME" developerSecret:@"DEV_SECRET_KEY" hardware:@"SOME_UNIQUE_HARDWARE_ID"];

{% elif platform == 'win8' or platform == 'winphone' %}

In your project's main activity file, import the TapStream SDK:

    :::csharp
    using Tapstream.Sdk;

Then, in the constructor of your main application class, create the `Tapstream` singleton with the account name and
developer secret that you've setup on the Tapstream website:

    :::csharp
    Tapstream.Create("TAPSTREAM_ACCOUNT_NAME", "DEV_SECRET_KEY");

If you have access to some kind of unique hardware identifier, you may provide this upon creation.
The hardware identifier must be no more than 255 characters in length.  In this case, instantiation would look like this:

    :::csharp
    Tapstream.Create("TAPSTREAM_ACCOUNT_NAME", "DEV_SECRET_KEY", "SOME_UNIQUE_HARDWARE_ID");

{% endif %}







## Firing events

Now that the SDK is initialized, you may fire events from anywhere in your code.  Firing an event is simple and can be done like this:

{% if platform == 'android' %}
    :::java
    Event *e = new Event("activation", false);
    Tapstream.getInstance().fireEvent(e);

{% elif platform == 'ios' or platform == 'mac' %}
    :::objective-c
    TSEvent *e = [TSEvent eventWithName:@"activation" oneTimeOnly:NO];
    [[TSTapstream instance] fireEvent:e];

{% elif platform == 'win8' or platform == 'winphone' %}
    :::csharp
    Event *e = new Event("activation", false);
    Tapstream.Instance.FireEvent(e);

{% endif %}

This example fires an event called "activation" which might be an appropriate name for a first activation event, but you may call your events anything you like. Event names are case insensitive.

The ***Tapstream SDK is threadsafe***, so you may fire events from any thread you wish.

If you want to fire an event that will only happen once in the lifetime of the application, you may create a "one time only" event as follows:

{% if platform == 'android' %}
    :::java
    Event *e = new Event("activation", true);
    Tapstream.getInstance().fireEvent(e);

{% elif platform == 'ios' or platform == 'mac' %}
    :::objective-c
    TSEvent *e = [TSEvent eventWithName:@"activation" oneTimeOnly:YES];
    [[TSTapstream instance] fireEvent:e];

{% elif platform == 'win8' or platform == 'winphone' %}
    :::csharp
    Event *e = new Event("activation", true);
    Tapstream.Instance.FireEvent(e);

{% endif %}

One-time only events will reach the Tapstream server ***exactly once***, even if you fire them multiple times in your code.








## Firing events with custom parameters

Tapstream also allows you to attach key/value pairs to your events.  The keys and values must be no more than 255 characters each (once in string form).
In the following example, two events are fired.  Both contain key/value pairs, and one is "one time only" while the other is not:

{% if platform == 'android' %}
    :::java
    Tapstream tracker = Tapstream.getInstance();

    Event *e = new Event("installed", true);
    e.addPair("username", "test-user");
    tracker.fireEvent(e);

    e = new Event("level-complete", false);
    e.addPair("score", 15000);
    e.addPair("skill", "easy");
    tracker.fireEvent(e);

{% elif platform == 'ios' or platform == 'mac' %}
    :::objective-c
    TSTapstream *tracker = [TSTapstream instance];

    TSEvent *e = [TSEvent eventWithName:@"installed" oneTimeOnly:YES];
    [e addValue:@"test-user" forKey:@"username"];
    [tracker fireEvent:e];

    e = [TSEvent eventWithName:@"level-complete" oneTimeOnly:NO];
    [e addIntegerValue:15000 forKey:@"score"];
    [e addValue:@"easy" forKey:@"skill"];
    [tracker fireEvent:e];

{% elif platform == 'win8' or platform == 'winphone' %}
    :::csharp
    Tapstream tracker = Tapstream.Instance;

    Event *e = new Event("installed", true);
    e.AddPair("username", "test-user");
    tracker.FireEvent(e);

    e = new Event("level-complete", false);
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








## Firing hits

Although hits are usually created in a browser, it is possible to fire hits from the Tapstream SDK.  Here's an example:

{% if platform == 'android' %}
    :::java
    Hit h = new Hit("my-hit-tracker-name");
    h.addTag("tag-1");
    h.addTag("tag-2");
    Tapstream.getInstance().fireHit(h, new Hit.CompletionHandler() {
        @Override
        public void complete(Response response) {
            if(response.status >= 200 && response.status < 300) {
                // Success
            } else {
                // Error
            }
        }
    });

{% elif platform == 'ios' or platform == 'mac' %}
    :::objective-c
    TSHit *h = [TSHit hitWithTrackerName:@"my-hit-tracker-name"];
    [h addTag:@"tag-1"];
    [h addTag:@"tag-2"];
    [[TSTapstream instance] fireHit:h completion:^(TSResponse *response) {
        if (response.status >= 200 && response.status < 300)
        {
            // Success
        }
        else
        {
            // Error
        }
    }];

{% elif platform == 'win8' or platform == 'winphone' %}
    :::csharp
    Hit h = new Hit("my-hit-tracker-name");
    h.AddTag("tag-1");
    h.AddTag("tag-2");
    Task.Run(async () =>
    {
        Response response = await Tapstream.Instance.FireHitAsync(h);
        if (response.Status >= 200 && response.Status < 300)
        {
            // Success
        }
        else
        {
            // Error
        }
    });

{% endif %}


The hit completion handler is optional, and you may pass {% if platform == 'ios' or platform == 'mac' %}`nil`{% else %}`null`{% endif %} if you prefer.
