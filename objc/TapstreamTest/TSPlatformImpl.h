#pragma once
#import <Foundation/Foundation.h>
#import "TSPlatform.h"
#import "TSResponse.h"
#import "TSHelpers.h"

@interface TSPlatformImpl : NSObject<TSPlatform> {
	TSResponse *response;
	NSArray *savedFiredList;
}

@property(nonatomic, STRONG_OR_RETAIN) TSResponse *response;
@property(nonatomic, STRONG_OR_RETAIN) NSArray *savedFiredList;

- (NSString *)loadUuid;
- (NSMutableSet *)loadFiredEvents;
- (void)saveFiredEvents:(NSMutableSet *)firedEvents;
- (NSString *)getResolution;
- (NSString *)getManufacturer;
- (NSString *)getModel;
- (NSString *)getOs;
- (NSString *)getLocale;
- (NSString *)getWifiMac;
- (NSString *)getAppName;
- (NSString *)getAppVersion;
- (NSString *)getPackageName;
- (TSResponse *)request:(NSString *)url data:(NSString *)data method:(NSString *)method;
- (NSSet *)getProcessSet;
- (NSString *)getComputerGUID;
- (NSString *)getBundleIdentifier;
- (NSString *)getBundleShortVersion;
@end