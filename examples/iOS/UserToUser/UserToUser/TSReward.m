//
//  TSReward.m
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSReward.h"
#import "TSHelpers.h"

@interface TSReward()

@property(strong, nonatomic, readwrite) NSDictionary *description;
@property(assign, nonatomic, readwrite) NSUInteger ident;
@property(strong, nonatomic, readwrite) NSString *name;
@property(assign, nonatomic, readwrite) NSInteger installs;

@end

@implementation TSReward

@synthesize description, ident, name, installs;


- (id)initWithDescription:(NSDictionary *)descriptionVal
{
    if(self = [super init]) {
        self.description = descriptionVal;
        self.ident = [[descriptionVal objectForKey:@"id"] unsignedIntegerValue];
        self.name = [descriptionVal objectForKey:@"name"];
        self.installs = [[descriptionVal objectForKey:@"installs"] integerValue];
    }
    return self;
}

- (void)dealloc
{
    RELEASE(self->description);
    RELEASE(self->name);
}

@end