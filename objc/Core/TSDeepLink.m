//
//  TSDeepLink.m
//  ExampleApp
//
//  Created by Adam Bard on 2015-12-22.
//  Copyright © 2015 Example. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TSDeepLink.h"

@interface TSDeepLink()
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSString *scheme;
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSArray *pathComponents;
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSDictionary *parameters;
@property(nonatomic, STRONG_OR_RETAIN, readwrite) NSURL *deepLinkUrl;
@end

@implementation TSDeepLink

+ (instancetype)deepLinkWithURL:(NSURL*)url
{
	NSURLComponents* comps = [NSURLComponents
							  componentsWithURL:url
							  resolvingAgainstBaseURL:false];


	NSMutableArray* pathComponents = [NSMutableArray arrayWithObject:[url host]];
	[pathComponents addObjectsFromArray:[url pathComponents]];
	if([pathComponents count] > 1){
		// Remove leading "/" from url pathcomponents if present
		[pathComponents removeObjectAtIndex:1];
	}

	NSMutableDictionary* paramsDict = [NSMutableDictionary dictionary];
	NSArray* components = [comps queryItems];
	for (int ii=0; ii < [components count]; ii++){
		NSURLQueryItem* item = [components objectAtIndex:ii];
		[paramsDict setObject:[item value] forKey:[item name]];
	}

	return [[self alloc] initWithScheme:[url scheme]
						 pathComponents:[NSArray arrayWithArray:pathComponents]
							 parameters:[NSDictionary dictionaryWithDictionary:paramsDict]
									url:url];
}

+ (instancetype)deepLinkWithString:(NSString*)urlString
{
	return [self deepLinkWithURL:[NSURL URLWithString:urlString]];
}

- (id)initWithScheme:(NSString*)scheme pathComponents:(NSArray*)pathComponents parameters:(NSDictionary*)parameters url:(NSURL*)url
{
	if([self init] != nil){
		self.scheme = scheme;
		self.pathComponents = pathComponents;
		self.parameters = parameters;
		self.deepLinkUrl = url;
	}
	return self;
}
@end