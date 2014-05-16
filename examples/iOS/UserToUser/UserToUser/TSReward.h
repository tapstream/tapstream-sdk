//
//  TSReward.h
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TSReward : NSObject

@property(strong, nonatomic, readonly) NSDictionary *description;
@property(assign, nonatomic, readonly) NSUInteger ident;
@property(strong, nonatomic, readonly) NSString *name;
@property(assign, nonatomic, readonly) NSInteger installs;

- (id)initWithDescription:(NSDictionary *)description;

@end
