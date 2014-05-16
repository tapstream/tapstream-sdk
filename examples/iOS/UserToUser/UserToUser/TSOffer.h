//
//  TSOffer.h
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TSOffer : NSObject

@property(strong, nonatomic, readonly) NSDictionary *description;
@property(assign, nonatomic, readonly) NSUInteger ident;
@property(strong, nonatomic, readonly) NSString *name;
@property(assign, nonatomic, readonly) NSInteger rewardMinimumInstalls;
@property(strong, nonatomic, readonly) NSString *rewardSku;
@property(strong, nonatomic, readonly) NSString *bundle;
@property(assign, nonatomic, readonly) NSInteger minimumAge;
@property(assign, nonatomic, readonly) NSInteger rateLimit;

- (id)initWithDescription:(NSDictionary *)description;

@end
