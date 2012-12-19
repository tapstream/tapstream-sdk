#pragma once
#import <Foundation/Foundation.h>
#import "CoreListener.h"
#import "OperationQueue.h"

@interface CoreListenerImpl : NSObject<CoreListener> {
@private
	OperationQueue *queue;
}

@property(nonatomic, STRONG_OR_RETAIN) OperationQueue *queue;

- (id)initWithQueue:(OperationQueue *)queue;
- (void)reportOperation:(NSString *)op;
- (void)reportOperation:(NSString *)op arg:(NSString *)arg;

@end