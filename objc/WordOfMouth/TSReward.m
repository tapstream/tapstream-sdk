//
//  TSReward.m
//  WordOfMouth
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSReward.h"

@interface TSReward()

@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSDictionary *description;
@property(assign, nonatomic, readwrite) NSUInteger ident;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *insertionPoint;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *sku;
@property(assign, nonatomic, readwrite) NSInteger installs;

@end

@implementation TSReward

@synthesize description, ident, insertionPoint, sku, installs;


- (id)initWithDescription:(NSDictionary *)descriptionVal
{
    if(self = [super init]) {
        self.description = descriptionVal;
        self.ident = [[descriptionVal objectForKey:@"id"] unsignedIntegerValue];
        self.insertionPoint = [descriptionVal objectForKey:@"insertion_point"];
        self.sku = [descriptionVal objectForKey:@"sku"];
        self.installs = [[descriptionVal objectForKey:@"installs"] integerValue];
    }
    return self;
}

- (void)dealloc
{
    SUPER_DEALLOC;
    
    RELEASE(self->description);
    RELEASE(self->insertionPoint);
    RELEASE(self->sku);
}

@end