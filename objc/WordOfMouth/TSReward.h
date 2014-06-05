//
//  TSReward.h
//  WordOfMouth
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TSHelpers.h"

@interface TSReward : NSObject

@property(STRONG_OR_RETAIN, nonatomic, readonly) NSDictionary *description;
@property(assign, nonatomic, readonly) NSUInteger ident;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSString *name;
@property(assign, nonatomic, readonly) NSInteger installs;

- (id)initWithDescription:(NSDictionary *)description;

@end
