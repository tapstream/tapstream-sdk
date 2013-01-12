#pragma once
#import <Foundation/Foundation.h>

@protocol TSDelegate<NSObject>
- (int)getDelay;
- (BOOL)isRetryAllowed;
@end