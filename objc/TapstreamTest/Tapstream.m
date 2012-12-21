#import "Tapstream.h"
#import "helpers.h"
#import "PlatformImpl.h"
#import "CoreListenerImpl.h"

@interface DelegateImpl : NSObject<Delegate> {}
- (int)getDelay;
- (bool)isRetryAllowed;
@end

@implementation DelegateImpl
- (int)getDelay
{
	return 0;
}

- (bool)isRetryAllowed
{
	return false;
}
@end




@interface Tapstream()

@property(nonatomic, STRONG_OR_RETAIN) id<Delegate> del;
@property(nonatomic, STRONG_OR_RETAIN) id<Platform> platform;
@property(nonatomic, STRONG_OR_RETAIN) id<CoreListener> listener;
@property(nonatomic, STRONG_OR_RETAIN) Core *core;

@end


@implementation Tapstream

@synthesize del, platform, listener, core;

- (id)initWithOperationQueue:(OperationQueue *)q accountName:(NSString *)accountName developerSecret:(NSString *)developerSecret hardware:(NSString *)hardware
{
	if((self = [super init]) != nil)
	{
		del = [[DelegateImpl alloc] init];
		platform = [[PlatformImpl alloc] init];
		listener = [[CoreListenerImpl alloc] initWithQueue:q];
		core = [[Core alloc] initWithDelegate:del
			platform:platform
			listener:listener
			accountName:accountName
			developerSecret:developerSecret
			hardware:hardware];
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

- (void)fireEvent:(Event *)event
{
	[core fireEvent:event];
}

- (void)fireHit:(Hit *)hit completion:(void(^)(Response *))completion
{
	[core fireHit:hit completion:completion];
}

- (void)setResponseStatus:(int)status
{
	NSString *msg = [NSString stringWithFormat:@"Http %d", status];
	((PlatformImpl *)platform).response = AUTORELEASE([[Response alloc] initWithStatus:status message:msg]);
}

- (NSArray *)getSavedFiredList
{
	return ((PlatformImpl *)platform).savedFiredList;
}

- (int)getDelay
{
	return [core getDelay];
}

- (NSString *)getPostData
{
	return core.postData;
}

@end