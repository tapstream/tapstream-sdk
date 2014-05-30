//
//  TSOffer.h
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TSHelpers.h"

@interface TSOffer : NSObject

@property(STRONG_OR_RETAIN, nonatomic, readonly) NSDictionary *description;
@property(assign, nonatomic, readonly) NSUInteger ident;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSString *insertionPoint;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSString *message;
@property(assign, nonatomic, readonly) NSInteger rewardMinimumInstalls;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSString *rewardSku;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSString *bundle;
@property(assign, nonatomic, readonly) NSInteger minimumAge;
@property(assign, nonatomic, readonly) NSInteger rateLimit;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSString *markup;

- (id)initWithDescription:(NSDictionary *)description;

@end
