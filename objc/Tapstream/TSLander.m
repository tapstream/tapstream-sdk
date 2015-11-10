//
//  TSLander.m
//  WordOfMouth
//
//  Created by Adam Bard on 2015-11-04.
//  Copyright © 2015 Tapstream. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TSHelpers.h"
#import "TSLander.h"

@interface TSLander()
@property(assign, nonatomic, readwrite) NSUInteger ident;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSString *html;
@property(STRONG_OR_RETAIN, nonatomic, readwrite) NSURL *url;
@end

@implementation TSLander
@synthesize html, ident, url;

- (id)initWithDescription:(NSDictionary *)descriptionVal
{
	if(self = [super init]) {
		self.ident = [[descriptionVal objectForKey:@"id"] unsignedIntegerValue];
		self.html = [descriptionVal objectForKey:@"markup"];
		NSString* urlString = [descriptionVal objectForKey:@"url"];
		if(urlString != nil){
			self.url = [NSURL URLWithString:urlString];
		}else{
			self.url = nil;
		}
	}
	return self;
}
@end