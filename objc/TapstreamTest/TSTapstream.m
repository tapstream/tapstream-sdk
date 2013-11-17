#import "TSTapstream.h"
#import "TSHelpers.h"
#import "TSPlatformImpl.h"
#import "TSCoreListenerImpl.h"

@interface TSDelegateImpl : NSObject<TSDelegate>
{
@private
	int delay;
}
- (int)getDelay;
- (bool)isRetryAllowed;
@end

@implementation TSDelegateImpl
- (int)getDelay
{
	return delay;
}

- (void)setDelay:(int)delayVal
{
	delay = delayVal;
}

- (bool)isRetryAllowed
{
	return false;
}
@end




@interface TSTapstream()

@property(nonatomic, STRONG_OR_RETAIN) id<TSDelegate> del;
@property(nonatomic, STRONG_OR_RETAIN) id<TSPlatform> platform;
@property(nonatomic, STRONG_OR_RETAIN) id<TSCoreListener> listener;
@property(nonatomic, STRONG_OR_RETAIN) TSCore *core;

@end


@implementation TSTapstream

@synthesize del, platform, listener, core;

- (id)initWithOperationQueue:(TSOperationQueue *)q accountName:(NSString *)accountName developerSecret:(NSString *)developerSecret config:(TSConfig *)config
{
	if((self = [super init]) != nil)
	{
		del = [[TSDelegateImpl alloc] init];
		platform = [[TSPlatformImpl alloc] init];
		listener = [[TSCoreListenerImpl alloc] initWithQueue:q];
		core = [[TSCore alloc] initWithDelegate:del
			platform:platform
			listener:listener
			appEventSource:nil
			accountName:accountName
			developerSecret:developerSecret
			config:config];
		[core start];
	}
	return self;
}

- (void)dealloc
{
	RELEASE(del);
	RELEASE(platform);
	RELEASE(listener);
	RELEASE(core);
	SUPER_DEALLOC;
}

- (void)fireEvent:(TSEvent *)event
{
	[core fireEvent:event];
}

- (void)fireHit:(TSHit *)hit completion:(void(^)(TSResponse *))completion
{
	[core fireHit:hit completion:completion];
}

- (void)setResponseStatus:(int)status
{
	NSString *msg = [NSString stringWithFormat:@"Http %d", status];
	((TSPlatformImpl *)platform).response = AUTORELEASE([[TSResponse alloc] initWithStatus:status message:msg data:nil]);
}

- (NSArray *)getSavedFiredList
{
	return ((TSPlatformImpl *)platform).savedFiredList;
}

- (int)getDelay
{
	return [core getDelay];
}

- (void)setDelay:(int)delay
{
	[del setDelay:delay];
}

- (NSString *)getPostData
{
	return core.postData;
}

@end