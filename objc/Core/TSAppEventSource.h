#pragma once
#import <Foundation/Foundation.h>

@protocol TSAppEventSource<NSObject>
- (void)setOnOpenHandler:(void(^)())handler;
@end