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

/**
 @brief Checks if there is an offer than can be shown from the code location indicated by locationTag.
 This operation @b BLOCKS until offer information is available, or the provided timeout elapses,
 whichever comes first.
 @param locationTag A string identifying a certain location within the flow of your application.
 @param timeoutSeconds The maximum time this call is allowed to block, in seconds.
 @return A TSOffer instance if available, else nil.
 */
- (TSOffer *)offerForCodeLocation:(NSString *)locationTag timeout:(NSTimeInterval)timeoutSeconds;

/**
 @brief Displays the specified offer to your user.
 @param offer The offer to show
 */
- (void)showOffer:(TSOffer *)offer;

/**
 @brief Request an array of awards that should be delivered to this user.  This method makes a network
 request and is @b BLOCKING.
 For each reward in the returned array, deliver the reward, and then call consumeReward, passing
 the reward in question as an argument.
 @return An array of TSReward instances.
 */
- (NSArray *)availableRewards;

/**
 @brief Consumes a reward.  Call this once you have delivered the reward to your user.  After a reward
 is consumed, it will not be returned again in the availableAwards array.
 @param reward The reward to consume.
 */
- (void)consumeReward:(TSReward *)reward;

@end
