#pragma once
#import <Foundation/Foundation.h>
#import "Response.h"

@protocol Platform<NSObject>
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