//
//  UserToUserController.m
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSUserToUserController.h"
#import "TSOfferViewController.h"
#import "TSShareViewController.h"

#define kTSMaxOfferRetries 8
#define kTSConsumedRewardsKey @"__tapstream_consumed_rewards"

@interface TSUserToUserController()
@property(strong, nonatomic) NSConditionLock *offersReady;
@property(strong, nonatomic) NSArray *offers;
@property(strong, nonatomic) NSMutableSet *consumedRewards;
@property(strong, nonatomic) NSURLRequest *offersRequest;
@property(strong, nonatomic) NSURLRequest *rewardsRequest;
@property(assign, nonatomic) int retries;
@property(assign, nonatomic) BOOL requestingOffers;

+ (NSArray *)parseOffers:(NSData *)offersJson;
+ (NSArray *)parseRewards:(NSData *)rewardsJson;

@end

@implementation TSUserToUserController

@synthesize offersReady, offers, consumedRewards, offersRequest, rewardsRequest, retries;

- (id)initWithSecret:(NSString *)secret andUuid:(NSString *)uuid
{
    if(self = [super init]) {
        self.offersReady = AUTORELEASE([[NSConditionLock alloc] initWithCondition:NO]);
        self.offersRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"https://app.tapstream.com/api/v1/user-to-user/offers/?secret=%@", secret]]];
        self.rewardsRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"https://app.tapstream.com/api/v1/user-to-user/rewards/?secret=%@&event_session=%@", secret, uuid]]];
        self.retries = 0;
        
        NSArray *rewardIds = [[NSUserDefaults standardUserDefaults] arrayForKey:kTSConsumedRewardsKey];
        self.consumedRewards = [NSMutableSet setWithArray:rewardIds ? rewardIds : [NSArray array]];
        
        [self requestOffers];
    }
    return self;
}

- (void)dealloc
{
    RELEASE(self->offersReady);
    RELEASE(self->consumedRewards);
    RELEASE(self->offersRequest);
    RELEASE(self->rewardsRequest);
}

- (TSOffer *)offerForCodeLocation:(NSString *)locationTag timeout:(NSTimeInterval)timeoutSeconds
{
    __block TSOffer *match;
    NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:timeoutSeconds];
    if([self.offersReady lockWhenCondition:YES beforeDate:deadline]) {
        [self.offers enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
            if([((TSOffer *)obj).name isEqualToString:locationTag]) {
                match = AUTORELEASE((TSOffer *)obj);
                *stop = YES;
            }
        }];
        [self.offersReady unlock];
    } else {
        NSLog(@"Timed out waiting for eligible offer");
    }
    return match;
}

- (void)showOffer:(TSOffer *)offer navigationController:(UINavigationController *)navigationController
{
    if(offer && navigationController) {
        //TSOfferViewController *offerViewController = [TSOfferViewController controllerWithOffer:offer];
        //[navigationController pushViewController:offerViewController animated:YES];
        
        TSShareViewController *shareViewController = [TSShareViewController shareViewController];
        [navigationController pushViewController:shareViewController animated:YES];
    }
}

- (NSArray *)availableRewards
{
    NSHTTPURLResponse *response;
    NSError *error;
    NSData *data = [NSURLConnection sendSynchronousRequest:self.rewardsRequest returningResponse:&response error:&error];
    
    NSArray *results = [NSArray array];
    if(!error && response && response.statusCode >= 200 && response.statusCode < 300 && data) {
        results = [TSUserToUserController parseRewards:data];
        
        // Filter out any rewards that have already been consumed
        @synchronized(self.consumedRewards) {
            results = [results filteredArrayUsingPredicate:[NSPredicate predicateWithBlock:^BOOL(id obj, NSDictionary *bindings) {
                return ![self.consumedRewards containsObject:[NSNumber numberWithInteger:((TSReward *)obj).ident]];
            }]];
        }
    }
    return results;
}

- (void)consumeReward:(TSReward *)reward
{
    if(reward) {
        @synchronized(self.consumedRewards) {
            [self.consumedRewards addObject:[NSNumber numberWithInteger:reward.ident]];
            [[NSUserDefaults standardUserDefaults] setObject:[self.consumedRewards allObjects] forKey:kTSConsumedRewardsKey];
        }
    }
}

- (void)requestOffers
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(void) {
        NSHTTPURLResponse *response;
        NSError *error;
        NSData *data = [NSURLConnection sendSynchronousRequest:self.offersRequest returningResponse:&response error:&error];
        
        NSInteger status = response ? ((NSHTTPURLResponse *)response).statusCode : -1;
        BOOL success = data && !error && status >= 200 && status < 300;
        BOOL retry = status < 0 || (status >= 500 && status < 600);
        
        NSLog(@"Offers request complete (status %d)", (int)status);
        
        if(success) {
            NSArray *results = [TSUserToUserController parseOffers:data];
            [self.offersReady lock];
            self.retries = 0;
            self.offers = results ? results : [NSArray array];
            [self.offersReady unlockWithCondition:YES];
            
        } else if(retry && self.retries < kTSMaxOfferRetries) {
            [self.offersReady lock];
            self.retries += 1;
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, MIN(128, pow(2, self.retries)) * NSEC_PER_SEC), dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^ {
                [self requestOffers];
            });
            [self.offersReady unlock];
            
        } else {
            [self.offersReady lock];
            self.retries = 0;
            self.offers = [NSArray array];
            [self.offersReady unlockWithCondition:YES];
            
        }
    });
}

+ (NSArray *)parseOffers:(NSData *)offersJson
{
    NSArray *json = [NSJSONSerialization JSONObjectWithData:offersJson options:0 error:nil];
    if(json) {
        NSMutableArray *results = [NSMutableArray arrayWithCapacity:32];
        [json enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
            [results addObject:[[TSOffer alloc] initWithDescription:(NSDictionary *)obj]];
        }];
        return results;
    }
    return nil;
}

+ (NSArray *)parseRewards:(NSData *)rewardsJson
{
    NSArray *json = [NSJSONSerialization JSONObjectWithData:rewardsJson options:0 error:nil];
    if(json) {
        NSMutableArray *results = [NSMutableArray arrayWithCapacity:32];
        [json enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
            [results addObject:[[TSReward alloc] initWithDescription:(NSDictionary *)obj]];
        }];
        return results;
    }
    return nil;
}


@end
