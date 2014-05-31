//
//  ViewController.h
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController<UITextFieldDelegate>

@property(strong) IBOutlet UITextField *insertionPointText;
@property(strong) IBOutlet UITextView *rewardsList;
@property(strong) IBOutlet UITextField *rewardSku;

@property(strong) NSMutableDictionary *rewards;

- (IBAction)onTestInsertionPoint:(id)sender;
- (IBAction)onMakeInstallTimeAgesAgo:(id)sender;
- (IBAction)onEraseLastShownTimes:(id)sender;
- (IBAction)onKill:(id)sender;
- (IBAction)onRefreshRewards:(id)sender;
- (IBAction)onConsumeReward:(id)sender;

@end
