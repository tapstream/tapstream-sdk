#pragma once
#import <Foundation/Foundation.h>
#import "TSEvent.h"
#import "TSDelegate.h"
#import "TSPlatform.h"
#import "TSCoreListener.h"
#import "TSHit.h"
#import "TSResponse.h"
#import "TSConfig.h"
#import "TSAppEventSource.h"
#import "TSUniversalLink.h"

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
#define kTSPlatform @"iOS"
#else
#define kTSPlatform @"Mac"
#endif

@interface TSCore : NSObject {
@private
	id<TSDelegate> del;
	id<TSPlatform> platform;
	id<TSCoreListener> listener;
	id<TSAppEventSource> appEventSource;
	TSConfig *config;
	NSString *accountName;
	NSMutableString *postData;
	NSMutableSet *firingEvents;
	NSMutableSet *firedEvents;
	NSString *failingEventId;
	NSString *appName;
	int delay;
	dispatch_semaphore_t cookieMatchFired;
}


- (id)initWithDelegate:(id<TSDelegate>)delegate platform:(id<TSPlatform>)platform listener:(id<TSCoreListener>)listener appEventSource:(id<TSAppEventSource>)appEventSource accountName:(NSString *)accountName developerSecret:(NSString *)developerSecret config:(TSConfig *)config;
#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
- (void)attachIdfaIfNotPresent:(TSConfig *)configVal;
#endif
- (void)start;
- (void)fireEvent:(TSEvent *)event;
- (void)fireHit:(TSHit *)hit completion:(void(^)(TSResponse *))completion;
- (void)firedCookieMatch;
- (void)getConversionData:(void(^)(NSData *))completion;
- (NSData*)getConversionDataBlocking:(int)timeout_ms;
- (int)getDelay;
- (NSURL*)makeLanderURL;
- (NSURL*)makeCookieMatchURL;
- (NSURL*)makeCookieMatchURL:(NSString*)eventName data:(NSString*)data;
- (NSMutableString *)postData;
- (void)dispatchOnQueue:(void(^)())completion;

#ifdef __IPHONE_OS_VERSION_MAX_ALLOWED
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 80000
- (TSUniversalLink*)handleUniversalLink:(NSURL*) url;
#endif
#endif

@end
