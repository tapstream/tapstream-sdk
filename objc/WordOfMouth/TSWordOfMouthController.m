//
//  WordOfMouthController.m
//  WordOfMouth
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSWordOfMouthController.h"
#import "TSOfferViewController.h"
#import "TSShareViewController.h"
#import "TSTapstream.h"
#import "TSUtils.h"

#define kTSMaxOfferRetries 8
#define kTSConsumedRewardsKey @"__tapstream_consumed_rewards"
#define kTSInstallDateKey @"__tapstream_install_date"
#define kTSLastOfferImpressionTimesKey @"__tapstream_last_offer_impression_times"
#define kTSWordOfMouthOffersEndPoint @"https://app.tapstream.com/api/v1/word-of-mouth/offers/?secret=%@&bundle=%@&insertion_point=%@"
#define kTSWordOfMouthRewardsEndPoint @"https://app.tapstream.com/api/v1/word-of-mouth/rewards/?secret=%@&event_session=%@"

@interface TSWordOfMouthController()
@property(STRONG_OR_RETAIN, nonatomic) NSString *secret;
@property(STRONG_OR_RETAIN, nonatomic) NSString *bundle;
@property(STRONG_OR_RETAIN, nonatomic) NSDate *installDate;
@property(STRONG_OR_RETAIN, nonatomic) NSMutableDictionary *lastOfferImpressionTimes;
@property(STRONG_OR_RETAIN, nonatomic) NSMutableDictionary *offerCache;
@property(STRONG_OR_RETAIN, nonatomic) NSMutableSet *consumedRewards;
@property(STRONG_OR_RETAIN, nonatomic) NSURLRequest *rewardsRequest;
@property(STRONG_OR_RETAIN, nonatomic) TSOfferViewController *offerViewController;
@property(STRONG_OR_RETAIN, nonatomic) TSShareViewController *shareViewController;

+ (NSArray *)parseRewards:(NSData *)rewardsJson;

@end

@implementation TSWordOfMouthController

@synthesize delegate, secret, bundle, installDate, lastOfferImpressionTimes, offerCache, consumedRewards, rewardsRequest, offerViewController, shareViewController;

- (id)initWithSecret:(NSString *)secretVal uuid:(NSString *)uuid bundle:(NSString *)bundleVal
{
    if(self = [super init]) {
        self.secret = secretVal;
        self.bundle = [TSUtils encodeString:bundleVal];
        
        self.installDate = [[NSUserDefaults standardUserDefaults] objectForKey:kTSInstallDateKey];
        if(!self.installDate) {
            self.installDate = [NSDate date];
            [[NSUserDefaults standardUserDefaults] setObject:self.installDate forKey:kTSInstallDateKey];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }
        
        self.lastOfferImpressionTimes = [[NSUserDefaults standardUserDefaults] objectForKey:kTSLastOfferImpressionTimesKey];
        if(!self.lastOfferImpressionTimes) {
            self.lastOfferImpressionTimes = [NSMutableDictionary dictionaryWithCapacity:8];
        }
        
        self.offerCache = [NSMutableDictionary dictionaryWithCapacity:8];
        self.rewardsRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:kTSWordOfMouthRewardsEndPoint, secret, uuid]]];
        
        NSArray *rewardIds = [[NSUserDefaults standardUserDefaults] arrayForKey:kTSConsumedRewardsKey];
        self.consumedRewards = [NSMutableSet setWithArray:rewardIds ? rewardIds : [NSArray array]];
    }
    return self;
}

- (void)dealloc
{
    SUPER_DEALLOC;
    
    RELEASE(self->secret);
    RELEASE(self->bundle);
    RELEASE(self->installDate);
    RELEASE(self->lastOfferImpressionTimes);
    RELEASE(self->offerCache);
    RELEASE(self->consumedRewards);
    RELEASE(self->rewardsRequest);
    RELEASE(self->offerViewController);
    RELEASE(self->shareViewController);
}

- (BOOL)isEligible:(TSOffer *)offer
{
    NSDate *now = [NSDate date];
    
    // Cannot show offers to users younger than minimumAge
    NSTimeInterval age = [now timeIntervalSinceDate:self.installDate];
    if(age < offer.minimumAge) {
        return NO;
    }
    
    // Cannot show offers more frequently than the rateLimit
    NSDate *lastImpression = [self.lastOfferImpressionTimes objectForKey:[[NSNumber numberWithInteger:offer.ident] stringValue]];
    if(lastImpression && [now timeIntervalSinceDate:lastImpression] < offer.rateLimit) {
        return NO;
    }
    
    return YES;
}

- (void)offerForInsertionPoint:(NSString *)insertionPoint result:(void (^)(TSOffer *))callback
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^(void) {
        if(!insertionPoint) {
            if(callback) {
                dispatch_sync(dispatch_get_main_queue(), ^() {
                    callback(nil);
                });
            }
            return;
        }
        
        TSOffer *offer;
        @synchronized(self.offerCache) {
            offer = [self.offerCache objectForKey:insertionPoint];
        }
        if(offer) {
            if(callback) {
                dispatch_sync(dispatch_get_main_queue(), ^() {
                    callback([self isEligible:offer] ? offer : nil);
                });
            }
            return;
        }
        
        NSString *url = [NSString stringWithFormat:kTSWordOfMouthOffersEndPoint, self.secret, self.bundle, [TSUtils encodeString:insertionPoint]];
        NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
        NSHTTPURLResponse *response;
        NSError *error;
        NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
        NSInteger status = response ? ((NSHTTPURLResponse *)response).statusCode : -1;
        
        NSLog(@"Offers request complete (status %d)", (int)status);
        
        if(data && status >= 200 && status < 300) {
            NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
            if(json) {
                offer = AUTORELEASE([[TSOffer alloc] initWithDescription:json]);
                @synchronized(self.offerCache) {
                    [self.offerCache setObject:offer forKey:insertionPoint];
                }
                if(callback) {
                    dispatch_sync(dispatch_get_main_queue(), ^() {
                        callback([self isEligible:offer] ? offer : nil);
                    });
                }
                return;
            }
        }
        
        if(callback) {
            dispatch_sync(dispatch_get_main_queue(), ^() {
                callback(nil);
            });
        }
        
    });
}

- (void)showOffer:(TSOffer *)offer parentViewController:(UIViewController *)parentViewController;
{
    if(offer && parentViewController) {
        self.offerViewController = [TSOfferViewController controllerWithOffer:offer parentViewController:parentViewController delegate:self];
        self.offerViewController.view.frame = parentViewController.view.bounds;
        [UIView transitionWithView:parentViewController.view
                          duration:0.3
                           options:UIViewAnimationOptionTransitionCrossDissolve
                        animations:^{ [parentViewController.view addSubview:self.offerViewController.view]; }
                        completion:NULL];

        [self.delegate showedOffer:offer.ident];
        
        // Update last shown time for this offer
        [self.lastOfferImpressionTimes setObject:[NSDate date] forKey:[[NSNumber numberWithInteger:offer.ident] stringValue]];
        [[NSUserDefaults standardUserDefaults] setObject:self.lastOfferImpressionTimes forKey:kTSLastOfferImpressionTimesKey];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

- (NSArray *)availableRewards
{
    NSHTTPURLResponse *response;
    NSError *error;
    NSData *data = [NSURLConnection sendSynchronousRequest:self.rewardsRequest returningResponse:&response error:&error];
    
    NSArray *results = [NSArray array];
    if(response && response.statusCode >= 200 && response.statusCode < 300 && data) {
        results = [TSWordOfMouthController parseRewards:data];
        
        // Filter out any rewards that have already been consumed
        @synchronized(self.consumedRewards) {
            results = [results filteredArrayUsingPredicate:[NSPredicate predicateWithBlock:^BOOL(id obj, NSDictionary *bindings) {
                return ![self.consumedRewards containsObject:[[NSNumber numberWithInteger:((TSReward *)obj).ident] stringValue]];
            }]];
        }
    }
    return results;
}

- (void)consumeReward:(TSReward *)reward
{
    if(reward) {
        @synchronized(self.consumedRewards) {
            [self.consumedRewards addObject:[[NSNumber numberWithInteger:reward.ident] stringValue]];
            [[NSUserDefaults standardUserDefaults] setObject:[self.consumedRewards allObjects] forKey:kTSConsumedRewardsKey];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }
    }
}

+ (NSArray *)parseRewards:(NSData *)rewardsJson
{
    NSArray *json = [NSJSONSerialization JSONObjectWithData:rewardsJson options:0 error:nil];
    if(json) {
        NSMutableArray *results = [NSMutableArray arrayWithCapacity:32];
        [json enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
            [results addObject:AUTORELEASE([[TSReward alloc] initWithDescription:(NSDictionary *)obj])];
        }];
        return results;
    }
    return nil;
}


// TSWordOfMouthDelegate
- (void)showedOffer:(NSUInteger)offerId
{
    [self.delegate showedOffer:offerId];
}

- (void)dismissedOffer:(BOOL)accepted
{
    if(accepted) {
        TSOffer *offer = self.offerViewController.offer;
        UIViewController *parent = self.offerViewController.parentViewController;
        
        self.shareViewController = [TSShareViewController controllerWithOffer:offer parentViewController:parent delegate:self];
        self.shareViewController.view.frame = parent.view.bounds;
        [UIView transitionWithView:parent.view
                          duration:0.3
                           options:UIViewAnimationOptionTransitionCrossDissolve
                        animations:^{ [parent.view addSubview:self.shareViewController.view]; }
                        completion:NULL];
    }
    self.offerViewController = nil;
    [self.delegate dismissedOffer:accepted];
}

- (void)showedSharing:(NSUInteger)offerId
{
    [self.delegate showedSharing:offerId];
}

- (void)dismissedSharing
{
    self.shareViewController = nil;
    [self.delegate dismissedSharing];
}

- (void)completedShare:(NSUInteger)offerId socialMedium:(NSString *)medium
{
    [self.delegate completedShare:offerId socialMedium:medium];
}


@end
