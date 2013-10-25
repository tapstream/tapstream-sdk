#pragma once

@interface TSUtils

+ (NSString *)encodeString:(NSString *)s;
+ (NSString *)stringify:(id)value;
+ (NSString *)encodeEventPairWithPrefix:(NSString *)prefix key:(NSString *)key value:(id)value;

@end