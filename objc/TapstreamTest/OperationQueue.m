#import "OperationQueue.h"
#import <assert.h>


@implementation Operation

@synthesize name;
@synthesize arg;

- (id)initWithName:(NSString *)nameVal arg:(NSString *)argVal
{
	if((self = [super init]) != nil)
	{
		name = RETAIN(nameVal);
		arg = RETAIN(argVal);
	}
	return self;
}

- (void)dealloc
{
	RELEASE(name);
	RELEASE(arg);
	SUPER_DEALLOC;
}

@end


@interface OperationQueue()

@property(nonatomic, STRONG_OR_RETAIN) NSMutableArray *queue;
@property(nonatomic, STRONG_OR_RETAIN) NSConditionLock *queueLock;

@end

@implementation OperationQueue : NSObject

@synthesize queue, queueLock;

- (id)init
{
	if((self = [super init]) != nil)
	{
		queue = [[NSMutableArray alloc] initWithCapacity:32];
		queueLock = [[NSConditionLock alloc] initWithCondition:0];
	}
	return self;
}

- (void)put:(Operation *)op
{
	[queueLock lock];
	[queue addObject:op];
	[queueLock unlockWithCondition:1];
}

- (Operation *)take
{
	[queueLock lockWhenCondition:1];
	Operation *op = [queue objectAtIndex:0];
	[queue removeObjectAtIndex:0];
	int more = [queue count] > 0 ? 1 : 0;
	[queueLock unlockWithCondition:more];
	return op;
}

- (void)expect:(NSString *)opName
{
	Operation *op = [self take];
	if(![op.name isEqualToString:opName])
	{
		NSAssert(false, @"Expected '%@' but got '%@'", opName, op.name);
	}
}

- (void)dealloc
{
	RELEASE(queue);
	RELEASE(queueLock);
	SUPER_DEALLOC;
}

@end

