//
//  TSOffer.m
//  WordOfMouth
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSOffer.h"

@interface TSOffer()

@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSDictionary *description;
@property(assign, nonatomic, readwrite) NSUInteger ident;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *insertionPoint;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *message;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *url;
@property(assign, nonatomic, readwrite) NSInteger rewardMinimumInstalls;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *rewardSku;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *bundle;
@property(assign, nonatomic, readwrite) NSInteger minimumAge;
@property(assign, nonatomic, readwrite) NSInteger rateLimit;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *markup;

@end



@implementation TSOffer

@synthesize description, ident, insertionPoint, message, url, rewardMinimumInstalls, rewardSku, bundle, minimumAge, rateLimit, markup;

- (id)initWithDescription:(NSDictionary *)descriptionVal uuid:(NSString *)uuid
{
    if(self = [super init]) {
        self.description = descriptionVal;
        self.ident = [[descriptionVal objectForKey:@"id"] unsignedIntegerValue];
        self.insertionPoint = [descriptionVal objectForKey:@"insertion_point"];
        self.message = [descriptionVal objectForKey:@"message"];
        self.url = [descriptionVal objectForKey:@"offer_url"];
        if(self.url) {
            self.url = [self.url stringByReplacingOccurrencesOfString:@"{event_session}" withString:uuid];
        }
        self.rewardMinimumInstalls = [[descriptionVal objectForKey:@"reward_minimum_installs"] integerValue];
        self.rewardSku = [descriptionVal objectForKey:@"reward_sku"];
        self.bundle = [descriptionVal objectForKey:@"bundle"];
        self.minimumAge = [[descriptionVal objectForKey:@"minimum_age"] integerValue];
        self.rateLimit = [[descriptionVal objectForKey:@"rate_limit"] integerValue];
        self.markup = [descriptionVal objectForKey:@"markup"];
    }
    return self;
}

- (void)dealloc
{
    SUPER_DEALLOC;
    
    RELEASE(self->description);
    RELEASE(self->insertionPoint);
    RELEASE(self->message);
    RELEASE(self->url);
    RELEASE(self->rewardSku);
    RELEASE(self->bundle);
    RELEASE(self->markup);
}

@end
