#ifndef TSUniversalLink_h
#define TSUniversalLink_h

#import "TSHelpers.h"
#import "TSResponse.h"

typedef enum _TSUniversalLinkStatus
{
	kTSULValid = 0,
	kTSULDisabled,
	kTSULUnknown
} TSUniversalLinkStatus;

@interface TSUniversalLink : NSObject

@property(nonatomic, STRONG_OR_RETAIN, readonly) NSURL* deeplinkURL;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSURL* fallbackURL;
@property(nonatomic, readonly) TSUniversalLinkStatus status;

+ (instancetype)universalLinkWithDeeplinkQueryResponse:(TSResponse*)response;
+ (instancetype)universalLinkWithStatus:(TSUniversalLinkStatus)status;
@end


#endif /* TSUniversalLink_h */
