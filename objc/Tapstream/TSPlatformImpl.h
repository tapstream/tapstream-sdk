#pragma once
#import <Foundation/Foundation.h>
#import "TSPlatform.h"
#import "TSResponse.h"
#import "TSLogging.h"

@interface TSPlatformImpl : NSObject<TSPlatform> {}

- (void)setPersistentFlagVal:(NSString*)key;
- (BOOL)getPersistentFlagVal:(NSString*)key;
- (BOOL) isFirstRun;
- (void) registerFirstRun;
- (NSString *)loadUuid;
- (NSMutableSet *)loadFiredEvents;
- (void)saveFiredEvents:(NSMutableSet *)firedEvents;
- (NSString *)getResolution;
- (NSString *)getManufacturer;
- (NSString *)getModel;
- (NSString *)getOs;
- (NSString *)getOsBuild;
- (NSString *)getLocale;
- (NSString *)getWifiMac;
- (NSString *)getAppName;
- (NSString *)getAppVersion;
- (NSString *)getPackageName;
- (TSResponse *)request:(NSString *)url data:(NSString *)data method:(NSString *)method timeout_ms:(int)timeout_ms;
- (NSString *)getComputerGUID;
- (NSString *)getBundleIdentifier;
- (NSString *)getBundleShortVersion;
@end
