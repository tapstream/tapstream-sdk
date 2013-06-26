/**
 * Your Copyright Here
 *
 * Appcelerator Titanium is Copyright (c) 2009-2010 by Appcelerator, Inc.
 * and licensed under the Apache Public License (version 2)
 */
#import "ComTapstreamSdkModule.h"
#import "TiBase.h"
#import "TiHost.h"
#import "TiUtils.h"

#import "TSTapstream.h"
#import "TSEvent.h"
#import "TSConfig.h"

@implementation ComTapstreamSdkModule

#pragma mark Internal

// this is generated for your module, please do not change it
-(id)moduleGUID
{
	return @"c29651c0-c056-44ab-b920-e1cf024f319a";
}

// this is generated for your module, please do not change it
-(NSString*)moduleId
{
	return @"com.tapstream.sdk";
}

#pragma mark Lifecycle

-(void)startup
{
	// this method is called when the module is first loaded
	// you *must* call the superclass
	[super startup];
	
	NSLog(@"[INFO] %@ loaded",self);
}

-(void)shutdown:(id)sender
{
	// this method is called when the module is being unloaded
	// typically this is during shutdown. make sure you don't do too
	// much processing here or the app will be quit forceably
	
	// you *must* call the superclass
	[super shutdown:sender];
}

#pragma mark Cleanup 

-(void)dealloc
{
	// release any resources that have been retained by the module
	[super dealloc];
}

#pragma mark Internal Memory Management

-(void)didReceiveMemoryWarning:(NSNotification*)notification
{
	// optionally release any resources that can be dynamically
	// reloaded once memory is available - such as caches
	[super didReceiveMemoryWarning:notification];
}

#pragma mark Listener Notifications

-(void)_listenerAdded:(NSString *)type count:(int)count
{
	if (count == 1 && [type isEqualToString:@"my_event"])
	{
		// the first (of potentially many) listener is being added 
		// for event named 'my_event'
	}
}

-(void)_listenerRemoved:(NSString *)type count:(int)count
{
	if (count == 0 && [type isEqualToString:@"my_event"])
	{
		// the last listener called for event named 'my_event' has
		// been removed, we can optionally clean up any resources
		// since no body is listening at this point for that event
	}
}

#pragma Public APIs

- (void)create:(id)args
{
    NSString *accountName = [args objectAtIndex:0];
    NSString *developerSecret = [args objectAtIndex:1];
    NSDictionary *configVals = [args objectAtIndex:2];
    
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
}

- (void)fireEvent:(id)args
{
    NSString *eventName = [args objectAtIndex:0];
    NSNumber *oneTimeOnly = [args objectAtIndex:1];
    NSDictionary *params = [args objectAtIndex:2];
    
    TSEvent *event = [TSEvent eventWithName:eventName oneTimeOnly:[oneTimeOnly boolValue]];
        
    if((id)params != [NSNull null])
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
}

@end
