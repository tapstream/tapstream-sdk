#pragma once
#import <Foundation/Foundation.h>
#import "TSCoreListener.h"
#import "TSOperationQueue.h"

@interface TSCoreListenerImpl : NSObject<TSCoreListener> {
@private
	TSOperationQueue *queue;
}

@property(nonatomic, STRONG_OR_RETAIN) TSOperationQueue *queue;

- (id)initWithQueue:(TSOperationQueue *)queue;
- (void)reportOperation:(NSString *)op;
- (void)reportOperation:(NSString *)op arg:(NSString *)arg;

@end