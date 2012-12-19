#import "CoreListenerImpl.h"

@implementation CoreListenerImpl

@synthesize queue;

- (id)initWithQueue:(OperationQueue *)queueVal
{
	if((self = [super init]) != nil)
	{
		self.queue = queueVal;
	}
	return self;
}

- (void)reportOperation:(NSString *)op
{
	[queue put:AUTORELEASE([[Operation alloc] initWithName:op arg:nil])];
}

- (void)reportOperation:(NSString *)op arg:(NSString *)arg
{
	[queue put:AUTORELEASE([[Operation alloc] initWithName:op arg:arg])];
}

- (void)dealloc
{
	RELEASE(queue);
	SUPER_DEALLOC;
}

@end