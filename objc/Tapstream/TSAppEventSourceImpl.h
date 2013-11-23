#pragma once
#import <Foundation/Foundation.h>
#import "TSHelpers.h"
#import "TSAppEventSource.h"

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
#import <StoreKit/StoreKit.h>

@interface TSAppEventSourceImpl : NSObject<TSAppEventSource, SKPaymentTransactionObserver, SKProductsRequestDelegate> {
@private
	id<NSObject> foregroundedEventObserver;
	TSOpenHandler onOpen;
	TSTransactionHandler onTransaction;
    NSMutableDictionary *requestTransactions;
}

- (void)setOpenHandler:(TSOpenHandler)handler;
- (void)setTransactionHandler:(TSTransactionHandler)handler;

@end

#endif