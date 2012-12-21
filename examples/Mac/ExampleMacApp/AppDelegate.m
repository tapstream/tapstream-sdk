//
//  AppDelegate.m
//  ExampleMacApp
//
//  Created by Eric Roy on 2012-12-15.
//  Copyright (c) 2012 Example. All rights reserved.
//

#import "AppDelegate.h"
#import "Tapstream.h"

@implementation AppDelegate

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    // Insert code here to initialize your application
    
    [Tapstream createWithAccountName:@"sdktest" developerSecret:@"YGP2pezGTI6ec48uti4o1w"];
    
    Tapstream *tracker = [Tapstream instance];
    
    
    Event *e = [Event eventWithName:@"test-event" oneTimeOnly:NO];
    [e addValue:@"John Doe" forKey:@"player"];
    [e addIntegerValue:5 forKey:@"score"];
    [tracker fireEvent:e];
    
    e = [Event eventWithName:@"test-event-oto" oneTimeOnly:YES];
    [tracker fireEvent:e];
    
    Hit *h = [Hit hitWithTrackerName:@"test-tracker"];
    [h addTag:@"tag1"];
    [h addTag:@"tag2"];
    [tracker fireHit:h completion:^(Response *response) {
        if (response.status >= 200 && response.status < 300)
        {
            // Success
        }
        else
        {
            // Error
        }
    }];

}

@end
