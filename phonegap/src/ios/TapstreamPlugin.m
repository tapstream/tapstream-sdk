#import "TapstreamPlugin.h"
#import "TSTapstream.h"
#import <Cordova/CDV.h>

@implementation TapstreamPlugin

- (void)create:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult *pluginResult = nil;
    NSString *accountName = [command.arguments objectAtIndex:0];
    NSString *developerSecret = [command.arguments objectAtIndex:1];
    NSDictionary *configVals = [command.arguments objectAtIndex:2];
    
    if(accountName == nil || developerSecret == nil)
    {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    else
    {
        TSConfig *config = [TSConfig configWithDefaults];

        if(configVals != nil)
        {
            for(NSString *key in configVals)
            {
                if([config respondsToSelector:NSSelectorFromString(key)])
                {
                    NSObject *value = [configVals objectForKey:key];
                    [config setValue:value forKey:key];
                }
                else
                {
                    NSLog(@"Ignoring config field named '%@', probably not meant for this platform.", key);
                }
            }
        }

        [TSTapstream createWithAccountName:accountName developerSecret:developerSecret config:config];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)fireEvent:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult *pluginResult = nil;
    NSString *eventName = [command.arguments objectAtIndex:0];
    NSNumber *oneTimeOnly = [command.arguments objectAtIndex:1];
    NSDictionary *params = [command.arguments objectAtIndex:2];

    if((id)eventName == [NSNull null] || (id)oneTimeOnly == [NSNull null])
    {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    else
    {
        TSEvent *event = [TSEvent eventWithName:eventName oneTimeOnly:[oneTimeOnly boolValue]];
        
        if((id)params != [NSNull null])
        {
            for(NSString *key in params)
            {
                id value = [params objectForKey:key];
                [event addValue:value forKey:key];
            }
        }

        TSTapstream *tracker = [TSTapstream instance];
        [tracker fireEvent:event];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end