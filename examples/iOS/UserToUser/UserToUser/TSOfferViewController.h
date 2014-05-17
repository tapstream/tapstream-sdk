//
//  TSOfferViewController.h
//  UserToUser
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TSHelpers.h"
#import "TSOffer.h"

@interface TSOfferViewController : UIViewController<UIWebViewDelegate>

@property(STRONG_OR_RETAIN, nonatomic) TSOffer *offer;

+ (id)controllerWithOffer:(TSOffer *)offer;

@end
