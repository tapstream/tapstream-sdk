//
//  TSSafariViewControllerDelegate.m
//  ExampleApp
//
//  Created by Adam Bard on 2015-09-12.
//  Copyright © 2015 Example. All rights reserved.
//

#import "TSSafariViewControllerDelegate.h"
#import "TSLogging.h"

#if (TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
@implementation TSSafariViewControllerDelegate

+ (void)presentSafariViewControllerWithURLAndCompletion:(NSURL*)url completion:(void (^)(void))completion
{
	Class safControllerClass = NSClassFromString(@"SFSafariViewController");
	if(safControllerClass != nil){
		UIViewController* safController = [[safControllerClass alloc] initWithURL:url];

		if(safController != nil){
			TSSafariViewControllerDelegate* me = [[TSSafariViewControllerDelegate alloc] init];

			me.safController = RETAIN(safController);

			me.completion = completion;

			me.hiddenWindow = [[UIWindow alloc] initWithFrame:CGRectZero];
			me.hiddenWindow.rootViewController = me;
			me.hiddenWindow.hidden = true;

			me.view.hidden = YES;
			me.modalPresentationStyle = UIModalPresentationOverFullScreen;

			[safController performSelector:@selector(setDelegate:) withObject:me];

			[me.hiddenWindow makeKeyAndVisible];
			[me presentViewController:safController animated:YES completion:nil];
		}
	}else{
		[TSLogging logAtLevel:kTSLoggingWarn format:@"Tapstream could not load SFSafariViewController, is Safari Services framework enabled?"];
	}
}


- (void)dealloc
{
	RELEASE(self.safController);
	RELEASE(self.hiddenWindow);
	SUPER_DEALLOC;
}

// SFSafariViewController delegate methods
- (NSArray<UIActivity *> *)safariViewController:(id)controller activityItemsForURL:(NSURL *)URL title:(nullable NSString *)title
{
	return nil;
}

- (void)safariViewControllerDidFinish:(id)controller
{
}

- (void)safariViewController:(id)controller didCompleteInitialLoad:(BOOL)didLoadSuccessfully
{
	[controller dismissViewControllerAnimated:false completion:^{
		[self.hiddenWindow.rootViewController dismissViewControllerAnimated:NO completion:nil];
		if(self.completion != nil){
			self.completion();
		}
	}];
}

@end

#else
// Stub for Mac
@implementation TSSafariViewControllerDelegate
+ (void)presentSafariViewControllerWithURLAndCompletion:(NSURL*)url completion:(void (^)(void))completion
{
	[TSLogging logAtLevel:kTSLoggingError format:@"Tapstream cookie matching should only be used on iOS devices"];
	if (completion != nil){
		completion();
	}
}
@end
#endif