#import "PlatformImpl.h"
#import <sys/types.h>
#import <sys/sysctl.h>
#import "helpers.h"

#if TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
#import <UIKit/UIKit.h>
#else
#import <AppKit/AppKit.h>
#endif

#define kFiredEventsKey @"__tapstream_fired_events"
#define kUUIDKey @"__tapstream_uuid"

@implementation PlatformImpl

- (NSString *)loadUuid
{
	NSString *uuid = [[NSUserDefaults standardUserDefaults] objectForKey:kUUIDKey];
	if(!uuid)
	{
		CFUUIDRef uuidObject = CFUUIDCreate(kCFAllocatorDefault); 
  		uuid = AUTORELEASE((BRIDGE_TRANSFER NSString *)CFUUIDCreateString(kCFAllocatorDefault, uuidObject));
 		CFRelease(uuidObject);
 		[[NSUserDefaults standardUserDefaults] setObject:uuid forKey:kUUIDKey];
 		[[NSUserDefaults standardUserDefaults] synchronize];
	}
	return uuid;
}

- (NSMutableSet *)loadFiredEvents
{
	NSArray *fireList = [[NSUserDefaults standardUserDefaults] objectForKey:kFiredEventsKey];
	if(fireList)
	{
		return [NSMutableSet setWithArray:fireList];
	}
	return [NSMutableSet setWithCapacity:32];
}

- (void)saveFiredEvents:(NSMutableSet *)firedEvents
{
	[[NSUserDefaults standardUserDefaults] setObject:[firedEvents allObjects] forKey:kFiredEventsKey];
	[[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString *)getResolution
{
#if TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
	CGRect frame = [UIScreen mainScreen].bounds;
	float scale = [UIScreen mainScreen].scale;
	return [NSString stringWithFormat:@"%dx%d", (int)(frame.size.width * scale), (int)(frame.size.height * scale)];
#else
	NSRect frame = [NSScreen mainScreen].frame;
	return [NSString stringWithFormat:@"%dx%d", (int)(frame.size.width), (int)(frame.size.height)];
#endif
}

- (NSString *)getManufacturer
{
	return @"Apple";
}

- (NSString *)getModel
{
#if TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
	return [self systemInfoByName:@"hw.machine"];
#else
	return [NSString stringWithFormat:@"%@ %@", [self systemInfoByName:@"hw.model"], [self systemInfoByName:@"hw.machine"]];
#endif
}

- (NSString *)getOs
{
#if TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
	return [NSString stringWithFormat:@"%@ %@", [[UIDevice currentDevice] systemName], [[UIDevice currentDevice] systemVersion]];
#else

	// This "Gestalt" method is deprecated since OSX 10.8
	#if 0
	SInt32 major, minor, bugfix;
	Gestalt(gestaltSystemVersionMajor, &major);
	Gestalt(gestaltSystemVersionMinor, &minor);
	Gestalt(gestaltSystemVersionBugFix, &bugfix);
	NSString *version = [NSString stringWithFormat:@"%d.%d.%d", major, minor, bugfix];
	return [NSString stringWithFormat:@"Mac OS X %@", version];
	#else

	NSDictionary *sv = [NSDictionary dictionaryWithContentsOfFile:@"/System/Library/CoreServices/SystemVersion.plist"];
	return [sv objectForKey:@"ProductVersion"];
	
	#endif
#endif
}

- (NSString *)getLocale
{
	return [[NSLocale currentLocale] localeIdentifier];
}

- (Response *)request:(NSString *)url data:(NSString *)data
{
	NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
	[request setHTTPMethod:@"POST"];
	[request setHTTPBody:[data dataUsingEncoding:NSUTF8StringEncoding]];

	NSError *error = nil;
	NSHTTPURLResponse *response = nil;
	if(![NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error] || !response)
	{
		if(error)
		{
			NSString *msg = [NSString stringWithFormat:@"%@", error];
			return AUTORELEASE([[Response alloc] initWithStatus:-1 message:msg]);
		}
		return AUTORELEASE([[Response alloc] initWithStatus:-1 message:@"Unknown"]);
	}
	return AUTORELEASE([[Response alloc] initWithStatus:response.statusCode message:[NSHTTPURLResponse localizedStringForStatusCode:response.statusCode]]);
}

- (NSString *)systemInfoByName:(NSString *)name
{
	size_t size;
	sysctlbyname( [name UTF8String], NULL, &size, NULL, 0);

	char *pBuffer = malloc(size);
	sysctlbyname( [name UTF8String], pBuffer, &size, NULL, 0);
	NSString *value = [NSString stringWithUTF8String:pBuffer];
	free( pBuffer );

	return value != nil ? value : @"";
}

@end

