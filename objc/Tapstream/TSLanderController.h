#ifndef TSLanderController_h
#define TSLanderController_h

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR

#import "TSLanderDelegate.h"
#import "TSLander.h"
#import <UIKit/UIKit.h>

@interface TSLanderController : UIViewController<UIWebViewDelegate>
@property(nonatomic, STRONG_OR_RETAIN) id<TSLanderDelegate> delegate;

+ (id)controllerWithLander:(TSLander*)lander delegate:(id<TSLanderDelegate>)delegate;
@end

#else
@interface TSLanderController : NSObject
@end
#endif
#endif /* TSLanderController_h */
