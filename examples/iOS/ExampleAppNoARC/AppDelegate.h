//
//  AppDelegate.h
//  ExampleAppNoARC
//
//  Created by Eric Roy on 2012-12-15.
//  Copyright (c) 2012 Example. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <StoreKit/StoreKit.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate, SKProductsRequestDelegate, SKPaymentTransactionObserver>

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) NSArray *products;
@property (strong, nonatomic) SKProductsRequest *request;

@end
