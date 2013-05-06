#import "TSTapstreamPlugin.h"
#import "TSTapstream.h"
#import <Cordova/CDV.h>

@implementation TSTapstreamPlugin

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

    if(eventName == nil || oneTimeOnly == nil)
    {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    else
    {
        TSEvent *event = [TSEvent eventWithName:eventName oneTimeOnly:[oneTimeOnly boolValue]];
        
        if(params != nil)
        {
            for(NSString *key in params)
            {
                id value = [params objectForKey:key];
                if([value isKindOfClass:[NSString class]])
                {
                    [event addValue:(NSString *)value forKey:(NSString *)key];
                }
                else if([value isKindOfClass:[NSNumber class]])
                {
                    NSNumber *number = (NSNumber *)value;
                    
                    if(strcmp([number objCType], @encode(int)) == 0)
                    {
                        [event addIntegerValue:[number intValue] forKey:key];
                    }
                    else if(strcmp([number objCType], @encode(uint)) == 0)
                    {
                        [event addUnsignedIntegerValue:[number unsignedIntValue] forKey:key];
                    }
                    else if(strcmp([number objCType], @encode(double)) == 0 ||
                        strcmp([number objCType], @encode(float)) == 0)
                    {
                        [event addDoubleValue:[number doubleValue] forKey:key];
                    }
                    else if(strcmp([number objCType], @encode(BOOL)) == 0)
                    {
                        [event addBooleanValue:[number boolValue] forKey:key];
                    }
                    else
                    {
                        NSLog(@"Tapstream Event cannot accept an NSNumber param holding this type, skipping param");
                    }
                }
                else
                {
                    NSLog(@"Tapstream Event cannot accept a param of this type, skipping param");
                }
            }
        }

        TSTapstream *tracker = [TSTapstream instance];
        [tracker fireEvent:event];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end