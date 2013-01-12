#pragma once
#import <Foundation/Foundation.h>
#import "TSApi.h"
#import "TSDelegate.h"
#import "TSPlatform.h"
#import "TSCoreListener.h"
#import "TSCore.h"
#import "TSEvent.h"
#import "TSOperationQueue.h"
#import "TSResponse.h"

@interface TSTapstream : NSObject<TSApi> {
@private
	id<TSDelegate> del;
	id<TSPlatform> platform;
	id<TSCoreListener> listener;
	TSCore *core;
}

- (id)initWithOperationQueue:(TSOperationQueue *)q accountName:(NSString *)accountName developerSecret:(NSString *)developerSecret hardware:(NSString *)hardware;
- (void)fireEvent:(TSEvent *)event;
- (void)fireHit:(TSHit *)hit completion:(void(^)(TSResponse *))completion;
- (void)setResponseStatus:(int)status;
- (NSArray *)getSavedFiredList;
- (int)getDelay;
- (NSString *)getPostData;

@end
