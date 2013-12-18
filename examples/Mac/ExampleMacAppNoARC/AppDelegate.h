//
//  AppDelegate.h
//  ExampleMacAppNoARC
//
//  Created by Eric Roy on 2012-12-15.
//  Copyright (c) 2012 Example. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <StoreKit/StoreKit.h>

@interface AppDelegate : NSObject <NSApplicationDelegate, SKProductsRequestDelegate, SKPaymentTransactionObserver>

@property (assign) IBOutlet NSWindow *window;
@property (assign) NSArray *products;
@property (assign) SKProductsRequest *request;

@end
