#import "TSConfig.h"
#import "TSHelpers.h"

@implementation TSConfig

@synthesize hardware = hardware;
@synthesize udid = udid;
@synthesize idfa = idfa;
@synthesize secureUdid = secureUdid;
@synthesize collectWifiMac = collectWifiMac;
#if !(TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
@synthesize collectSerialNumber = collectSerialNumber;
#endif

+ (id)configWithDefaults
{
	return AUTORELEASE([[self alloc] init]);
}

- (id)init
{
	if((self = [super init]) != nil)
	{
		collectWifiMac = YES;
#if !(TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
		collectSerialNumber = YES;
#endif
	}
	return self;
}

- (void)dealloc
{
	RELEASE(hardware);
	RELEASE(udid);
	RELEASE(idfa);
	RELEASE(secureUdid);
	SUPER_DEALLOC;
}

@end