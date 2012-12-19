#pragma once
#import <Foundation/Foundation.h>
#import "Platform.h"
#import "Response.h"

@interface PlatformImpl : NSObject<Platform> {}

- (NSString *)loadUuid;
- (NSMutableSet *)loadFiredEvents;
- (void)saveFiredEvents:(NSMutableSet *)firedEvents;
- (NSString *)getResolution;
- (NSString *)getManufacturer;
- (NSString *)getModel;
- (NSString *)getOs;
- (NSString *)getLocale;
- (Response *)request:(NSString *)url data:(NSString *)data;

@end