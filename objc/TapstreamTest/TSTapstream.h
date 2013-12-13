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
#import "TSConfig.h"

@interface TSTapstream : NSObject<TSApi> {
@private
	id<TSDelegate> del;
	id<TSPlatform> platform;
	id<TSCoreListener> listener;
	TSCore *core;
	TSConfig *config;
}

@property(nonatomic, STRONG_OR_RETAIN) id<TSDelegate> del;
@property(nonatomic, STRONG_OR_RETAIN) id<TSPlatform> platform;
@property(nonatomic, STRONG_OR_RETAIN) id<TSCoreListener> listener;
@property(nonatomic, STRONG_OR_RETAIN) TSCore *core;
@property(nonatomic, STRONG_OR_RETAIN) TSConfig *config;


- (id)initWithOperationQueue:(TSOperationQueue *)q accountName:(NSString *)accountName developerSecret:(NSString *)developerSecret config:(TSConfig *)config;
- (void)fireEvent:(TSEvent *)event;
- (void)fireHit:(TSHit *)hit completion:(void(^)(TSResponse *))completion;
- (void)setResponseStatus:(int)status;
- (NSArray *)getSavedFiredList;
- (int)getDelay;
- (void)setDelay:(int)delay;
- (NSString *)getPostData;

@end
