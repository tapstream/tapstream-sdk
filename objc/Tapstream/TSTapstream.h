#pragma once
#import <Foundation/Foundation.h>
#import "TSApi.h"
#import "TSDelegate.h"
#import "TSPlatform.h"
#import "TSCoreListener.h"
#import "TSAppEventSource.h"
#import "TSCore.h"
#import "TSEvent.h"
#import "TSResponse.h"
#import "TSLogging.h"
#import "TSWordOfMouthDelegate.h"
#import "TSLanderDelegate.h"
#import "TSUniversalLink.h"

@interface TSTapstream : NSObject<TSApi, TSWordOfMouthDelegate> {
@private
	id<TSDelegate> del;
	id<TSPlatform> platform;
	id<TSCoreListener> listener;
	id<TSAppEventSource> appEventSource;
    id wordOfMouthController;
	TSCore *core;
}

+ (void)createWithAccountName:(NSString *)accountName developerSecret:(NSString *)developerSecret config:(TSConfig *)config;
+ (id)instance;
+ (id)wordOfMouthController;

- (void)fireEvent:(TSEvent *)event;
- (void)fireHit:(TSHit *)hit completion:(void(^)(TSResponse *))completion;
- (void)getConversionData:(void(^)(NSData *))completion;
- (NSData*)getConversionDataBlocking:(int)timeout_ms;
- (void)showLanderIfExistsWithDelegate:(id<TSLanderDelegate>)delegate;

#ifdef __IPHONE_OS_VERSION_MAX_ALLOWED
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 80000

- (TSUniversalLink*)handleUniversalLink:(NSUserActivity*)userActivity;
#endif
#endif

@end
