//
//  TSOfferViewController.h
//  WordOfMouth
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TSHelpers.h"
#import "TSOffer.h"
#import "TSWordOfMouthDelegate.h"

@interface TSOfferViewController : UIViewController<UIWebViewDelegate>

@property(STRONG_OR_RETAIN, nonatomic) TSOffer *offer;
@property(STRONG_OR_RETAIN, nonatomic) UIViewController *parentViewController;
@property(assign, nonatomic) id<TSWordOfMouthDelegate> delegate;

+ (id)controllerWithOffer:(TSOffer *)offer parentViewController:(UIViewController *)parentViewController delegate:(id<TSWordOfMouthDelegate>)delegate;

@end
