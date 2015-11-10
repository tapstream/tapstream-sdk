//
//  TSLander.h
//  WordOfMouth
//
//  Created by Adam Bard on 2015-11-04.
//  Copyright © 2015 Tapstream. All rights reserved.
//

#ifndef TSLander_h
#define TSLander_h

@interface TSLander : NSObject
@property(assign, nonatomic, readonly) NSUInteger ident;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSString *html;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSURL *url;
- (id)initWithDescription:(NSDictionary *)descriptionVal;
@end

#endif /* TSLander_h */
