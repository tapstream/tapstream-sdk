#import "TSPlatformImpl.h"
#import "TSHelpers.h"

@implementation TSPlatformImpl

@synthesize response, savedFiredList;

- (id)init
{
	if((self = [super init]) != nil)
	{
		response = [[TSResponse alloc] initWithStatus:200 message:nil data:nil];
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
	return @"Test App";
}

- (NSString *)getAppVersion
{
	return @"1.0.0.0";
}

- (NSString *)getPackageName
{
	return @"com.test.TestApp";
}

- (TSResponse *)request:(NSString *)url data:(NSString *)data method:(NSString *)method
{
	return response;
}


- (NSSet *)getProcessSet
{
    NSMutableSet *set = [NSMutableSet setWithCapacity:10];
    [set addObject:@"Test App"];
    [set addObject:@"Another App"];
    return set;
}

- (NSString *)getComputerGUID
{
	return @"00000000-0000-0000-0000-000000000000";
}

- (NSString *)getBundleIdentifier
{
	return @"bundle_identifier";
}

- (NSString *)getBundleShortVersion
{
	return @"1.0";
}

@end