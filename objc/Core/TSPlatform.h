#pragma once
#import <Foundation/Foundation.h>
#import "TSResponse.h"

@protocol TSPlatform<NSObject>
- (NSString *)loadUuid;
- (NSMutableSet *)loadFiredEvents;
- (void)saveFiredEvents:(NSMutableSet *)firedEvents;
- (NSString *)getResolution;
- (NSString *)getManufacturer;
- (NSString *)getModel;
- (NSString *)getOs;
- (NSString *)getLocale;
- (TSResponse *)request:(NSString *)url data:(NSString *)data;
@end