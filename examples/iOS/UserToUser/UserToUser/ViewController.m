//
//  ViewController.m
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "ViewController.h"
#import "TSTapstream.h"
#import "TSUserToUserController.h"

@interface ViewController ()

@end

@implementation ViewController

@synthesize insertionPointText;

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    self.view.layer.backgroundColor = [UIColor colorWithRed:0.6 green:0.8 blue:1.0 alpha:1.0].CGColor;
}

- (IBAction)onTestInsertionPoint:(id)sender
{
    ((UIButton *)sender).enabled = NO;

    NSLog(@"Requesting offer");
    
    TSUserToUserController *u2u = [TSTapstream userToUserController];
    
    NSString *insertionPoint = self.insertionPointText.text;
    [u2u offerForInsertionPoint:insertionPoint result:^(TSOffer *offer) {
        if(offer) {
            [u2u showOffer:offer parentViewController:self];
        }
        ((UIButton *)sender).enabled = YES;
    }];
}

- (IBAction)onMakeInstallTimeAgesAgo:(id)sender
{
    [[NSUserDefaults standardUserDefaults] setObject:[NSDate dateWithTimeIntervalSinceNow:-99999999999] forKey:@"__tapstream_install_date"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (IBAction)onEraseLastShownTimes:(id)sender
{
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"__tapstream_last_offer_impression_times"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (IBAction)onKill:(id)sender
{
    exit(0);
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
