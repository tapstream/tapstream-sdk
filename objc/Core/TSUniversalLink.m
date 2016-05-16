#import <Foundation/Foundation.h>
#import "TSUniversalLink.h"
#import "TSError.h"

@interface TSUniversalLink()
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSURL* deeplinkURL;
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSURL* fallbackURL;
@property(nonatomic, readwrite) TSUniversalLinkStatus status;
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSError* error;
@end

@implementation TSUniversalLink



+ (instancetype)universalLinkWithDeeplinkQueryResponse:(TSResponse*)response;
{
	TSUniversalLinkStatus status = kTSULUnknown;

	if (response.status != 200 || response.data == nil){
		NSError* error = [TSError errorWithCode:kTSInvalidResponse
										message:[NSString stringWithFormat:@"Invalid response (status code %d)", response.status]];
		return [self universalLinkWithError:error];
	}

	NSData *jsonData = response.data;
	NSError* error = nil;
	NSDictionary *jsonDict  = [NSJSONSerialization JSONObjectWithData:jsonData
															  options:kNilOptions
																error:&error];

	if (error != nil){
		NSError* tsError = [TSError errorWithCode:kTSInvalidResponse
										message:@"Invalid JSON response"
										   info:[NSDictionary dictionaryWithObjectsAndKeys:error, @"cause", nil]];
		return [self universalLinkWithError:tsError];
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

+ (instancetype)universalLinkWithError:(NSError*)error
{
	return [[self alloc] initWithError:error];
}

- (id)initWithStatus:(TSUniversalLinkStatus)status
{
	if([self init] != nil){
		self.deeplinkURL = nil;
		self.fallbackURL = nil;
		self.status = status;
		self.error = nil;
	}
	return self;
}

- (id)initWithError:(NSError*)error
{
	if([self init] != nil){
		self.deeplinkURL = nil;
		self.fallbackURL = nil;
		self.status = kTSULUnknown;
		self.error = error;
	}
	return self;
}

- (id)initWithDeeplinkURL:(NSURL*)deeplinkURL fallbackURL:(NSURL*)fallbackURL status:(TSUniversalLinkStatus)status
{
	if([self init] != nil){
		self.deeplinkURL = deeplinkURL;
		self.fallbackURL = fallbackURL;
		self.status = status;
		self.error = nil;
	}
	return self;
}
@end
