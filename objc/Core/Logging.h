#pragma once
#import <Foundation/Foundation.h>
#import "LogLevel.h"

@interface Logging : NSObject {
}

+ (void)setLogger:(void(^)(int logLevel, NSString *msg))logger;
+ (void)logAtLevel:(LoggingLevel)level format:(NSString *)format, ...;

@end