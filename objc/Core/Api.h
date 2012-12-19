#pragma once
#import "Event.h"
#import "Hit.h"
#import "Response.h"

@protocol Api<NSObject>
- (void)fireEvent:(Event *)event;
- (void)fireHit:(Hit *)hit completion:(void(^)(Response *))completion;
@end