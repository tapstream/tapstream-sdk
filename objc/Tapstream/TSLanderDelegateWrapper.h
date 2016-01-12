#ifndef TSLanderDelegateWrapper_h
#define TSLanderDelegateWrapper_h

#import "TSLanderDelegate.h"

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR

#import <UIKit/UIKit.h>
#import "TSHelpers.h"
#import "TSPlatform.h"

@interface TSLanderDelegateWrapper : NSObject<TSLanderDelegate>
@property(nonatomic, STRONG_OR_RETAIN) id<TSPlatform> platform;
@property(nonatomic, STRONG_OR_RETAIN) id<TSLanderDelegate> delegate;
@property(nonatomic, STRONG_OR_RETAIN) UIWindow* window;
- initWithPlatformAndDelegateAndWindow:(id<TSPlatform>)platform delegate:(id<TSLanderDelegate>)delegate window:(UIWindow*)window;
@end
#else
@interface TSLanderDelegateWrapper : NSObject<TSLanderDelegate>
@end
#endif
#endif /* TSLanderDelegateWrapper_h */
