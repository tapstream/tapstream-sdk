//
//  TSOffer.m
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSOffer.h"
#import "TSHelpers.h"

@interface TSOffer()

@property(strong, nonatomic, readwrite) NSDictionary *description;
@property(assign, nonatomic, readwrite) NSInteger ident;
@property(strong, nonatomic, readwrite) NSString *name;
@property(assign, nonatomic, readwrite) NSInteger rewardMinimumInstalls;
@property(strong, nonatomic, readwrite) NSString *rewardSku;
@property(strong, nonatomic, readwrite) NSString *bundle;
@property(assign, nonatomic, readwrite) NSInteger minimumAge;
@property(assign, nonatomic, readwrite) NSInteger rateLimit;

@end



@implementation TSOffer

@synthesize description, ident, name, rewardMinimumInstalls, rewardSku, bundle, minimumAge, rateLimit;

- (id)initWithDescription:(NSDictionary *)descriptionVal
{
    if(self = [super init]) {
        self.description = descriptionVal;
        self.ident = [[descriptionVal objectForKey:@"id"] integerValue];
        self.name = [descriptionVal objectForKey:@"name"];
        self.rewardMinimumInstalls = [[descriptionVal objectForKey:@"reward_minimum_installs"] integerValue];
        self.rewardSku = [descriptionVal objectForKey:@"reward_sku"];
        self.bundle = [descriptionVal objectForKey:@"bundle"];
        self.minimumAge = [[descriptionVal objectForKey:@"minimum_age"] integerValue];
        self.rateLimit = [[descriptionVal objectForKey:@"rate_limit"] integerValue];
    }
    return self;
}

- (void)dealloc
{
    RELEASE(self->description);
    RELEASE(self->name);
    RELEASE(self->rewardSku);
    RELEASE(self->bundle);
}

@end
