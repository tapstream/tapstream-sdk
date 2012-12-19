#pragma once
#import <Foundation/Foundation.h>

@protocol CoreListener<NSObject>
- (void)reportOperation:(NSString *)op;
- (void)reportOperation:(NSString *)op arg:(NSString *)arg;
@end
