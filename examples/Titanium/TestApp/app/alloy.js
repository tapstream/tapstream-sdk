// The contents of this file will be executed before any of
// your view controllers are ever executed, including the index.
// You have access to all functionality on the `Alloy` namespace.
//
// This is a great place to do any initialization for your app
// or create any global variables/functions that you'd like to
// make available throughout your app. You can easily make things
// accessible globally by attaching them to the `Alloy.Globals`
// object. For example:
//
// Alloy.Globals.someGlobalFunction = function(){};


// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


var tapstream = require('com.tapstream.sdk');

var config = {
	collectWifiMac: false,
    secureUdid: '<SecureUDID goes here>',
    idfa: '<IDFA goes here>',
    collectDeviceId: true,
    installEventName: 'custom-install-event-name',
};
tapstream.create("sdktest", "YGP2pezGTI6ec48uti4o1w", config);

// Regular event:
tapstream.fireEvent('test-event', false);

// Regular event with custom params:
tapstream.fireEvent('test-event', false, {
    'my-custom-param': 3,
});

// One-time-only event:
tapstream.fireEvent('install', true);

// One-time-only event with custom params:
tapstream.fireEvent('install', true, {
    'my-custom-param': 'hello world',
});