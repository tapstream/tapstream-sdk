//
//  TSUniversalLink.h
//  ExampleApp
//
//  Created by Adam Bard on 2015-12-17.
//  Copyright © 2015 Example. All rights reserved.
//

#ifndef TSUniversalLink_h
#define TSUniversalLink_h

#import "TSHelpers.h"

@interface TSDeepLink : NSObject

@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *scheme;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSArray *pathComponents;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSDictionary *parameters;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSURL *deepLinkUrl;

+ (instancetype)deepLinkWithURL:(NSURL*)url;
+ (instancetype)deepLinkWithString:(NSString*)urlString;
@end


#endif /* TSUniversalLink_h */
