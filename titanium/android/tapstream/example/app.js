// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


// open a single window
var win = Ti.UI.createWindow({
	backgroundColor:'white'
});
win.open();


var tapstream = require('com.tapstream.titanium');
Ti.API.info("module is => " + tapstream);

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
