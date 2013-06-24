// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


// open a single window
var win = Ti.UI.createWindow({
	backgroundColor:'white'
});
win.open();

// TODO: write your module tests here
var tapstream = require('com.tapstream.titanium');
Ti.API.info("module is => " + tapstream);

if (Ti.Platform.name == "android") {
	
	tapstream.create("sdktest", "YGP2pezGTI6ec48uti4o1w", config);
}

