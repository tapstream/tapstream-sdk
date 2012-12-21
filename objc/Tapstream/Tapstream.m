#import "Tapstream.h"
#import "helpers.h"
#import "PlatformImpl.h"
#import "CoreListenerImpl.h"

@interface DelegateImpl : NSObject<Delegate> {
	Tapstream *ts;
}
@property(nonatomic, STRONG_OR_RETAIN) Tapstream *ts;
- (id)initWithTapstream:(Tapstream *)ts;
- (int)getDelay;
- (bool)isRetryAllowed;
@end
// DelegateImpl comes at the end of the file so it can access a private property of the Tapstream interface



static Tapstream *instance = nil;


@interface Tapstream()

@property(nonatomic, STRONG_OR_RETAIN) id<Delegate> del;
@property(nonatomic, STRONG_OR_RETAIN) id<Platform> platform;
@property(nonatomic, STRONG_OR_RETAIN) id<CoreListener> listener;
@property(nonatomic, STRONG_OR_RETAIN) Core *core;

- (id)initWithAccountName:(NSString *)accountName developerSecret:(NSString *)developerSecret hardware:(NSString *)hardware;

@end


@implementation Tapstream

@synthesize del, platform, listener, core;

+ (void)createWithAccountName:(NSString *)accountName developerSecret:(NSString *)developerSecret
{
	[Tapstream createWithAccountName:accountName developerSecret:developerSecret hardware:nil];
}

+ (void)createWithAccountName:(NSString *)accountName developerSecret:(NSString *)developerSecret hardware:(NSString *)hardware
{
	@synchronized(self)
	{
		if(instance == nil)
		{
			instance = [[Tapstream alloc] initWithAccountName:accountName developerSecret:developerSecret hardware:hardware];
		}
		else
		{
			[Logging logAtLevel:kLoggingWarn format:@"Tapstream Warning: Tapstream already instantiated, it cannot be re-created."];
		}
	}
}

+ (id)instance
{
	@synchronized(self)
	{
		NSAssert(instance != nil, @"You must first call +createWithAccountName:developerSecret:");
		return AUTORELEASE(instance);
	}
}


- (id)initWithAccountName:(NSString *)accountName developerSecret:(NSString *)developerSecret hardware:(NSString *)hardware
{
	if((self = [super init]) != nil)
	{
		del = [[DelegateImpl alloc] init];
		platform = [[PlatformImpl alloc] init];
		listener = [[CoreListenerImpl alloc] init];
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

@end





@implementation DelegateImpl
@synthesize ts;

- (id)initWithTapstream:(Tapstream *)tsVal
{
	if((self = [super init]) != nil)
	{
		self.ts = tsVal;
	}
	return self;
}

- (void)dealloc
{
	RELEASE(ts);
	SUPER_DEALLOC;
}

- (int)getDelay
{
	return [ts.core getDelay];
}

- (bool)isRetryAllowed
{
	return true;
}
@end


