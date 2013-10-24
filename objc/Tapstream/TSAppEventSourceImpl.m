#import "TSAppEventSourceImpl.h"

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
#import <UIKit/UIKit.h>

Class TSSKPaymentQueue = nil;
Class TSSKProductsRequest = nil;

void TSLoadStoreKitClasses()
{
	if(TSSKPaymentQueue == nil)
	{
		TSSKPaymentQueue = NSClassFromString(@"SKPaymentQueue");
		TSSKProductsRequest = NSClassFromString(@"SKProductsRequest");
	}	
}




typedef void(^TSProductRequestCompletion)(SKProduct *);

@interface TSProductRequestDelegate : NSObject<SKProductsRequestDelegate>

@property(nonatomic, STRONG_OR_RETAIN) SKProduct *product;
@property(nonatomic, copy) TSProductRequestCompletion completion;

@end

@implementation TSProductRequestDelegate

@synthesize product, completion;

- (id)initWithCompletion:(TSProductRequestCompletion)completionVal
{
	if((self = [super init]) != nil)
	{
		self.completion = completionVal;
	}
	return self;
}

- (void)dealloc
{
	RELEASE(product);
	SUPER_DEALLOC;
}

- (void)productsRequest:(SKProductsRequest *)request didReceiveResponse:(SKProductsResponse *)response
{
	for(SKProduct *prod in response.products)
	{
		product = prod;
		break;
	}
	if(product != nil && completion != nil)
	{
		completion(product);
	}
}

@end




@interface TSAppEventSourceImpl()

@property(nonatomic, STRONG_OR_RETAIN) id<NSObject> foregroundedEventObserver;
@property(nonatomic, copy) TSOpenHandler onOpen;
@property(nonatomic, copy) TSTransactionHandler onTransaction;

- (id)init;
- (void)dealloc;

@end


@implementation TSAppEventSourceImpl

@synthesize foregroundedEventObserver, onOpen, onTransaction;

- (id)init
{
	if((self = [super init]) != nil)
	{
		TSLoadStoreKitClasses();

		self.foregroundedEventObserver = [[NSNotificationCenter defaultCenter] addObserverForName:UIApplicationWillEnterForegroundNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *note) {
			if(onOpen != nil)
			{
				onOpen();
			}
		}];

		if(TSSKPaymentQueue != nil)
		{
			[[TSSKPaymentQueue defaultQueue] addTransactionObserver:self];
		}
	}
	return self;
}

- (void)paymentQueue:(SKPaymentQueue *)queue updatedTransactions:(NSArray *)transactions
{
	if(onTransaction != nil)
	{
		for(SKPaymentTransaction *trans in transactions)
		{
			if(trans.transactionState == SKPaymentTransactionStatePurchased)
			{
				dispatch_async(dispatch_get_main_queue(), ^() {
					SKProductsRequest *req = AUTORELEASE([[TSSKProductsRequest alloc]
						initWithProductIdentifiers:[NSSet setWithObject:trans.payment.productIdentifier]
						]);
					req.delegate = AUTORELEASE([[TSProductRequestDelegate alloc] initWithCompletion:^(SKProduct *product) {
						onTransaction(trans.transactionIdentifier,
							product.productIdentifier,
							trans.payment.quantity,
							(int)([product.price doubleValue] * 100),
							[product.priceLocale objectForKey:NSLocaleCurrencyCode]
							);
					}]);
				});
			}
		}
	}
}

- (void)setOpenHandler:(TSOpenHandler)handler
{
	self.onOpen = handler;
}

- (void)setTransactionHandler:(TSTransactionHandler)handler
{
	self.onTransaction = handler;
}

- (void)dealloc
{
	if(TSSKPaymentQueue != nil)
	{
		[[TSSKPaymentQueue defaultQueue] removeTransactionObserver:self];
	}

	if(foregroundedEventObserver != nil)
	{
		[[NSNotificationCenter defaultCenter] removeObserver:foregroundedEventObserver];
	}

	RELEASE(onOpen);
	RELEASE(onTransaction);
	RELEASE(foregroundedEventObserver);
	SUPER_DEALLOC;
}

@end

#endif