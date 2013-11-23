//
//  AppDelegate.m
//  IAPTest
//
//  Created by Eric on 11/22/2013.
//  Copyright (c) 2013 Tapstream. All rights reserved.
//

#import "AppDelegate.h"
#import "TSTapstream.h"

@implementation AppDelegate

@synthesize products, productsRequest;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    
    TSConfig *config = [TSConfig configWithDefaults];
//    config.conversionListener = ^(NSData *jsonInfo) {
//        NSError *error;
//        NSArray *json = [NSJSONSerialization JSONObjectWithData:jsonInfo options:kNilOptions error:&error];
//        if(json && !error)
//        {
//            // Read some data from this json object, and modify your application's behaviour accordingly
//            // ...
//        }
//    };
    
    [TSTapstream createWithAccountName:@"sdktest" developerSecret:@"YGP2pezGTI6ec48uti4o1w" config:config];

    TSTapstream *tracker = [TSTapstream instance];

    TSEvent *e = [TSEvent eventWithName:@"test-event" oneTimeOnly:NO];
    [e addValue:@"John Doe" forKey:@"player"];
    [e addIntegerValue:5 forKey:@"score"];
    [tracker fireEvent:e];
    
    
    [[SKPaymentQueue defaultQueue] addTransactionObserver:self];
    
    NSArray *productIds = @[@"com.tapstream.catalog.tiddlywinks"];
    self.productsRequest = [[SKProductsRequest alloc] initWithProductIdentifiers:[NSSet setWithArray:productIds]];
    self.productsRequest.delegate = self;
    [self.productsRequest start];
    
    return YES;
}

// SKProductsRequestDelegate protocol method
- (void)request:(SKRequest *)request didFailWithError:(NSError *)error
{
    NSLog(@"%@", error);
}
- (void)requestDidFinish:(SKRequest *)request
{
    NSLog(@"Request finished");
}
- (void)productsRequest:(SKProductsRequest *)request didReceiveResponse:(SKProductsResponse *)response
{
    self.products = response.products;
    self.productsRequest = nil;
    
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
            case SKPaymentTransactionStatePurchasing:
                NSLog(@"StatePurchasing %@ %@", transaction.transactionIdentifier, transaction.payment.productIdentifier);
                break;
            case SKPaymentTransactionStatePurchased:
                NSLog(@"StatePurchased %@ %@", transaction.transactionIdentifier, transaction.payment.productIdentifier);
                [[SKPaymentQueue defaultQueue] finishTransaction:transaction];
                break;
            case SKPaymentTransactionStateFailed:
                NSLog(@"StateFailed %@ %@", transaction.transactionIdentifier, transaction.payment.productIdentifier);
                [[SKPaymentQueue defaultQueue] finishTransaction:transaction];
                break;
            case SKPaymentTransactionStateRestored:
                NSLog(@"StateRestored %@ %@", transaction.transactionIdentifier, transaction.payment.productIdentifier);
                break;
            default:
                break;
        }
    }
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
