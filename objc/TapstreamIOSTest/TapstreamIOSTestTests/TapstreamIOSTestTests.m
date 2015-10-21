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
@property(nonatomic, strong) dispatch_semaphore_t sem;
@property(nonatomic) BOOL firstRun;
@property(nonatomic, strong) NSMutableArray<NSArray<NSString*>*>* requests;
- (dispatch_semaphore_t)expectRequest;
- (NSMutableArray*)getRequests;
@end

@implementation TSPlatformTestImpl
- (TSPlatformTestImpl*)initWithDelegate:(id<TSPlatform>) delegate
{
	self = [super init];
	self.delegate = delegate;
	self.requests = [[NSMutableArray alloc] initWithCapacity:10];
	self.sem = dispatch_semaphore_create(0);
	self.firstRun;
	return self;
}

- (TSResponse *)request:(NSString *)url data:(NSString *)data method:(NSString *)method timeout_ms:(int)timeout_ms
{

	NSArray<NSString*>* requestData = [[NSArray alloc] initWithObjects:url, data, method, nil];
	[self.requests addObject:requestData];
	dispatch_semaphore_signal(self.sem);
	return [[TSResponse alloc] initWithStatus:200 message:@"ok" data:nil];
}

- (void)fireCookieMatch:(NSURL*)url completion:(void(^)(TSResponse*))completion
{
	NSArray* requestData = [[NSArray alloc] initWithObjects:[url absoluteString], @"", @"GET", nil];
	[self.requests addObject: requestData];
	completion([[TSResponse alloc] initWithStatus:200 message:@"ok" data:nil]);
	dispatch_semaphore_signal(self.sem);
}

- (NSMutableArray*)getRequests
{
	return self.requests;
}

- (dispatch_semaphore_t)expectRequest
{
	return self.sem;
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
- (BOOL) shouldCookieMatch{ return [self.delegate shouldCookieMatch]; }
- (void)setCookieMatchFired:(NSTimeInterval)t{ [self.delegate setCookieMatchFired:t]; }

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

	[[NSUserDefaults standardUserDefaults]
	 setDouble:0
	 forKey:@"__tapstream_cookie_match_timestamp"];

	// Startup -- should cookie match
	dispatch_semaphore_t flag = [self.platform expectRequest];
	[self.core start];
	dispatch_semaphore_wait(flag, DISPATCH_TIME_FOREVER);
	NSArray* requests = [self.platform getRequests];

	XCTAssertEqual(1, [requests count]);
	NSString* cookieMatchUrl = [[requests firstObject] firstObject];
	XCTAssertTrue([[[requests firstObject] lastObject] isEqualToString:@"GET"]);
	XCTAssertTrue([cookieMatchUrl containsString:@"cookiematch=true"]);

	[self.core firedCookieMatch];
	// Send event -- cookie match already done.
	double timestampBefore = [[NSUserDefaults standardUserDefaults]
							  doubleForKey:@"__tapstream_cookie_match_timestamp"];
	XCTAssertFalse([self.platform shouldCookieMatch]);
	[self.core fireEvent:[TSEvent eventWithName:@"test-event" oneTimeOnly:NO]];
	dispatch_semaphore_wait(flag, DISPATCH_TIME_FOREVER);
	XCTAssertEqual(2, [requests count]);

	NSString* regularEventUrl = [[requests lastObject] firstObject];
	XCTAssertFalse([regularEventUrl containsString:@"cookiematch=true"]);


	// Send event tomorrow (reset timestamp) -- should cookie match
	timestampBefore = [[NSUserDefaults standardUserDefaults]
							  doubleForKey:@"__tapstream_cookie_match_timestamp"];
	[[NSUserDefaults standardUserDefaults]
	 setDouble:(timestampBefore - 86400.0)
	 forKey:@"__tapstream_cookie_match_timestamp"];
	[self.core fireEvent:[TSEvent eventWithName:@"test-event2" oneTimeOnly:NO]];
	dispatch_semaphore_wait(flag, DISPATCH_TIME_FOREVER);
	XCTAssertEqual(3, [requests count]);

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

	dispatch_semaphore_t flag = [self.platform expectRequest];

	// Should cookie match
	[self.core fireEvent:[TSEvent eventWithName:@"test-event" oneTimeOnly:NO]];
	[self.core fireEvent:[TSEvent eventWithName:@"test-event2" oneTimeOnly:NO]];
	dispatch_semaphore_wait(flag, DISPATCH_TIME_FOREVER);
	dispatch_semaphore_wait(flag, DISPATCH_TIME_FOREVER);
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

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
