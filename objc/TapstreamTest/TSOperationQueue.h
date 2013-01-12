#pragma once
#import <Foundation/Foundation.h>
#import "TSHelpers.h"

@interface TSOperation : NSObject {
@private
	NSString *name;
	NSString *arg;
}

@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *name;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *arg;

- (id)initWithName:(NSString *)name arg:(NSString *)arg;

@end


@interface TSOperationQueue : NSObject {
@private
	NSMutableArray *queue;
    NSConditionLock *queueLock;
}

- (void)put:(TSOperation *)op;
- (TSOperation *)take;
- (void)expect:(NSString *)opName;

@end