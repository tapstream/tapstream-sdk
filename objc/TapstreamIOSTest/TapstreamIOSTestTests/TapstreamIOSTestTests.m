//
//  TapstreamIOSTestTests.m
//  TapstreamIOSTestTests
//
//  Created by Adam Bard on 2015-10-17.
//  Copyright © 2015 Adam Bard. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "TSTapstream.h"
#import "TSPlatform.h"
#import "TSPlatformImpl.h"
#import "TSCoreListenerImpl.h"
#import "TSAppEventSourceImpl.h"
#import "TSCoreListener.h"
#import "TSCore.h"
#import "TSLander.h"

@interface TSDelegateTestImpl : NSObject<TSDelegate>
- (int)getDelay;
- (void)setDelay:(int)delay;
- (BOOL)isRetryAllowed;
@end

@implementation TSDelegateTestImpl
- (int)getDelay {return 0;}
- (void)setDelay:(int)delay {}
- (BOOL)isRetryAllowed { return true; }
@end

@interface TSPlatformTestImpl : NSObject<TSPlatform>
@property(nonatomic, strong) id<TSPlatform> delegate;
@property(nonatomic) BOOL firstRun;
@property(nonatomic, strong) NSMutableArray<NSArray<NSString*>*>* requests;
- (NSMutableArray*)getRequests;
@end

@implementation TSPlatformTestImpl
- (TSPlatformTestImpl*)initWithDelegate:(id<TSPlatform>) delegate
{
	self = [super init];
	self.delegate = delegate;
	self.requests = [[NSMutableArray alloc] initWithCapacity:10];
	self.firstRun = true;
	return self;
}

- (TSResponse *)request:(NSString *)url data:(NSString *)data method:(NSString *)method timeout_ms:(int)timeout_ms
{

	NSArray<NSString*>* requestData = [[NSArray alloc] initWithObjects:url, data, method, nil];
	[self.requests addObject:requestData];
	return [[TSResponse alloc] initWithStatus:200 message:@"ok" data:nil];
}

- (void)fireCookieMatch:(NSURL*)url completion:(void(^)(TSResponse*))completion
{
	NSArray* requestData = [[NSArray alloc] initWithObjects:[url absoluteString], @"", @"GET", nil];
	[self.requests addObject: requestData];
	completion([[TSResponse alloc] initWithStatus:200 message:@"ok" data:nil]);
}

- (NSMutableArray*)getRequests
{
	return self.requests;
}

- (void)setPersistentFlagVal:(NSString*)key{ [self.delegate setPersistentFlagVal:key]; }
- (BOOL)getPersistentFlagVal:(NSString*)key{ return [self.delegate getPersistentFlagVal:key]; }
- (BOOL) isFirstRun{ return self.firstRun; }
- (void) registerFirstRun{ self.firstRun = false; }
- (NSString *)loadUuid{ return [self.delegate loadUuid]; }
- (NSMutableSet *)loadFiredEvents{ return [self.delegate loadFiredEvents]; }
- (void)saveFiredEvents:(NSMutableSet *)firedEvents{ [self.delegate saveFiredEvents:firedEvents]; }
- (NSString *)getResolution{ return [self.delegate getResolution]; }
- (NSString *)getManufacturer{ return [self.delegate getManufacturer]; }
- (NSString *)getModel{ return [self.delegate getModel]; }
- (NSString *)getOs{ return [self.delegate getOs]; }
- (NSString *)getOsBuild{ return [self.delegate getOsBuild]; }
- (NSString *)getLocale{ return [self.delegate getLocale]; }
- (NSString *)getWifiMac{ return [self.delegate getWifiMac]; }
- (NSString *)getAppName{ return [self.delegate getAppName]; }
- (NSString *)getAppVersion{ return [self.delegate getAppVersion]; }
- (NSString *)getPackageName{ return [self.delegate getPackageName]; }
- (NSString *)getComputerGUID{ return [self.delegate getComputerGUID]; }
- (NSString *)getBundleIdentifier{ return [self.delegate getBundleIdentifier]; }
- (NSString *)getBundleShortVersion{ return [self.delegate getBundleShortVersion]; }
- (BOOL)landerShown:(NSUInteger)landerId{ return [self.delegate landerShown:landerId]; }
- (void)setLanderShown:(NSUInteger)landerId{ [self.delegate setLanderShown:landerId]; }

- (BOOL) shouldCookieMatch{ return [self.delegate shouldCookieMatch]; }
- (void)setCookieMatchFired:(NSTimeInterval)t{ [self.delegate setCookieMatchFired:t]; }
- (void)registerCookieMatchObserver:(id)observerClass selector:(SEL)observerSelector
{
	[self.delegate registerCookieMatchObserver:observerClass selector:observerSelector];
}
- (void)unregisterCookieMatchObserver:(id)observerClass
{
	[self.delegate unregisterCookieMatchObserver:observerClass];
}

@end


@interface TapstreamIOSTestTests : XCTestCase
@property(nonatomic, strong) id<TSDelegate> del;
@property(nonatomic, strong) TSPlatformTestImpl<TSPlatform>* platform;
@property(nonatomic, strong) id<TSCoreListener> listener;
@property(nonatomic, strong) id<TSAppEventSource> appEventSource;
@property(nonatomic, strong) TSCore* core;
@end

@implementation TapstreamIOSTestTests

- (void)initCore:(TSConfig*) config
{
	self.core = AUTORELEASE([[TSCore alloc] initWithDelegate:self.del
													platform:self.platform
													listener:self.listener
											  appEventSource:self.appEventSource
												 accountName:@"sdktest"
											 developerSecret:@""
													  config:config]);
}

- (void)setUp {
    [super setUp];

	self.del = [[TSDelegateTestImpl alloc] init];
	self.platform = [[TSPlatformTestImpl alloc] initWithDelegate:[[TSPlatformImpl alloc] init]];
	self.listener = [[TSCoreListenerImpl alloc] init];
	self.appEventSource = [[TSAppEventSourceImpl alloc] init];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testCookieMatchTiming {
	TSConfig* config = [TSConfig configWithDefaults];
	config.attemptCookieMatch = true;
	[self initCore:config];
	[[NSUserDefaults standardUserDefaults]
	 setDouble:0
	 forKey:@"__tapstream_cookie_match_timestamp"];

	XCTAssertTrue([self.platform shouldCookieMatch]);
	[self.core firedCookieMatch];
	XCTAssertFalse([self.platform shouldCookieMatch]);
	double timestampBefore = [[NSUserDefaults standardUserDefaults] doubleForKey:@"__tapstream_cookie_match_timestamp"];

	[[NSUserDefaults standardUserDefaults]
	 setDouble:(timestampBefore - 86399.0)
	 forKey:@"__tapstream_cookie_match_timestamp"];
	XCTAssertFalse([self.platform shouldCookieMatch]);

	[[NSUserDefaults standardUserDefaults]
	 setDouble:(timestampBefore - 86400.0)
	 forKey:@"__tapstream_cookie_match_timestamp"];
	XCTAssertTrue([self.platform shouldCookieMatch]);
}

- (void)testAutomaticCookieMatch {
	TSConfig* config = [TSConfig configWithDefaults];
	config.attemptCookieMatch = true;
	[self initCore:config];

	self.platform.firstRun = true;
	[[NSUserDefaults standardUserDefaults]
	 setDouble:0
	 forKey:@"__tapstream_cookie_match_timestamp"];

	// Startup -- should cookie match
	[self.core start];

	[[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:1]];

	NSArray* requests = [self.platform getRequests];

	XCTAssertEqual(2, [requests count]);
	NSString* cookieMatchUrl = [[requests firstObject] firstObject];
	XCTAssertTrue([[[requests firstObject] lastObject] isEqualToString:@"GET"]);
	XCTAssertTrue([cookieMatchUrl containsString:@"cookiematch=true"]);

	[self.core firedCookieMatch];
	// Send event -- cookie match already done.
	double timestampBefore = [[NSUserDefaults standardUserDefaults]
							  doubleForKey:@"__tapstream_cookie_match_timestamp"];
	XCTAssertFalse([self.platform shouldCookieMatch]);
	[self.core fireEvent:[TSEvent eventWithName:@"test-event" oneTimeOnly:NO]];
	[[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:1]];
	XCTAssertEqual(3, [requests count]);

	NSString* regularEventUrl = [[requests lastObject] firstObject];
	XCTAssertFalse([regularEventUrl containsString:@"cookiematch=true"]);


	// Send event tomorrow (reset timestamp) -- should cookie match
	timestampBefore = [[NSUserDefaults standardUserDefaults]
							  doubleForKey:@"__tapstream_cookie_match_timestamp"];
	[[NSUserDefaults standardUserDefaults]
	 setDouble:(timestampBefore - 86410.0)
	 forKey:@"__tapstream_cookie_match_timestamp"];

	XCTAssertTrue([self.platform shouldCookieMatch]);

	[self.core fireEvent:[TSEvent eventWithName:@"test-event2" oneTimeOnly:NO]];
	[[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:1]];
	XCTAssertEqual(4, [requests count]);

	regularEventUrl = [[requests lastObject] firstObject];
	XCTAssertTrue([regularEventUrl containsString:@"cookiematch=true"]);

}

- (void)testConsecutiveCookieMatches {
	TSConfig* config = [TSConfig configWithDefaults];
	config.attemptCookieMatch = true;
	[self initCore:config];

	[[NSUserDefaults standardUserDefaults]
	 setDouble:0
	 forKey:@"__tapstream_cookie_match_timestamp"];

	// Should cookie match
	[self.core fireEvent:[TSEvent eventWithName:@"test-event" oneTimeOnly:NO]];
	[self.core fireEvent:[TSEvent eventWithName:@"test-event2" oneTimeOnly:NO]];
	[[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:1]];
	NSArray* requests = [self.platform getRequests];

	XCTAssertEqual(2, [requests count]);
	NSString* firstUrl = [[requests firstObject] firstObject];
	NSString* secondUrl = [[requests lastObject] firstObject];
	if([firstUrl containsString:@"cookiematch=true"]){
		XCTAssertFalse([secondUrl containsString:@"cookiematch=true"]);
		XCTAssertEqual(@"GET", [[requests firstObject] lastObject]);
		XCTAssertEqual(@"POST", [[requests lastObject] lastObject]);
	}else{
		XCTAssertTrue([secondUrl containsString:@"cookiematch=true"]);
		XCTAssertEqual(@"POST", [[requests firstObject] lastObject]);
		XCTAssertEqual(@"GET", [[requests lastObject] lastObject]);
	}
}

/* Lander Features */

- (void) testLanderPlatformImpl {
	[[NSUserDefaults standardUserDefaults] removeObjectForKey:@"__tapstream_landers_shown"];
	XCTAssertFalse([self.platform landerShown:1]);
	[self.platform setLanderShown:1];
	XCTAssertTrue([self.platform landerShown:1]);
	XCTAssertFalse([self.platform landerShown:2]);
}

- (void) testInitTSLander {
	// Try to cause errors initializing TSLander; all should just return a nil lander.

	// Test with nil data
	TSLander* lander = [[TSLander alloc] initWithDescription:nil];

	// Empty JSON
	NSString* jsonStr = @"{}";
	NSDictionary* data = [NSJSONSerialization JSONObjectWithData:[jsonStr dataUsingEncoding:NSUTF8StringEncoding]
														 options:nil
														   error:nil];
	lander = [[TSLander alloc] initWithDescription:data];
	XCTAssertFalse([lander isValid]);

	// Null URL
	jsonStr = @"{\"url\": null}";
	data = [NSJSONSerialization JSONObjectWithData:[jsonStr dataUsingEncoding:NSUTF8StringEncoding]
														 options:nil
														   error:nil];
	lander = [[TSLander alloc] initWithDescription:data];
	XCTAssertFalse([lander isValid]);

	// Null Markup + URL + id
	jsonStr = @"{\"url\": null, \"markup\": null, \"id\": null}";
	data = [NSJSONSerialization JSONObjectWithData:[jsonStr dataUsingEncoding:NSUTF8StringEncoding]
										   options:nil
											 error:nil];
	lander = [[TSLander alloc] initWithDescription:data];
	XCTAssertFalse([lander isValid]);

	// Invalid (no scheme) url
	jsonStr = @"{\"url\": \"www.myspace.com\", \"markup\": null, \"id\": 31}";
	data = [NSJSONSerialization JSONObjectWithData:[jsonStr dataUsingEncoding:NSUTF8StringEncoding]
										   options:nil
											 error:nil];
	lander = [[TSLander alloc] initWithDescription:data];
	XCTAssertFalse([lander isValid]);

	// Valid url
	jsonStr = @"{\"url\": \"https://www.myspace.com\", \"markup\": null, \"id\": 31}";
	data = [NSJSONSerialization JSONObjectWithData:[jsonStr dataUsingEncoding:NSUTF8StringEncoding]
										   options:nil
											 error:nil];
	lander = [[TSLander alloc] initWithDescription:data];
	XCTAssertTrue([lander isValid]);

	// Valid markup
	jsonStr = @"{\"url\": null, \"markup\": \"<h1>Ok</h1>\", \"id\": 31}";
	data = [NSJSONSerialization JSONObjectWithData:[jsonStr dataUsingEncoding:NSUTF8StringEncoding]
										   options:nil
											 error:nil];
	lander = [[TSLander alloc] initWithDescription:data];
	XCTAssertTrue([lander isValid]);
}


@end
