//
//  TSShareViewController.m
//  UserToUser
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSShareViewController.h"

@interface TSShareViewController() {
    BOOL hasTwitter;
    BOOL hasFacebook;
    BOOL hasEmail;
    BOOL hasMessaging;
}

@property(STRONG_OR_RETAIN, nonatomic) TSOffer *offer;
@property(STRONG_OR_RETAIN, nonatomic) UIViewController *parentViewController;
@property(assign, nonatomic) id<TSUserToUserDelegate> delegate;
@property(STRONG_OR_RETAIN, nonatomic) SLComposeViewController *twitterComposeViewController;
@property(STRONG_OR_RETAIN, nonatomic) SLComposeViewController *facebookComposeViewController;
@property(STRONG_OR_RETAIN, nonatomic) MFMailComposeViewController *emailComposeViewController;
@property(STRONG_OR_RETAIN, nonatomic) MFMessageComposeViewController *messageComposeViewController;

@end


@implementation TSShareViewController

@synthesize offer, parentViewController, twitterComposeViewController, facebookComposeViewController, emailComposeViewController, bg, doneButton,
    twitterButton, twitterButtonCheck,
    facebookButton, facebookButtonCheck,
    emailButton, emailButtonCheck,
    messagingButton, messagingButtonCheck;

+ (id)controllerWithOffer:(TSOffer *)offer parentViewController:(UIViewController *)parentViewController delegate:(id<TSUserToUserDelegate>)delegate
{
    return AUTORELEASE([[TSShareViewController alloc] initWithNibName:@"TSShareView" bundle:nil offer:offer parentViewController:parentViewController delegate:delegate]);
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil offer:(TSOffer *)offerVal parentViewController:(UIViewController *)parentViewControllerVal delegate:(id<TSUserToUserDelegate>)delegateVal
{
    if(self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        self.offer = offerVal;
        self.parentViewController = parentViewControllerVal;
        self.delegate = delegateVal;
        
        hasTwitter = [SLComposeViewController isAvailableForServiceType:SLServiceTypeTwitter];
        hasFacebook = [SLComposeViewController isAvailableForServiceType:SLServiceTypeFacebook];
        hasEmail = [MFMailComposeViewController canSendMail];
        hasMessaging = [MFMessageComposeViewController canSendText];
        
        __weak TSShareViewController *me = self;
        
        if(hasTwitter) {
            self.twitterComposeViewController = [SLComposeViewController composeViewControllerForServiceType:SLServiceTypeTwitter];
            self.twitterComposeViewController.completionHandler = ^(SLComposeViewControllerResult result) {
                NSLog(@"Twitter finished: %d", result == SLComposeViewControllerResultDone);
                if(result == SLComposeViewControllerResultDone) {
                    me.twitterButtonCheck.hidden = NO;
                    me.doneButton.enabled = YES;
                }
                [me.twitterComposeViewController dismissViewControllerAnimated:YES completion:nil];
            };
        }
        
        if(hasFacebook) {
            self.facebookComposeViewController = [SLComposeViewController composeViewControllerForServiceType:SLServiceTypeFacebook];
            self.facebookComposeViewController.completionHandler = ^(SLComposeViewControllerResult result) {
                NSLog(@"Facebook finished: %d", result == SLComposeViewControllerResultDone);
                if(result == SLComposeViewControllerResultDone) {
                    me.facebookButtonCheck.hidden = NO;
                    me.doneButton.enabled = YES;
                }
                [me.facebookComposeViewController dismissViewControllerAnimated:YES completion:nil];
            };
        }
        
        if(hasEmail) {
            self.emailComposeViewController = AUTORELEASE([[MFMailComposeViewController alloc] init]);
            self.emailComposeViewController.mailComposeDelegate = self;
        }
        
        if(hasMessaging) {
            self.messageComposeViewController = AUTORELEASE([[MFMessageComposeViewController alloc] init]);
            self.messageComposeViewController.messageComposeDelegate = self;
        }
    }
    return self;
}

- (void)dealloc
{
    RELEASE(self->offer);
    RELEASE(self->parentViewController);
    RELEASE(self->twitterComposeViewController);
    RELEASE(self->facebookComposeViewController);
    RELEASE(self->emailComposeViewController);
    RELEASE(self->messageComposeViewController);
    RELEASE(self->twitterButton);
    RELEASE(self->twitterButtonCheck);
    RELEASE(self->facebookButton);
    RELEASE(self->facebookButtonCheck);
    RELEASE(self->emailButton);
    RELEASE(self->emailButtonCheck);
    RELEASE(self->messagingButton);
    RELEASE(self->messagingButtonCheck);
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.view.frame = self.parentViewController.view.bounds;
    
    self.bg.layer.backgroundColor = [UIColor colorWithWhite:0.949 alpha:1.0].CGColor;
    self.bg.layer.cornerRadius = 10;
    self.bg.layer.masksToBounds = YES;
    self.bg.layer.borderWidth = 1;
    self.bg.layer.borderColor = [UIColor colorWithRed:0.137 green:0.122 blue:0.125 alpha:1.0].CGColor;
    
    self.doneButton.enabled = NO;
    
    self.twitterButton.enabled = hasTwitter;
    self.twitterButtonCheck.hidden = YES;
    self.facebookButton.enabled = hasFacebook;
    self.facebookButtonCheck.hidden = YES;
    self.emailButton.enabled = hasEmail;
    self.emailButtonCheck.hidden = YES;
    self.messagingButton.enabled = hasMessaging;
    self.messagingButtonCheck.hidden = YES;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)close
{
    [UIView transitionWithView:self.view.superview
                      duration:0.3
                       options:UIViewAnimationOptionTransitionCrossDissolve
                    animations:^{ [self.view removeFromSuperview]; }
                    completion:NULL];
}

- (IBAction)onBtnClose:(id)sender
{
    NSLog(@"Close click");
    [self close];
}

- (IBAction)onBtnDone:(id)sender
{
    NSLog(@"Done click");
    [self close];
}

- (IBAction)onBtnMessaging:(id)sender
{
    NSLog(@"Messaging click");
    [self.parentViewController presentViewController:self.messageComposeViewController animated:YES completion:nil];
}

- (IBAction)onBtnTwitter:(id)sender
{
    NSLog(@"Twitter click");
    [self.parentViewController presentViewController:self.twitterComposeViewController animated:YES completion:nil];
}

- (IBAction)onBtnFacebook:(id)sender
{
    NSLog(@"Facebook click");
    [self.parentViewController presentViewController:self.facebookComposeViewController animated:YES completion:nil];
}

- (IBAction)onBtnEmail:(id)sender
{
    NSLog(@"Email click");
    [self.parentViewController presentViewController:self.emailComposeViewController animated:YES completion:nil];
}



- (void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result
{
    NSLog(@"Messaging finished: %d", result == MessageComposeResultSent);
    if(result == MessageComposeResultSent) {
        self.messagingButtonCheck.hidden = NO;
        self.doneButton.enabled = YES;
    }
    [self.messageComposeViewController dismissViewControllerAnimated:YES completion:nil];
}

- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error
{
    NSLog(@"Email finished: %d", result == MFMailComposeResultSent);
    if(result == MFMailComposeResultSent) {
        self.emailButtonCheck.hidden = NO;
        self.doneButton.enabled = YES;
    }
    [self.emailComposeViewController dismissViewControllerAnimated:YES completion:nil];
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
