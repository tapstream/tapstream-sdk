//
//  TSDeepLink.m
//  ExampleApp
//
//  Created by Adam Bard on 2015-12-22.
//  Copyright © 2015 Example. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TSUniversalLink.h"

@interface TSUniversalLink()
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSURL* deeplinkUrl;
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSURL* fallbackUrl;
@property(nonatomic, readwrite) TSUniversalLinkStatus status;
@end

@implementation TSUniversalLink



+ (instancetype)universalLinkWithDeeplinkQueryResponse:(TSResponse*)response;
{
	TSUniversalLinkStatus status = kTSULUnknown;

	NSData *jsonData = response.data;
	NSError* error = nil;
	NSDictionary *jsonDict  = [NSJSONSerialization JSONObjectWithData:jsonData
															  options:kNilOptions
																error:&error];


	if (error != nil || response.status != 200){
		return [self universalLinkWithStatus:kTSULUnknown];
	}

	id regUrlStr = [jsonDict objectForKey:@"registered_url"];
	id fbUrlStr = [jsonDict objectForKey:@"fallback_url"];
	BOOL eul = (BOOL) [jsonDict objectForKey:@"enable_universal_links"];

	NSURL *regUrl, *fbUrl;

	if (fbUrlStr != nil){
		fbUrl = [NSURL URLWithString:fbUrlStr];
		status = kTSULHandled;
	}else{
		fbUrl = nil;
	}

	if (regUrlStr != nil){
		regUrl = [NSURL URLWithString:regUrlStr];
		status = kTSULHandled;
	}else{
		regUrl = nil;
	}

	if (status == kTSULHandled && !eul){
		status = kTSULDisabled;
	}

	return [[self alloc] initWithDeeplinkUrl:regUrl
								 fallbackUrl:fbUrl
									  status:status];
}

+ (instancetype)universalLinkWithStatus:(TSUniversalLinkStatus)status
{
	return [[self alloc] initWithStatus:status];
}

- (id)initWithStatus:(TSUniversalLinkStatus)status
{
	if([self init] != nil){
		self.deeplinkUrl = nil;
		self.fallbackUrl = nil;
		self.status = status;
	}
	return self;
}

- (id)initWithDeeplinkUrl:(NSURL*)deeplinkUrl fallbackUrl:(NSURL*)fallbackUrl status:(TSUniversalLinkStatus)status
{
	if([self init] != nil){
		self.deeplinkUrl = deeplinkUrl;
		self.fallbackUrl = fallbackUrl;
		self.status = status;
	}
	return self;
}
@end