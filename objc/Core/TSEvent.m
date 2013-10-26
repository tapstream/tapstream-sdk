#import "TSEvent.h"
#import <sys/time.h>
#import <stdio.h>
#import <stdlib.h>
#import "TSLogging.h"
#import "TSUtils.h"

@interface TSEvent()

- (id)initWithName:(NSString *)name oneTimeOnly:(BOOL)oneTimeOnly;
- (void)addValue:(NSString *)value forKey:(NSString *)key withPrefix:(NSString *)prefix;
- (void)firing;
- (NSString *)makeUid;

@end



@implementation TSEvent

@synthesize uid, name, encodedName, oneTimeOnly, postData;

+ (id)eventWithName:(NSString *)eventName oneTimeOnly:(BOOL)oneTimeOnlyArg
{
	return AUTORELEASE([[self alloc] initWithName:eventName oneTimeOnly:oneTimeOnlyArg]);
}

+ (id)iapEventWithName:(NSString *)name
	transactionId:(NSString *)transactionId
	productId:(NSString *)productId
	quantity:(int)quantity
	priceInCents:(int)priceInCents
	currency:(NSString *)currencyCode
{
	TSEvent *e = AUTORELEASE([[self alloc] initWithName:name oneTimeOnly:NO]);
	[e addValue:transactionId forKey:@"purchase-transaction-id" withPrefix:@""];
	[e addValue:productId forKey:@"purchase-product-id" withPrefix:@""];
	[e addValue:[TSUtils stringifyInteger:quantity] forKey:@"purchase-quantity" withPrefix:@""];
	[e addValue:[TSUtils stringifyInteger:priceInCents] forKey:@"purchase-price" withPrefix:@""];
	[e addValue:currencyCode forKey:@"purchase-currency" withPrefix:@""];
	return e;
}

- (id)initWithName:(NSString *)eventName oneTimeOnly:(BOOL)oneTimeOnlyArg
{
	if((self = [super init]) != nil)
	{
		firstFiredTime = 0;
		uid = [self makeUid];
		name = [[eventName lowercaseString] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
		encodedName = [TSUtils encodeString:name];
		oneTimeOnly = oneTimeOnlyArg;
	}
	return self;
}

- (void)addValue:(id)value forKey:(NSString *)key
{
	[self addValue:value forKey:key withPrefix:@"custom-"];
}

- (void)addIntegerValue:(int)value forKey:(NSString *)key
{
	[self addValue:[TSUtils stringifyInteger:value] forKey:key];
}

- (void)addUnsignedIntegerValue:(uint)value forKey:(NSString *)key
{
	[self addValue:[TSUtils stringifyUnsignedInteger:value] forKey:key];
}

- (void)addDoubleValue:(double)value forKey:(NSString *)key
{
	[self addValue:[TSUtils stringifyDouble:value] forKey:key];
}

- (void)addFloatValue:(double)value forKey:(NSString *)key
{
	[self addValue:[TSUtils stringifyFloat:value] forKey:key];
}

- (void)addBooleanValue:(BOOL)value forKey:(NSString *)key
{
	[self addValue:[TSUtils stringifyBOOL:value] forKey:key];
}

- (NSString *)postData
{
	NSString *data = postData != nil ? (NSString *)postData : @"";
	return [[NSString stringWithFormat:@"&created-ms=%u", (unsigned int)(firstFiredTime*1000)] stringByAppendingString:data];
}

- (void)firing
{
	// Only record the time of the first fire attempt
	if(firstFiredTime == 0)
	{
		firstFiredTime = [[NSDate date] timeIntervalSince1970];
	}
}

- (NSString *)makeUid
{
	NSTimeInterval t = [[NSDate date] timeIntervalSince1970];
	return [NSString stringWithFormat:@"%u:%f", (unsigned int)(t*1000), arc4random() / (float)0x10000000];
}

- (void)addValue:(id)value forKey:(NSString *)key withPrefix:(NSString *)prefix
{
	NSString *encodedPair = [TSUtils encodeEventPairWithPrefix:prefix key:key value:value];
	if(encodedPair == nil)
	{
		return;
	}

	if(postData == nil)
	{
		postData = RETAIN([NSMutableString stringWithCapacity:64]);
	}
	[postData appendString:@"&"];
	[postData appendString:encodedPair];
}

- (void)dealloc
{
	RELEASE(uid);
	RELEASE(name);
	RELEASE(encodedName);
	RELEASE(postData);
	SUPER_DEALLOC;
}

@end
