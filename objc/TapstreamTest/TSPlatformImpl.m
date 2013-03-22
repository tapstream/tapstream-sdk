#import "TSPlatformImpl.h"
#import "TSHelpers.h"

@implementation TSPlatformImpl

@synthesize response, savedFiredList;

- (id)init
{
	if((self = [super init]) != nil)
	{
		response = [[TSResponse alloc] initWithStatus:200 message:nil];
		savedFiredList = nil;
	}
	return self;
}

- (void)dealloc
{
	RELEASE(response);
	RELEASE(savedFiredList);
	SUPER_DEALLOC;
}

- (NSString *)loadUuid
{
	return @"00000000-0000-0000-0000-000000000000";
}

- (NSMutableSet *)loadFiredEvents
{
	return AUTORELEASE([[NSMutableSet alloc] initWithCapacity:32]);
}

- (void)saveFiredEvents:(NSMutableSet *)firedEvents
{
	self.savedFiredList = [firedEvents allObjects];
}

- (NSString *)getResolution
{
	return @"480x960";
}

- (NSString *)getManufacturer
{
	return @"TestManufacturer";
}

- (NSString *)getModel
{
	return @"TestModel";
}

- (NSString *)getOs
{
	return @"TestOs";
}

- (NSString *)getLocale
{
	return @"en_US";
}

- (NSString *)getWifiMac
{
	return @"00:00:00:00:00:00";
}

- (NSString *)getAppName
{
	return @"TestApp";
}

- (NSString *)getPackageName
{
	return @"com.test.TestApp";
}

- (TSResponse *)request:(NSString *)url data:(NSString *)data
{
	return response;
}


@end