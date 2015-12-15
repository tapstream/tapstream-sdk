//
//  TSSafariViewControllerDelegate.m
//  ExampleApp
//
//  Created by Adam Bard on 2015-09-12.
//  Copyright © 2015 Example. All rights reserved.
//

#import "TSSafariViewControllerDelegate.h"
#import "TSLogging.h"
#import "TSResponse.h"

#if (TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
@implementation TSSafariViewControllerDelegate

+ (BOOL)presentSafariViewControllerWithURLAndCompletion:(NSURL*)url completion:(void (^)(TSResponse*))completion
{
	Class safControllerClass = NSClassFromString(@"SFSafariViewController");
	if(safControllerClass != nil){
		UIViewController* safController = [[safControllerClass alloc] initWithURL:url];

		if(safController != nil){
			TSSafariViewControllerDelegate* me = AUTORELEASE([[TSSafariViewControllerDelegate alloc] init]);

			me.safController = RETAIN(safController);

			me.completion = completion;

			me.hiddenWindow = [[UIWindow alloc] initWithFrame:CGRectZero];
			me.hiddenWindow.rootViewController = me;
			me.hiddenWindow.hidden = true;

			me.view.hidden = YES;

			[safController performSelector:@selector(setDelegate:) withObject:me];

			[me.hiddenWindow makeKeyAndVisible];
			[me presentViewController:safController animated:YES completion:nil];
			return true;
		}
	}else{
		[TSLogging logAtLevel:kTSLoggingWarn format:@"Tapstream could not load SFSafariViewController, is Safari Services framework enabled?"];
	}
	return false;
}


- (void)dealloc
{
	RELEASE(self.safController);
	RELEASE(self.hiddenWindow);
	SUPER_DEALLOC;
}

- (void)safariViewController:(id)controller didCompleteInitialLoad:(BOOL)didLoadSuccessfully
{
	__unsafe_unretained UIWindow* window = self.hiddenWindow;
	__unsafe_unretained void (^completion)(TSResponse*) = self.completion;
	[controller dismissViewControllerAnimated:false completion:^{

		TSResponse* response;
		if(didLoadSuccessfully) {
			response = [[TSResponse alloc]
						initWithStatus:200
						message:[NSHTTPURLResponse localizedStringForStatusCode:200]
						data:nil];
		}else{
			response = [[TSResponse alloc]
						initWithStatus:-1
						message:@"An error occurred presenting Safari View controller"
						data:nil];
		}
		if(completion != nil){
			completion(response);
		}
		[window.rootViewController dismissViewControllerAnimated:NO completion:nil];
		window.rootViewController = nil;
	}];
}

@end

#else
// Stub for Mac
@implementation TSSafariViewControllerDelegate
+ (void)presentSafariViewControllerWithURLAndCompletion:(NSURL*)url completion:(void (^)(TSResponse*))completion
{
	[TSLogging logAtLevel:kTSLoggingError format:@"Tapstream cookie matching should only be used on iOS devices"];

	if (completion != nil){
		completion([[TSResponse alloc]
					initWithStatus:-1
						   message:@"SafariServices framework not loaded, cookie match impossible"
							  data:nil]);
	}
}
@end
#endif