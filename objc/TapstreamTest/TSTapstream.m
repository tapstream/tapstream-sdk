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





@implementation TSTapstream

@synthesize del, platform, listener, core, config;

- (id)initWithOperationQueue:(TSOperationQueue *)q accountName:(NSString *)accountName developerSecret:(NSString *)developerSecret config:(TSConfig *)configVal
{
	if((self = [super init]) != nil)
	{
		del = [[TSDelegateImpl alloc] init];
		platform = [[TSPlatformImpl alloc] init];
		listener = RETAIN([[TSCoreListenerImpl alloc] initWithQueue:q]);
		config = RETAIN(configVal);
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
	RELEASE(config);
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