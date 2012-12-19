#pragma once
#import <Foundation/Foundation.h>
#import "Event.h"
#import "Delegate.h"
#import "Platform.h"
#import "CoreListener.h"
#import "Hit.h"
#import "Response.h"

@interface Core : NSObject {
@private
	id<Delegate> del;
	id<Platform> platform;
	id<CoreListener> listener;
	NSString *accountName;
	NSMutableString *postData;
	NSMutableSet *firingEvents;
	NSMutableSet *firedEvents;
	NSString *failingEventId;
	int delay;
}

- initWithDelegate:(id<Delegate>)delegate platform:(id<Platform>)platform listener:(id<CoreListener>)listener accountName:(NSString *)accountName developerSecret:(NSString *)developerSecret hardware:(NSString *)hardware;
- (void)fireEvent:(Event *)event;
- (void)fireHit:(Hit *)hit completion:(void(^)(Response *))completion;
- (int)getDelay;
- (NSMutableString *)postData;

@end