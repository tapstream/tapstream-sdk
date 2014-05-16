//
//  UserToUserController.h
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TSHelpers.h"
#import "TSOffer.h"
#import "TSReward.h"

typedef void (^TSUserToUserResultHandler)(NSArray *);

@interface TSUserToUserController : NSObject

- (id)initWithSecret:(NSString *)secret andUuid:(NSString *)uuid;
- (TSOffer *)offerForCodeLocation:(NSString *)locationTag timeout:(NSTimeInterval)timeoutSeconds;
- (void)showOffer:(TSOffer *)offer;

- (NSArray *)availableRewards;
- (void)consumeReward:(TSReward *)reward;

@end
