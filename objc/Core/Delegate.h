#pragma once
#import <Foundation/Foundation.h>

@protocol Delegate<NSObject>
- (int)getDelay;
- (BOOL)isRetryAllowed;
@end