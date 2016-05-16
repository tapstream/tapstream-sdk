#ifndef TSError_h
#define TSError_h

#import "TSHelpers.h"

#define kTSErrorDomain @"Tapstream"

typedef enum _TSErrorCode {
	kTSIOError = 0,
	kTSInvalidResponse
} TSErrorCode;



@interface TSError : NSObject
+(NSError*)errorWithCode:(TSErrorCode)code message:(NSString*)message;
+(NSError*)errorWithCode:(TSErrorCode)code message:(NSString*)message info:(NSDictionary*)userInfo;
@end


#endif /* TSError_h */
