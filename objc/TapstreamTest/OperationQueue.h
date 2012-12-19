#pragma once
#import <Foundation/Foundation.h>
#import "helpers.h"

@interface Operation : NSObject {
@private
	NSString *name;
	NSString *arg;
}

@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *name;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *arg;

- (id)initWithName:(NSString *)name arg:(NSString *)arg;

@end


@interface OperationQueue : NSObject {
@private
	NSMutableArray *queue;
    NSConditionLock *queueLock;
}

- (void)put:(Operation *)op;
- (Operation *)take;
- (void)expect:(NSString *)opName;

@end