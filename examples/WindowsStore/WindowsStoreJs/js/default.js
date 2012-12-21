// For an introduction to the Blank template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkId=232509
(function () {
    "use strict";

    WinJS.Binding.optimizeBindingReferences = true;

    var app = WinJS.Application;
    var activation = Windows.ApplicationModel.Activation;
    
    var tapstream = TapstreamMetrics.Sdk;

    app.onactivated = function (args) {
        if (args.detail.kind === activation.ActivationKind.launch) {
            if (args.detail.previousExecutionState !== activation.ApplicationExecutionState.terminated) {
                // TODO: This application has been newly launched. Initialize
                // your application here.
            } else {
                // TODO: This application has been reactivated from suspension.
                // Restore application state here.
            }
            args.setPromise(WinJS.UI.processAll());

            tapstream.Tapstream.create("sdktest", "YGP2pezGTI6ec48uti4o1w");

            var tracker = tapstream.Tapstream.instance;

            var e = new tapstream.Event("test-event", false);
            e.addPair("player", "John Doe");
            e.addPair("score", 5);
            tracker.fireEvent(e);

            e = new tapstream.Event("test-event-oto", true);
            tracker.fireEvent(e);

            var h = new tapstream.Hit("test-tracker");
            h.addTag("tag1");
            h.addTag("tag2");
            tracker.fireHitAsync(h).then(function (response) {
                if (response.status >= 200 && response.status < 300) {
                    // Success
                }
                else {
                    // Error
                }
            });
        }
    };

    app.oncheckpoint = function (args) {
        // TODO: This application is about to be suspended. Save any state
        // that needs to persist across suspensions here. You might use the
        // WinJS.Application.sessionState object, which is automatically
        // saved and restored across suspension. If you need to complete an
        // asynchronous operation before your application is suspended, call
        // args.setPromise().
    };

    app.start();
})();
