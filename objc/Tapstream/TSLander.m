#import <Foundation/Foundation.h>
#import "TSHelpers.h"
#import "TSLander.h"

@interface TSLander()
@property(assign, nonatomic, readwrite) NSUInteger ident;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *html;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSURL *url;
@end

@implementation TSLander

- (id)initWithDescription:(NSDictionary *)descriptionVal
{
	self.ident = -1;
	self.html = nil;
	self.url = nil;

	if(self = [super init]) {
		id ident = [descriptionVal objectForKey:@"id"];
		if(ident != nil && ident != (id)[NSNull null]){
			self.ident = [ident unsignedIntegerValue];
		}

		NSString* html = [descriptionVal objectForKey:@"markup"];
		if(html != nil && html != (id)[NSNull null]){
			self.html = html;
		}

		NSString* urlString = [descriptionVal objectForKey:@"url"];
		if(urlString != nil && urlString != (id)[NSNull null]){
			self.url = [NSURL URLWithString:urlString];
		}
	}
	return self;
}
- (BOOL)isValid
{
	if(self.ident <= 0)
	{
		return false;
	}

	// Markup type: any HTML is ok
	if(self.url == nil && self.html != nil)
	{
		return true;
	}

	// Url type: make sure url is present and scheme is http or https
	if(self.url == nil)
	{
		return false;
	}

	if([[self.url scheme] isEqualToString:@"http"])
	{
		return true;
	}

	if([[self.url scheme] isEqualToString:@"https"])
	{
		return true;
	}
	
	return false;
}
@end