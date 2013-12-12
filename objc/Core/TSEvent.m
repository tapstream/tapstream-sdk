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
- (void)setName:(NSString *)name;
- (void)setTransactionNameWithAppName:(NSString *)appName platform:(NSString *)platformName;

@property(nonatomic, STRONG_OR_RETAIN) NSString *productId;

@end



@implementation TSEvent

@synthesize uid, name, encodedName, oneTimeOnly, postData, productId, isTransaction;

+ (id)eventWithName:(NSString *)eventName oneTimeOnly:(BOOL)oneTimeOnlyArg
{
	return AUTORELEASE([[self alloc] initWithName:eventName oneTimeOnly:oneTimeOnlyArg]);
}

+ (id)eventWithTransactionId:(NSString *)transactionId
	productId:(NSString *)productId
	quantity:(int)quantity
{
	return AUTORELEASE([[self alloc] initWithTransactionId:transactionId productId:productId quantity:quantity]);
}

+ (id)eventWithTransactionId:(NSString *)transactionId
	productId:(NSString *)productId
	quantity:(int)quantity
	priceInCents:(int)priceInCents
	currency:(NSString *)currencyCode
{
	return AUTORELEASE([[self alloc] initWithTransactionId:transactionId productId:productId quantity:quantity priceInCents:priceInCents currency:currencyCode]);
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
		isTransaction = NO;
	}
	return self;
}

- (id)initWithTransactionId:(NSString *)transactionId
	productId:(NSString *)productIdVal
	quantity:(int)quantity
{
	if((self = [super init]) != nil)
	{
		firstFiredTime = 0;
		uid = [self makeUid];
		oneTimeOnly = NO;
		isTransaction = YES;
		productId = productIdVal;

		[self addValue:transactionId forKey:@"purchase-transaction-id" withPrefix:@""];
		[self addValue:productId forKey:@"purchase-product-id" withPrefix:@""];
		[self addValue:[TSUtils stringifyInteger:quantity] forKey:@"purchase-quantity" withPrefix:@""];
	}
	return self;
}

- (id)initWithTransactionId:(NSString *)transactionId
	productId:(NSString *)productIdVal
	quantity:(int)quantity
	priceInCents:(int)priceInCents
	currency:(NSString *)currencyCode
{
	if((self = [super init]) != nil)
	{
		firstFiredTime = 0;
		uid = [self makeUid];
		oneTimeOnly = NO;
		isTransaction = YES;
		productId = productIdVal;

		[self addValue:transactionId forKey:@"purchase-transaction-id" withPrefix:@""];
		[self addValue:productId forKey:@"purchase-product-id" withPrefix:@""];
		[self addValue:[TSUtils stringifyInteger:quantity] forKey:@"purchase-quantity" withPrefix:@""];
		[self addValue:[TSUtils stringifyInteger:priceInCents] forKey:@"purchase-price" withPrefix:@""];
		[self addValue:currencyCode forKey:@"purchase-currency" withPrefix:@""];
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
	return [[NSString stringWithFormat:@"&created-ms=%.0f", firstFiredTime*1000] stringByAppendingString:data];
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

- (void)setName:(NSString *)eventName
{
	name = [[eventName lowercaseString] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
	encodedName = [TSUtils encodeString:name];
}

- (void)setTransactionNameWithAppName:(NSString *)appName platform:(NSString *)platformName
{
	[self setName:[NSString stringWithFormat:@"%@-%@-purchase-%@", platformName, [appName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]], productId]];
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
	RELEASE(productId);
	SUPER_DEALLOC;
}

@end
