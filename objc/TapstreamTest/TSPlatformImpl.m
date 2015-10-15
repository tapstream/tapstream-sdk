#import "TSPlatformImpl.h"
#import "TSHelpers.h"

@implementation TSPlatformImpl

@synthesize response, savedFiredList, firstRun;

- (id)init
{
	if((self = [super init]) != nil)
	{
		response = [[TSResponse alloc] initWithStatus:200 message:nil data:nil];
		savedFiredList = nil;
		firstRun = true;
	}
	return self;
}

- (void)dealloc
{
	RELEASE(response);
	RELEASE(savedFiredList);
	SUPER_DEALLOC;
}

- (void)setPersistentFlagVal:(NSString*)key
{
	[[NSUserDefaults standardUserDefaults] setBool:true forKey:key];
}

- (BOOL)getPersistentFlagVal:(NSString*)key
{
	BOOL val = [[NSUserDefaults standardUserDefaults] boolForKey:key];
	return val;
}

- (BOOL) isFirstRun
{
	return firstRun;
}

- (void) registerFirstRun
{
	firstRun = false;
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

- (NSString *)getOsBuild
{
	return @"FACADE";
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

- (TSResponse *)request:(NSString *)url data:(NSString *)data method:(NSString *)method timeout_ms:(int)timeout_ms
{
	return response;
}


- (NSString *)getComputerGUID
{
	return @"00000000000000000000000000000000";
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
