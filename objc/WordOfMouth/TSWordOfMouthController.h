//
//  WordOfMouthController.h
//  WordOfMouth
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TSHelpers.h"
#import "TSOffer.h"
#import "TSReward.h"
#import "TSWordOfMouthDelegate.h"

@interface TSWordOfMouthController : NSObject<TSWordOfMouthDelegate>

@property(assign) id<TSWordOfMouthDelegate> delegate;

/**
 @brief Checks if there is an offer than can be shown from the code location indicated by insertionPoint.
 @param insertionPoint A string identifying a certain location within the flow of your application.
 @param callback Block that receives either a TSOffer instance or nil.  This callback is main on the main thread.
 */
- (void)offerForInsertionPoint:(NSString *)insertionPoint result:(void (^)(TSOffer *))callback;

/**
 @brief Displays the specified offer to your user.
 @param offer The offer to show
 @param parentViewController The view controller that will be used to show the offer views.
 */
- (void)showOffer:(TSOffer *)offer parentViewController:(UIViewController *)parentViewController;

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
