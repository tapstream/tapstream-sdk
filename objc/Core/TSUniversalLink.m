#import <Foundation/Foundation.h>
#import "TSUniversalLink.h"

@interface TSUniversalLink()
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSURL* deeplinkURL;
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSURL* fallbackURL;
@property(nonatomic, readwrite) TSUniversalLinkStatus status;
@end

@implementation TSUniversalLink



+ (instancetype)universalLinkWithDeeplinkQueryResponse:(TSResponse*)response;
{
	TSUniversalLinkStatus status = kTSULUnknown;

	if (response.status != 200 || response.data == nil){
		return [self universalLinkWithStatus:kTSULUnknown];
	}

	NSData *jsonData = response.data;
	NSError* error = nil;
	NSDictionary *jsonDict  = [NSJSONSerialization JSONObjectWithData:jsonData
															  options:kNilOptions
																error:&error];

	if (error != nil){
		return [self universalLinkWithStatus:kTSULUnknown];
	}

	id regUrlStr = [jsonDict objectForKey:@"registered_url"];
	id fbUrlStr = [jsonDict objectForKey:@"fallback_url"];
	id enabledOrNull = [jsonDict objectForKey:@"enable_universal_links"];
	BOOL eul = false;

	if (enabledOrNull != [NSNull null] && enabledOrNull != nil){
		eul = [enabledOrNull boolValue];
	}

	NSURL *regUrl, *fbUrl;

	if (fbUrlStr != [NSNull null] && fbUrlStr != nil){
		fbUrl = [NSURL URLWithString:fbUrlStr];
		status = kTSULValid;
	}else{
		fbUrl = nil;
	}

	if (regUrlStr != [NSNull null] && regUrlStr != nil){
		regUrl = [NSURL URLWithString:regUrlStr];
		status = kTSULValid;
	}else{
		regUrl = nil;
	}

	if (status == kTSULValid && !eul){
		status = kTSULDisabled;
	}

	return [[self alloc] initWithDeeplinkURL:regUrl
								 fallbackURL:fbUrl
									  status:status];
}

+ (instancetype)universalLinkWithStatus:(TSUniversalLinkStatus)status
{
	return [[self alloc] initWithStatus:status];
}

- (id)initWithStatus:(TSUniversalLinkStatus)status
{
	if([self init] != nil){
		self.deeplinkURL = nil;
		self.fallbackURL = nil;
		self.status = status;
	}
	return self;
}

- (id)initWithDeeplinkURL:(NSURL*)deeplinkURL fallbackURL:(NSURL*)fallbackURL status:(TSUniversalLinkStatus)status
{
	if([self init] != nil){
		self.deeplinkURL = deeplinkURL;
		self.fallbackURL = fallbackURL;
		self.status = status;
	}
	return self;
}
@end