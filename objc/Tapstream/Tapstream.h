#pragma once
#import <Foundation/Foundation.h>
#import "Api.h"
#import "Delegate.h"
#import "Platform.h"
#import "CoreListener.h"
#import "Core.h"
#import "Event.h"
#import "Response.h"
#import "Logging.h"

@interface Tapstream : NSObject<Api> {
@private
	id<Delegate> del;
	id<Platform> platform;
	id<CoreListener> listener;
	Core *core;
}

+ (void)createWithAccountName:(NSString *)accountName developerSecret:(NSString *)developerSecret;
+ (void)createWithAccountName:(NSString *)accountName developerSecret:(NSString *)developerSecret hardware:(NSString *)hardware;
+ (id)instance;

- (void)fireEvent:(Event *)event;
- (void)fireHit:(Hit *)hit completion:(void(^)(Response *))completion;

@end
