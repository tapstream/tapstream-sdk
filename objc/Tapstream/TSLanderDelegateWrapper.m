//  Wraps TSLanderDelegate to record the lander being shown when it completes.

#import "TSLanderDelegateWrapper.h"

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR

#import <Foundation/Foundation.h>
#import "TSLanderDelegate.h"
#import "TSPlatform.h"

@implementation TSLanderDelegateWrapper
- initWithPlatformAndDelegateAndWindow:(id<TSPlatform>)platform delegate:(id<TSLanderDelegate>)delegate window:(UIWindow*)window
{
	if(self = [super init])
	{
		self.platform = platform;
		self.delegate = delegate;
		self.window = window;
	}
	return self;
}
- (void)showedLander:(NSUInteger)landerId
{
	[self.platform setLanderShown:landerId];
	[self.delegate showedLander:landerId];
}
- (void)dismissedLander
{
	[self.delegate dismissedLander];
	[self.window removeFromSuperview];
	self.window = nil;
}
- (void)submittedLander
{
	[self.delegate submittedLander];
	[self.window removeFromSuperview];
	self.window = nil;
}
@end
#else
@implementation TSLanderDelegateWrapper
@end
#endif