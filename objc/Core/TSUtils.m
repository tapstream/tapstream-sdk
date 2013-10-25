#include "TSUtil.h"


@implementation TSUtils

+ (NSString *)encodeString:(NSString *)s
{
	if(s == nil)
	{
		return nil;
	}

	return AUTORELEASE((BRIDGE_TRANSFER NSString *)CFURLCreateStringByAddingPercentEscapes(
		NULL, (CFStringRef)s, NULL, (CFStringRef)@"!*'();:@&=+$,/?%#[]", kCFStringEncodingUTF8));
}

+ (NSString *)stringify:(id)value
{
	if(value == nil)
	{
		return nil;
	}

	if([value isKindOfClass:[NSString class]])
	{
		return (NSString *)value;
	}
	else if([value isKindOfClass:[NSNumber class]])
	{
		NSNumber *number = (NSNumber *)value;
		
		if(strcmp([number objCType], @encode(int)) == 0)
		{
			return [NSString stringWithFormat:@"%d", [number intValue]];
		}
		else if(strcmp([number objCType], @encode(uint)) == 0)
		{
			return [NSString stringWithFormat:@"%u", [number unsignedIntValue]];
		}
		else if(strcmp([number objCType], @encode(double)) == 0 ||
			strcmp([number objCType], @encode(float)) == 0)
		{
			return [NSString stringWithFormat:@"%g", [number doubleValue]];
		}
		else if(strcmp([number objCType], @encode(BOOL)) == 0)
		{
			return [value boolValue] ? @"true" : @"false";
		}
		else
		{
			NSLog(@"Tapstream Event cannot accept an NSNumber param holding this type, skipping param");
		}
	}
	else
	{
		NSLog(@"Tapstream Event cannot accept a param of this type, skipping param");
	}

	return nil;
}

+ (NSString *)encodeEventPairWithPrefix:(NSString *)prefix key:(NSString *)key value:(id)value;
{
	if(key == nil || value == nil)
	{
		return nil;
	}

	if(key.length > 255)
	{
		[TSLogging logAtLevel:kTSLoggingWarn format:@"Tapstream Warning: Event key exceeds 255 characters, this field will not be included in the post (key=%@)", key];
		return nil;
	}

	NSString *encodedKey = [TSUtils encodeString:[prefix stringByAppendingString:key]];
	if(encodedKey == nil)
	{
		return nil;
	}

	NSString *encodedValue = [TSUtils encodeString:[TSUtils stringify:value]];
	if(encodedValue == nil)
	{
		return nil;
	}

	if(encodedValue.length > 255)
	{
		[TSLogging logAtLevel:kTSLoggingWarn format:@"Tapstream Warning: Event value exceeds 255 characters, this field will not be included in the post (value=%@)", value];
		return nil;
	}

	return [encodedKey stringByAppendingString:[@"=" stringByAppendingString:encodedValue]];
}

@end