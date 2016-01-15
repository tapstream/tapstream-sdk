#ifndef TSLander_h
#define TSLander_h

@interface TSLander : NSObject
@property(assign, nonatomic, readonly) NSUInteger ident;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSString *html;
@property(STRONG_OR_RETAIN, nonatomic, readonly) NSURL *url;
- (id)initWithDescription:(NSDictionary *)descriptionVal;
- (BOOL)isValid;
@end

#endif /* TSLander_h */
