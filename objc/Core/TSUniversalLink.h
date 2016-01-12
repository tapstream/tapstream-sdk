#ifndef TSUniversalLink_h
#define TSUniversalLink_h

#import "TSHelpers.h"
#import "TSResponse.h"

typedef enum _TSUniversalLinkStatus
{
	kTSULHandled = 0,
	kTSULDisabled,
	kTSULUnknown
} TSUniversalLinkStatus;

@interface TSUniversalLink : NSObject

@property(nonatomic, STRONG_OR_RETAIN, readonly) NSURL* deeplinkUrl;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSURL* fallbackUrl;
@property(nonatomic, readonly) TSUniversalLinkStatus status;

+ (instancetype)universalLinkWithDeeplinkQueryResponse:(TSResponse*)response;
+ (instancetype)universalLinkWithStatus:(TSUniversalLinkStatus)status;
@end


#endif /* TSUniversalLink_h */
