//
//  TSUserToUserDelegate.h
//  UserToUser
//
//  Created by Eric on 2014-05-17.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol TSUserToUserDelegate <NSObject>

- (void)showedOffer:(NSUInteger)offerId;
- (void)dismissedOffer:(BOOL)accepted;
- (void)showedSharing:(NSUInteger)offerId;
- (void)dismissedSharing;
- (void)completedShare:(NSUInteger)offerId socialMedium:(NSString *)medium;

@end
