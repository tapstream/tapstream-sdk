var Alloy = require("alloy"), _ = Alloy._, Backbone = Alloy.Backbone;

var tapstream = require("com.tapstream.sdk");

var config = {
    collectWifiMac: false,
    secureUdid: "<SecureUDID goes here>",
    idfa: "<IDFA goes here>",
    collectDeviceId: true,
    installEventName: "custom-install-event-name"
};

tapstream.create("sdktest", "YGP2pezGTI6ec48uti4o1w", config);

tapstream.fireEvent("test-event", false);

tapstream.fireEvent("test-event", false, {
    "my-custom-param": 3
});

tapstream.fireEvent("install", true);

tapstream.fireEvent("install", true, {
    "my-custom-param": "hello world"
});

Alloy.createController("index");