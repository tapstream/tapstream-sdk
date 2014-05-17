//
//  TSShareViewController.h
//  UserToUser
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Social/SLComposeViewController.h"
#import "Social/SLServiceTypes.h"
#import "MessageUI/MFMailComposeViewController.h"
#import "MessageUI/MFMessageComposeViewController.h"
#import "TSHelpers.h"

@interface TSShareViewController : UIViewController<MFMessageComposeViewControllerDelegate>

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

+ (id)controllerWithParentViewController:(UIViewController *)parentViewController;

- (IBAction)onBtnClose:(id)sender;
- (IBAction)onBtnDone:(id)sender;
- (IBAction)onBtnMessaging:(id)sender;
- (IBAction)onBtnTwitter:(id)sender;
- (IBAction)onBtnFacebook:(id)sender;
- (IBAction)onBtnEmail:(id)sender;

- (void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result;

@end
