#import "TSAppEventSourceImpl.h"

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
#import <UIKit/UIKit.h>

@interface TSAppEventSourceImpl()

@property(nonatomic, STRONG_OR_RETAIN) id<NSObject> foregroundedEventObserver;
@property(nonatomic, copy) void(^onOpen)();

- (id)init;
- (void)dealloc;

@end



@implementation TSAppEventSourceImpl

@synthesize foregroundedEventObserver, onOpen;

- (id)init
{
	if((self = [super init]) != nil)
	{
		self.onOpen = nil;
		self.foregroundedEventObserver = [[NSNotificationCenter defaultCenter] addObserverForName:UIApplicationWillEnterForegroundNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *note) {
			if(onOpen != nil) {
				onOpen();
			}
		}];
	}
	return self;
}

- (void)setOnOpenHandler:(void(^)())handler
{
	self.onOpen = handler;
}

- (void)dealloc
{
	if(foregroundedEventObserver != nil) {
		[[NSNotificationCenter defaultCenter] removeObserver:foregroundedEventObserver];
	}

	RELEASE(onOpen);
	RELEASE(foregroundedEventObserver);
	SUPER_DEALLOC;
}

@end

#endif