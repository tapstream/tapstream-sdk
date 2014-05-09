//
//  UserToUserController.m
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSUserToUserController.h"

@interface TSUserToUserController()
@property(strong, nonatomic) NSCondition *offersReady;
@property(strong, nonatomic) NSString *uuid;
@property(strong, nonatomic) NSArray *offers;
@property(strong, nonatomic) NSURLRequest *offersRequest;
@property(strong, nonatomic) NSURLRequest *rewardsRequest;
@property(assign, nonatomic) int retries;
@property(assign, nonatomic) BOOL requestingOffers;

+ (NSArray *)parseOffers:(NSData *)offersJson;
+ (NSArray *)parseRewards:(NSData *)rewardsJson;

@end

@implementation TSUserToUserController

@synthesize offersReady, uuid, offers, offersRequest, rewardsRequest, retries, requestingOffers;

- (id)initWithSecret:(NSString *)secret andUuid:(NSString *)uuidVal
{
    if(self = [super init])
    {
        self.uuid = uuidVal;
        self.offersReady = AUTORELEASE([[NSCondition alloc] init]);
        self.offersRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:@""]];
        self.rewardsRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:@""]];
        self.retries = 0;
        self.requestingOffers = NO;
        [self requestOffers];
    }
    return self;
}

- (void)dealloc
{
    RELEASE(self->uuid);
    RELEASE(self->offersReady);
    RELEASE(self->offersRequest);
    RELEASE(self->rewardsRequest);
}

- (void)offersForCodeLocation:(NSString *)locationTag results:(TSUserToUserResultHandler)handler
{
    if(handler) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^ {
            @synchronized(self) {
                if(!self.offers && !self.requestingOffers) {
                    self.requestingOffers = YES;
                    [self requestOffers];
                }
            }
            
            [self.offersReady wait];
            
            NSArray *results;
            @synchronized(self) {
                results = AUTORELEASE(self.offers);
            }
            handler(results);
        });
    }
}

- (void)showOffer:(TSOffer *)offer
{
    if(!offer) {
        return;
    }
    
}

- (void)availableRewards:(TSUserToUserResultHandler)handler
{
    if(handler) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^ {
            [NSURLConnection sendAsynchronousRequest:self.rewardsRequest queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *connectionError) {
                if(!connectionError && data) {
                    NSArray *rewards = [TSUserToUserController parseRewards:data];
                    // TODO: filter out rewards that have already been consumed
                    handler(rewards ? rewards : [NSArray array]);
                } else {
                    handler([NSArray array]);
                }
            }];
        });
    }
}

- (void)consumeReward:(TSReward *)reward
{
    // TODO: Save this reward's identifier somewhere so we know it cannot be offered again
}

- (void)requestOffers
{
    [NSURLConnection sendAsynchronousRequest:self.offersRequest queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *connectionError) {
        if(!connectionError && data) {
            NSArray *results = [TSUserToUserController parseOffers:data];
            @synchronized(self) {
                self.retries = 0;
                self.requestingOffers = NO;
                self.offers = results ? results : [NSArray array];
                [self.offersReady signal];
            }
        } else {
            @synchronized(self) {
                if(self.retries < 3) {
                    self.retries += 1;
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 2 * NSEC_PER_SEC), dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^ {
                        [self requestOffers];
                    });
                } else {
                    self.retries = 0;
                    self.requestingOffers = NO;
                    self.offers = [NSArray array];
                    [self.offersReady signal];
                }
            }
        }
    }];
}

+ (NSArray *)parseOffers:(NSData *)offersJson
{
    id json = [NSJSONSerialization JSONObjectWithData:offersJson options:0 error:nil];
    if(json) {
        NSMutableArray *results = [NSMutableArray arrayWithCapacity:32];
        
        return AUTORELEASE(results);
    }
    return nil;
}

+ (NSArray *)parseRewards:(NSData *)rewardsJson
{
    id json = [NSJSONSerialization JSONObjectWithData:rewardsJson options:0 error:nil];
    if(json) {
        NSMutableArray *results = [NSMutableArray arrayWithCapacity:32];
        
        return AUTORELEASE(results);
    }
    return nil;
}


@end
