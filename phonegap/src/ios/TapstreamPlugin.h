#import <Cordova/CDV.h>

@interface TapstreamPlugin : CDVPlugin

- (void)create:(CDVInvokedUrlCommand *)command;
- (void)fireEvent:(CDVInvokedUrlCommand *)command;

@end