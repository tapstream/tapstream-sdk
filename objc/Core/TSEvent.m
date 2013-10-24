#import "TSEvent.h"
#import <sys/time.h>
#import <stdio.h>
#import <stdlib.h>
#import "TSLogging.h"

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
	[e addValue:[NSString stringWithFormat:@"%d", quantity] forKey:@"purchase-quantity" withPrefix:@""];
	[e addValue:[NSString stringWithFormat:@"%d", priceInCents] forKey:@"purchase-price" withPrefix:@""];
	[e addValue:currencyCode forKey:@"purchase-currency" withPrefix:@""];
	return e;
}

- (id)initWithName:(NSString *)eventName oneTimeOnly:(BOOL)oneTimeOnlyArg
{
	if((self = [super init]) != nil)
	{
		firstFiredTime = 0;
		uid = RETAIN([self makeUid]);
		name = RETAIN([[eventName lowercaseString] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]);
		encodedName = RETAIN([self encodeString:name]);
		oneTimeOnly = oneTimeOnlyArg;
	}
	return self;
}

- (void)addValue:(NSString *)value forKey:(NSString *)key
{
	[self addValue:value forKey:key withPrefix:@"custom-"];
}

- (void)addIntegerValue:(int)value forKey:(NSString *)key
{
	[self addValue:[NSString stringWithFormat:@"%d", value] forKey:key];
}

- (void)addUnsignedIntegerValue:(uint)value forKey:(NSString *)key
{
	[self addValue:[NSString stringWithFormat:@"%u", value] forKey:key];
}

- (void)addDoubleValue:(double)value forKey:(NSString *)key
{
	[self addValue:[NSString stringWithFormat:@"%g", value] forKey:key];
}

- (void)addBooleanValue:(BOOL)value forKey:(NSString *)key
{
	[self addValue:(value ? @"true" : @"false") forKey:key];
}

- (NSString *)postData
{
	NSString *data = postData != nil ? (NSString *)postData : @"";
	return [[NSString stringWithFormat:@"&created-ms=%u", (unsigned int)(firstFiredTime*1000)] stringByAppendingString:data];
}

- (NSString *)encodeString:(NSString *)s
{
	return AUTORELEASE((BRIDGE_TRANSFER NSString *)CFURLCreateStringByAddingPercentEscapes(
		NULL, (CFStringRef)s, NULL, (CFStringRef)@"!*'();:@&=+$,/?%#[]", kCFStringEncodingUTF8));
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

- (void)addValue:(NSString *)value forKey:(NSString *)key withPrefix:(NSString *)prefix
{
	if(value == nil)
	{
		return;
	}

	if(key.length > 255)
	{
		[TSLogging logAtLevel:kTSLoggingWarn format:@"Tapstream Warning: Custom key exceeds 255 characters, this field will not be included in the post (key=%@)", key];
		return;
	}
	NSString *encodedKey = [self encodeString:[prefix stringByAppendingString:key]];

	NSString *encodedValue = [self encodeString:value];
	if(encodedValue.length > 255)
	{
		[TSLogging logAtLevel:kTSLoggingWarn format:@"Tapstream Warning: Custom value exceeds 255 characters, this field will not be included in the post (value=%@)", value];
		return;
	}

	if(postData == nil)
	{
		postData = RETAIN([NSMutableString stringWithCapacity:64]);
	}
	[postData appendString:@"&"];
	[postData appendString:encodedKey];
	[postData appendString:@"="];
	[postData appendString:encodedValue];
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
