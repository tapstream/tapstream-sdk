//
//  ViewController.m
//  WordOfMouthNonArc
//
//  Created by Eric on 2014-05-31.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "ViewController.h"
#import "TSTapstream.h"
#import "TSWordOfMouthController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    NSLog(@"Requesting offer");
    
    TSWordOfMouthController *wom = [TSTapstream wordOfMouthController];
    
    [wom offerForInsertionPoint:@"launch" result:^(TSOffer *offer) {
        if(offer) {
            [wom showOffer:offer parentViewController:self];
        }
    }];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(10 * NSEC_PER_SEC)), dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSArray *rewards = [wom availableRewards];
        NSLog(@"Rewards: %@", rewards);
    });
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
