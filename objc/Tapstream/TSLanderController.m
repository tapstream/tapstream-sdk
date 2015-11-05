//
//  TSLanderController.m
//  ExampleApp
//
//  Created by Adam Bard on 2015-11-03.
//  Copyright © 2015 Example. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TSHelpers.h"
#import "TSLander.h"
#import "TSLanderController.h"

@implementation TSLanderController

+ (id)controllerWithLander:(TSLander*)lander delegate:(id<TSLanderDelegate>)delegate
{
	return AUTORELEASE([[TSLanderController alloc] initWithNibName:@"TSLanderView" bundle:nil lander:lander delegate:delegate]);
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil lander:(TSLander*)lander  delegate:(id<TSLanderDelegate>)delegate
{
	if(self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
		((UIWebView *)self.view).delegate = self;
		self.delegate = delegate;
		[((UIWebView *)self.view) loadHTMLString:lander.html baseURL:nil];
		[self.delegate showedLander:lander.ident];
	}
	return self;
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
	[self close];
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
	NSString *url = [[request URL] absoluteString];
	if([url hasSuffix:@"close"]) {
		[self close];
        [self.delegate dismissedLander];
		return NO;

	}
	return YES;
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
	NSURLRequest* req = [webView request];
	NSURL* url = [req URL];
	NSString* s = [url absoluteString];
	if(![s isEqualToString:@"about:blank"]){
        [self.delegate submittedLander];
		[self close];
	}
}

@end