#import "Response.h"
#import "helpers.h"

@implementation Response

@synthesize status = status;
@synthesize message = message;

- initWithStatus:(int)statusVal message:(NSString *)messageVal
{
	if((self = [super init]) != nil)
	{
		status = statusVal;
		message = RETAIN(messageVal);
	}
	return self;
}

- (void)dealloc
{
	RELEASE(message);
	SUPER_DEALLOC;
}

@end