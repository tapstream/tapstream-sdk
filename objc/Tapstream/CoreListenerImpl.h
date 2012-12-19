#pragma once
#import <Foundation/Foundation.h>
#import "CoreListener.h"

@interface CoreListenerImpl : NSObject<CoreListener> {}

- (void)reportOperation:(NSString *)op;
- (void)reportOperation:(NSString *)op arg:(NSString *)arg;

@end