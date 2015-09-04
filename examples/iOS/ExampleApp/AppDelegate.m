//
//  AppDelegate.m
//  ExampleApp
//
//  Created by Eric Roy on 2012-12-15.
//  Copyright (c) 2012 Example. All rights reserved.
//

#import "AppDelegate.h"
#import "TSTapstream.h"

@implementation AppDelegate

@synthesize products, request;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        UISplitViewController *splitViewController = (UISplitViewController *)self.window.rootViewController;
        UINavigationController *navigationController = [splitViewController.viewControllers lastObject];
        splitViewController.delegate = (id)navigationController.topViewController;
    }
    
    TSConfig *config = [TSConfig configWithDefaults];
    [config.globalEventParams setValue:@25.4 forKey:@"degrees"];
    
    [TSTapstream createWithAccountName:@"sdktest" developerSecret:@"YGP2pezGTI6ec48uti4o1w" config:config];

    
    TSTapstream *tracker = [TSTapstream instance];

	// Sync getConversionData
	NSData* jsonInfo = [tracker getConversionDataBlocking:10000];

	// Async getConversionData
	[tracker getConversionData:^(NSData* jsonInfo) {
		if(jsonInfo == nil)
		{
			// No conversion data available. This might be because Tapstream
			// took longer than 10 seconds to respond, or because the Tapstream
			// API server returned a 4xx response.
		} else {
			NSError *error      = nil;
			NSDictionary *jsonDict  = [NSJSONSerialization JSONObjectWithData:jsonInfo
																	  options:kNilOptions
																		error:&error];


			if(jsonDict && !error)
			{
				NSArray *hits = [jsonDict objectForKey:@"hits"];
				NSArray *events = [jsonDict objectForKey:@"events"];

				NSLog(@"Hits: %@", hits);
				NSLog(@"Events: %@", events);
				
			}
		}

	}];

    
    TSEvent *e = [TSEvent eventWithName:@"test-event" oneTimeOnly:NO];
    [e addValue:@"John Doe" forKey:@"player"];
    [e addValue:@10.1 forKey:@"degrees"];
    [e addValue:@5 forKey:@"score"];
    [[TSTapstream instance] fireEvent:e];

    [[SKPaymentQueue defaultQueue] addTransactionObserver:self];
    
    NSArray *productIds = @[@"com.tapstream.catalog.tiddlywinks"];
    request = [[SKProductsRequest alloc] initWithProductIdentifiers:[NSSet setWithArray:productIds]];
    request.delegate = self;
    [request start];
    
    return YES;
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
    NSLog(@"*** willEnterForeground");
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
