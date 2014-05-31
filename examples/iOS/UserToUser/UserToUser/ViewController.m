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

@synthesize insertionPointText, rewardsList, rewardSku, rewards;

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    self.rewards = [NSMutableDictionary dictionary];
    self.view.layer.backgroundColor = [UIColor colorWithRed:0.6 green:0.8 blue:1.0 alpha:1.0].CGColor;
}

- (IBAction)onTestInsertionPoint:(id)sender
{
    ((UIButton *)sender).enabled = NO;

    NSLog(@"Requesting offer");
    
    TSUserToUserController *u2u = [TSTapstream userToUserController];
    
    [u2u offerForInsertionPoint:self.insertionPointText.text result:^(TSOffer *offer) {
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

- (IBAction)onRefreshRewards:(id)sender
{
    [(UIButton *)sender setEnabled:NO];
    [rewardsList setText:@""];
    self.rewards = [NSMutableDictionary dictionary];
    TSUserToUserController *u2u = [TSTapstream userToUserController];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSArray *results = [u2u availableRewards];
        dispatch_async(dispatch_get_main_queue(), ^{
            NSString *rewardSkus = @"";
            [results enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
                [rewardSkus stringByAppendingString:[NSString stringWithFormat:@"%lu, %@\n", (unsigned long)((TSReward *)obj).ident, ((TSReward *)obj).name]];
                [self.rewards setObject:obj forKey:[[NSNumber numberWithInteger:((TSReward *)obj).ident] stringValue]];
            }];
            [rewardsList setText:rewardSkus];
            [(UIButton *)sender setEnabled:YES];
        });
    });
}

- (IBAction)onConsumeReward:(id)sender
{
    TSReward *reward = [self.rewards objectForKey:self.rewardSku.text];
    if(reward) {
        TSUserToUserController *u2u = [TSTapstream userToUserController];
        [u2u consumeReward:reward];
    } else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Invalid reward id"
                                                        message:@"Enter the numeric id of the reward to consume"
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

@end
