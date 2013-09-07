#import <Cordova/CDV.h>

@interface TSTapstreamPlugin : CDVPlugin

- (void)create:(CDVInvokedUrlCommand *)command;
- (void)fireEvent:(CDVInvokedUrlCommand *)command;

@end