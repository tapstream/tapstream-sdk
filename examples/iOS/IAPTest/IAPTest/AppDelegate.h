//
//  AppDelegate.h
//  IAPTest
//
//  Created by Eric on 11/22/2013.
//  Copyright (c) 2013 Tapstream. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <StoreKit/StoreKit.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate, SKProductsRequestDelegate, SKPaymentTransactionObserver>

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) NSArray *products;
@property (strong, nonatomic) SKRequest *productsRequest;

@end
