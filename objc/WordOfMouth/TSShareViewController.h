//
//  TSShareViewController.h
//  WordOfMouth
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Social/SLComposeViewController.h"
#import "Social/SLServiceTypes.h"
#import "MessageUI/MFMailComposeViewController.h"
#import "MessageUI/MFMessageComposeViewController.h"
#import "TSWordOfMouthDelegate.h"
#import "TSOffer.h"
#import "TSHelpers.h"

@interface TSShareViewController : UIViewController<MFMessageComposeViewControllerDelegate, MFMailComposeViewControllerDelegate>

@property(STRONG_OR_RETAIN) IBOutlet UIView *bg;
@property(STRONG_OR_RETAIN) IBOutlet UIButton *doneButton;
@property(STRONG_OR_RETAIN) IBOutlet UIButton *twitterButton;
@property(STRONG_OR_RETAIN) IBOutlet UIView *twitterButtonCheck;
@property(STRONG_OR_RETAIN) IBOutlet UIButton *facebookButton;
@property(STRONG_OR_RETAIN) IBOutlet UIView *facebookButtonCheck;
@property(STRONG_OR_RETAIN) IBOutlet UIButton *emailButton;
@property(STRONG_OR_RETAIN) IBOutlet UIView *emailButtonCheck;
@property(STRONG_OR_RETAIN) IBOutlet UIButton *messagingButton;
@property(STRONG_OR_RETAIN) IBOutlet UIView *messagingButtonCheck;

+ (id)controllerWithOffer:(TSOffer *)offer delegate:(id<TSWordOfMouthDelegate>)delegate;

- (IBAction)onBtnClose:(id)sender;
- (IBAction)onBtnDone:(id)sender;
- (IBAction)onBtnMessaging:(id)sender;
- (IBAction)onBtnTwitter:(id)sender;
- (IBAction)onBtnFacebook:(id)sender;
- (IBAction)onBtnEmail:(id)sender;

- (void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result;
- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error;

@end
