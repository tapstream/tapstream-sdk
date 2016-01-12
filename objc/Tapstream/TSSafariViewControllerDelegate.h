#import <Foundation/Foundation.h>

#import "TSHelpers.h"
#import "TSResponse.h"

#if (TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
#import <UIKit/UIKit.h>


@interface TSSafariViewControllerDelegate : UIViewController

@property(nonatomic, STRONG_OR_RETAIN) NSURL* url;
@property(nonatomic, copy) void (^completion)(TSResponse*);
@property(nonatomic, STRONG_OR_RETAIN) UIWindow* hiddenWindow;
@property(nonatomic, STRONG_OR_RETAIN) UIViewController* safController;

+ (BOOL)presentSafariViewControllerWithURLAndCompletion:(NSURL*)url completion:(void (^)(TSResponse*))completion;
@end
#else

@interface TSSafariViewControllerDelegate : NSObject
+ (BOOL)presentSafariViewControllerWithURLAndCompletion:(NSURL*)url completion:(void (^)(TSResponse*))completion;
@end
#endif