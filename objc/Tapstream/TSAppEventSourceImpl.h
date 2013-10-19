#pragma once
#import <Foundation/Foundation.h>
#import "TSHelpers.h"
#import "TSAppEventSource.h"

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR

@interface TSAppEventSourceImpl : NSObject<TSAppEventSource> {
@private
	id<NSObject> foregroundedEventObserver;
	void(^onOpen)();
}

- (void)setOnOpenHandler:(void(^)())handler;

@end

#endif