#pragma once
#import <Foundation/Foundation.h>
#import "Api.h"
#import "Delegate.h"
#import "Platform.h"
#import "CoreListener.h"
#import "Core.h"
#import "Event.h"
#import "OperationQueue.h"
#import "Response.h"

@interface ConversionTracker : NSObject<Api> {
@private
	id<Delegate> del;
	id<Platform> platform;
	id<CoreListener> listener;
	Core *core;
}

- (id)initWithOperationQueue:(OperationQueue *)q accountName:(NSString *)accountName developerSecret:(NSString *)developerSecret hardware:(NSString *)hardware;
- (void)fireEvent:(Event *)event;
- (void)fireHit:(Hit *)hit completion:(void(^)(Response *))completion;
- (void)setResponseStatus:(int)status;
- (NSArray *)getSavedFiredList;
- (int)getDelay;
- (NSString *)getPostData;

@end
