//
//  AppDelegate.m
//  ExampleMacApp
//
//  Created by Eric Roy on 2012-12-15.
//  Copyright (c) 2012 Example. All rights reserved.
//

#import "AppDelegate.h"
#import "TSTapstream.h"

@implementation AppDelegate

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    // Insert code here to initialize your application
    TSConfig *config = [TSConfig configWithDefaults];
    [config.globalEventParams setValue:@25.4 forKey:@"degrees"];
    
    [TSTapstream createWithAccountName:@"sdktest" developerSecret:@"YGP2pezGTI6ec48uti4o1w" config:config];
    
    TSTapstream *tracker = [TSTapstream instance];
    [tracker getConversionData:^(NSData *jsonInfo) {
        if(jsonInfo == nil)
        {
            // No conversion data available
            NSLog(@"No conversion data");
        }
        else
        {
            NSLog(@"Conversion data: %@", [[NSString alloc] initWithData:jsonInfo encoding:NSUTF8StringEncoding]);
            NSError *error;
            NSArray *json = [NSJSONSerialization JSONObjectWithData:jsonInfo options:kNilOptions error:&error];
            if(json && !error)
            {
                // Read some data from this json object, and modify your application's behaviour accordingly
                // ...
            }
        }
    }];
    
    
    TSEvent *e = [TSEvent eventWithName:@"test-event" oneTimeOnly:NO];
    [e addValue:@"John Doe" forKey:@"player"];
    [e addValue:@10.1 forKey:@"degrees"];
    [e addValue:@5 forKey:@"score"];
    [tracker fireEvent:e];

}

@end
