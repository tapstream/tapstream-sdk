//
//  AppDelegate.m
//  ExampleMacAppNoARC
//
//  Created by Eric Roy on 2012-12-15.
//  Copyright (c) 2012 Example. All rights reserved.
//

#import "AppDelegate.h"
#import "TSTapstream.h"

@implementation AppDelegate

@synthesize products, request;

- (void)dealloc
{
    [products release];
    [request release];
    [super dealloc];
}

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    TSConfig *config = [TSConfig configWithDefaults];
    [config.globalEventParams setValue:@25.4 forKey:@"degrees"];
    
    [TSTapstream createWithAccountName:@"sdktest" developerSecret:@"YGP2pezGTI6ec48uti4o1w" config:config];
    
    TSTapstream *tracker = [TSTapstream instance];
    [tracker getConversionData:^(NSData *jsonInfo) {
        if(jsonInfo == nil)
        {
            // No conversion data available
            NSLog(@"No conversion data");
        }
        else
        {
            NSLog(@"Conversion data: %@", [[[NSString alloc] initWithData:jsonInfo encoding:NSUTF8StringEncoding] autorelease]);
            NSError *error;
            NSArray *json = [NSJSONSerialization JSONObjectWithData:jsonInfo options:kNilOptions error:&error];
            if(json && !error)
            {
                // Read some data from this json object, and modify your application's behaviour accordingly
                // ...
            }
        }
    }];
    
    
    TSEvent *e = [TSEvent eventWithName:@"test-event" oneTimeOnly:NO];
    [e addValue:@"John Doe" forKey:@"player"];
    [e addValue:@10.1 forKey:@"degrees"];
    [e addValue:@5 forKey:@"score"];
    [tracker fireEvent:e];
    
    
    [[SKPaymentQueue defaultQueue] addTransactionObserver:self];
    
    NSArray *productIds = @[@"com.tapstream.catalog.tiddlywinks"];
    self.request = [[SKProductsRequest alloc] initWithProductIdentifiers:[NSSet setWithArray:productIds]];
    self.request.delegate = self;
    [self.request start];

}

- (void)request:(SKRequest *)request didFailWithError:(NSError *)error
{
    NSLog(@"%@", error.description);
}

// SKProductsRequestDelegate protocol method
- (void)productsRequest:(SKProductsRequest *)request didReceiveResponse:(SKProductsResponse *)response
{
    self.products = response.products;
    
    for (NSString *ident in response.invalidProductIdentifiers) {
        NSLog(@"Invalid product id: %@", ident);
    }
    
    for (NSString *ident in response.products) {
        NSLog(@"Product id: %@", ident);
    }
    
    if(self.products.count > 0) {
        SKProduct *product = [self.products objectAtIndex:0];
        
        SKMutablePayment *payment = [SKMutablePayment paymentWithProduct:product];
        payment.quantity = 2;
        
        [[SKPaymentQueue defaultQueue] addPayment:payment];
    }
}

- (void)paymentQueue:(SKPaymentQueue *)queue updatedTransactions:(NSArray *)transactions
{
    for (SKPaymentTransaction *transaction in transactions) {
        switch (transaction.transactionState) {
                // Call the appropriate custom method.
            case SKPaymentTransactionStatePurchased:
                NSLog(@"StatePurchased %@", transaction.payment.productIdentifier);
                [queue finishTransaction:transaction];
                break;
            case SKPaymentTransactionStateFailed:
                NSLog(@"StateFailed %@", transaction.payment.productIdentifier);
                [queue finishTransaction:transaction];
                break;
            case SKPaymentTransactionStateRestored:
                NSLog(@"StateRestored %@", transaction.payment.productIdentifier);
                break;
            default:
                break;
        }
    }
}


@end
