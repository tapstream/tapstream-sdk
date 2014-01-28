#import "TSAppEventSourceImpl.h"

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
#import <UIKit/UIKit.h>
#else
#import <AppKit/AppKit.h>
#endif

Class TSSKPaymentQueue = nil;
Class TSSKProductsRequest = nil;

static void TSLoadStoreKitClasses()
{
	if(TSSKPaymentQueue == nil)
	{
		TSSKPaymentQueue = NSClassFromString(@"SKPaymentQueue");
		TSSKProductsRequest = NSClassFromString(@"SKProductsRequest");
	}	
}


@interface TSRequestWrapper : NSObject<NSCopying>
@property(nonatomic, STRONG_OR_RETAIN) SKProductsRequest *request;
+ (id)requestWrapperWithRequest:(SKProductsRequest *)req;
- (id)copyWithZone:(NSZone *)zone;
- (BOOL)isEqual:(id)other;
- (NSUInteger)hash;
@end

@implementation TSRequestWrapper
@synthesize request;
+ (id)requestWrapperWithRequest:(SKProductsRequest *)req
{
	return AUTORELEASE([[self alloc] initWithRequest:req]);
}
- (id)initWithRequest:(SKProductsRequest *)req
{
	if((self = [super init]) != nil)
	{
		self.request = req;
	}
	return self;
}
- (id)copyWithZone:(NSZone *)zone
{
	return [[[self class] allocWithZone:zone] initWithRequest:self.request];
}
- (BOOL)isEqual:(id)other
{
	if(self == other)
	{
		return YES;
	}
	if(!other || ![other isKindOfClass:[self class]])
	{
		return NO;
	}
	return self.request == ((TSRequestWrapper *)other).request;
}
- (NSUInteger)hash
{
	return (NSUInteger)self.request;
}
- (void)dealloc
{
	self.request = nil;
	SUPER_DEALLOC;
}
@end





@interface TSAppEventSourceImpl()

@property(nonatomic, STRONG_OR_RETAIN) id<NSObject> foregroundedEventObserver;
@property(nonatomic, copy) TSOpenHandler onOpen;
@property(nonatomic, copy) TSTransactionHandler onTransaction;
@property(nonatomic, STRONG_OR_RETAIN) NSMutableDictionary *requestTransactions;

- (id)init;
- (void)dealloc;

@end


@implementation TSAppEventSourceImpl

@synthesize foregroundedEventObserver, onOpen, onTransaction, requestTransactions;

- (id)init
{
	if((self = [super init]) != nil)
	{
		TSLoadStoreKitClasses();

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
		self.foregroundedEventObserver = [[NSNotificationCenter defaultCenter] addObserverForName:UIApplicationWillEnterForegroundNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *note) {
			if(onOpen != nil)
			{
				onOpen();
			}
		}];
#endif
		
		if(TSSKPaymentQueue != nil)
		{
			self.requestTransactions = [NSMutableDictionary dictionary];
			[[TSSKPaymentQueue defaultQueue] addTransactionObserver:self];
		}
	}
	return self;
}

- (void)paymentQueue:(SKPaymentQueue *)queue updatedTransactions:(NSArray *)transactions
{
}

- (void)paymentQueue:(SKPaymentQueue *)queue removedTransactions:(NSArray *)transactions
{
	if(onTransaction != nil)
	{
		NSMutableDictionary *transForProduct = [NSMutableDictionary dictionary];
		for(SKPaymentTransaction *trans in transactions)
		{
			if(trans.transactionState == SKPaymentTransactionStatePurchased)
			{
				[transForProduct setValue:trans forKey:trans.payment.productIdentifier];
			}
		}

		if([transForProduct count] > 0)
		{
			SKProductsRequest *req = AUTORELEASE([[TSSKProductsRequest alloc] initWithProductIdentifiers:[NSSet setWithArray:[transForProduct allKeys]]]);
			req.delegate = self;
			@synchronized(self)
			{
				[self.requestTransactions setObject:transForProduct forKey:[TSRequestWrapper requestWrapperWithRequest:req]];
			}
			[req start];
		}
	}
}

- (void)productsRequest:(SKProductsRequest *)request didReceiveResponse:(SKProductsResponse *)response
{
	NSMutableDictionary *transactions = nil;
	@synchronized(self)
	{
		TSRequestWrapper *key = [TSRequestWrapper requestWrapperWithRequest:request];
		transactions = [self.requestTransactions objectForKey:key];
		[self.requestTransactions removeObjectForKey:key];
	}
	if(transactions)
	{
		for(SKProduct *product in response.products)
		{
			SKPaymentTransaction *transaction = [transactions objectForKey:product.productIdentifier];
			if(transaction)
			{
				onTransaction(transaction.transactionIdentifier,
					product.productIdentifier,
					(int)transaction.payment.quantity,
					(int)([product.price doubleValue] * 100),
					[product.priceLocale objectForKey:NSLocaleCurrencyCode]
					);
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

	RELEASE(foregroundedEventObserver);
	RELEASE(requestTransactions);
	SUPER_DEALLOC;
}

@end




