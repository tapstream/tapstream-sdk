#pragma once
#import <Foundation/Foundation.h>
#import "helpers.h"

@interface Hit : NSObject {
@private
	NSString *trackerName;
	NSString *encodedTrackerName;
	NSMutableString *tags;
}

@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *trackerName;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *encodedTrackerName;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *postData;

+ (id)hitWithTrackerName:(NSString *)trackerName;
- (void)addTag:(NSString *)tag;

@end