#import "TSEvent.h"
#import <sys/time.h>
#import <stdio.h>
#import <stdlib.h>
#import "TSLogging.h"
#import "TSUtils.h"

@interface TSEvent()
@end


@implementation TSEvent

@synthesize uid, name, encodedName, productId, customFields, postData, isOneTimeOnly, isTransaction;

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

- (id)initWithName:(NSString *)eventName
	oneTimeOnly:(BOOL)oneTimeOnlyArg
{
	if((self = [super init]) != nil)
	{
		firstFiredTime = 0;
		uid = RETAIN([self makeUid]);
		name = RETAIN([[eventName lowercaseString] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]);
        encodedName = RETAIN([TSUtils encodeString:name]);
		isOneTimeOnly = oneTimeOnlyArg;
		isTransaction = NO;
        customFields = RETAIN([NSMutableDictionary dictionaryWithCapacity:16]);
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
		uid = RETAIN([self makeUid]);
		productId = RETAIN(productIdVal);
		isOneTimeOnly = NO;
		isTransaction = YES;
        customFields = RETAIN([NSMutableDictionary dictionaryWithCapacity:16]);

		[self addValue:transactionId forKey:@"purchase-transaction-id"];
		[self addValue:productId forKey:@"purchase-product-id"];
		[self addValue:[NSNumber numberWithInt:quantity] forKey:@"purchase-quantity"];
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
		uid = RETAIN([self makeUid]);
        productId = RETAIN(productIdVal);
		isOneTimeOnly = NO;
		isTransaction = YES;
        customFields = RETAIN([NSMutableDictionary dictionaryWithCapacity:16]);

		[self addValue:transactionId forKey:@"purchase-transaction-id"];
		[self addValue:productId forKey:@"purchase-product-id"];
		[self addValue:[NSNumber numberWithInt:quantity] forKey:@"purchase-quantity"];
		[self addValue:[NSNumber numberWithInt:priceInCents] forKey:@"purchase-price"];
		[self addValue:currencyCode forKey:@"purchase-currency"];
	}
	return self;
}

- (NSString *)makeUid
{
	NSTimeInterval t = [[NSDate date] timeIntervalSince1970];
	return [NSString stringWithFormat:@"%.0f:%f", t*1000, arc4random() / (float)0x10000000];
}

- (void)setTransactionNameWithAppName:(NSString *)appName platform:(NSString *)platformName
{
    NSString *eventName = [NSString stringWithFormat:@"%@-%@-purchase-%@", platformName, [appName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]], productId];
    RELEASE(name);
    name = RETAIN([[eventName lowercaseString] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]);
    RELEASE(encodedName);
    encodedName = RETAIN([TSUtils encodeString:name]);
}

- (void)addValue:(NSObject *)obj forKey:(NSString *)key
{
	[self.customFields setObject:obj forKey:key];
}

- (BOOL)prepared
{
    return firstFiredTime != 0;
}

- (void)prepare
{
    // Only record the time of the first fire attempt
    if(firstFiredTime == 0)
    {
        firstFiredTime = [[NSDate date] timeIntervalSince1970];
        
        postData = RETAIN([NSMutableString stringWithCapacity:64]);
        [postData appendString:@"&"];
        [postData appendString:[TSUtils encodeEventPairWithPrefix:@"" key:@"created-ms" value:[NSString stringWithFormat:@"%.0f", firstFiredTime*1000]]];
        
        for(NSString *key in self.customFields)
        {
            NSString *encodedPair = [TSUtils encodeEventPairWithPrefix:@"custom-" key:key value:[customFields valueForKey:key]];
            if(encodedPair != nil)
            {
                [postData appendString:@"&"];
                [postData appendString:encodedPair];
            }
        }
    }
}

- (void)dealloc
{
    RELEASE(uid);
    RELEASE(name);
    RELEASE(encodedName);
    RELEASE(productId);
    RELEASE(customFields);
    RELEASE(postData);
    SUPER_DEALLOC;
}

/*
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
*/

@end
