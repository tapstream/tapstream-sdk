#pragma once
#import <Foundation/Foundation.h>
#import "Platform.h"
#import "Response.h"
#import "helpers.h"

@interface PlatformImpl : NSObject<Platform> {
	Response *response;
	NSArray *savedFiredList;
}

@property(nonatomic, STRONG_OR_RETAIN) Response *response;
@property(nonatomic, STRONG_OR_RETAIN) NSArray *savedFiredList;

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