//
//  TSOfferViewController.m
//  WordOfMouth
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSOfferViewController.h"

@implementation TSOfferViewController

@synthesize offer, delegate;

+ (id)controllerWithOffer:(TSOffer *)offer delegate:(id<TSWordOfMouthDelegate>)delegate
{
    return AUTORELEASE([[TSOfferViewController alloc] initWithNibName:@"TSOfferView" bundle:nil offer:offer delegate:delegate]);
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil offer:(TSOffer *)offerVal delegate:(id<TSWordOfMouthDelegate>)delegateVal
{
    if(self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        self.offer = offerVal;

        self.delegate = delegateVal;
        ((UIWebView *)self.view).delegate = self;
        [((UIWebView *)self.view) loadHTMLString:self.offer.markup baseURL:[NSURL URLWithString:@""]];
    }
    return self;
}

- (void)dealloc
{
    SUPER_DEALLOC;
    
    RELEASE(self->offer);
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    ((UIWebView *)self.view).scrollView.scrollEnabled = NO;
    ((UIWebView *)self.view).scrollView.opaque = NO;
    ((UIWebView *)self.view).scrollView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.8];
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

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    NSString *url = [[request URL] absoluteString];
    if([url isEqualToString:@"file:///close"]) {
        [self close];
        [self.delegate dismissedOffer:NO];
        return NO;
        
    } else if([url isEqualToString:@"file:///accept"]) {
        [self close];
        [self.delegate dismissedOffer:YES];
        return NO;
    }
    return YES;
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
}

- (void)webViewDidStartLoad:(UIWebView *)webView
{
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
