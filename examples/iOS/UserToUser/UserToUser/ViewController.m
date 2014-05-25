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

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    self.view.layer.backgroundColor = [UIColor colorWithRed:1.0 green:0.0 blue:0.0 alpha:1.0].CGColor;
}

- (IBAction)onTestInsertionPoint:(id)sender
{
    ((UIButton *)sender).enabled = NO;

    NSLog(@"Requesting offer");
    
    TSUserToUserController *u2u = [TSTapstream userToUserController];
    
    [u2u offerForInsertionPoint:@"launch" result:^(TSOffer *offer) {
        if(offer) {
            [u2u showOffer:offer parentViewController:self];
        }
        ((UIButton *)sender).enabled = YES;
    }];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
