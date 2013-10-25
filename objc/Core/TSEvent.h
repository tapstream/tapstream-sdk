#pragma once
#import <Foundation/Foundation.h>
#import "TSHelpers.h"

@interface TSEvent : NSObject {
@private
	NSTimeInterval firstFiredTime;
	NSString *uid;
	NSString *name;
	NSString *encodedName;
	BOOL oneTimeOnly;
	NSMutableString *postData;
}

@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *uid;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *name;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *encodedName;
@property(nonatomic, STRONG_OR_RETAIN, readonly) NSString *postData;
@property(nonatomic, assign, readonly) BOOL oneTimeOnly;

+ (id)eventWithName:(NSString *)name oneTimeOnly:(BOOL)oneTimeOnly;
+ (id)iapEventWithTransactionId:(NSString *)transactionId
	productId:(NSString *)productId
	quantity:(int)quantity
	priceInCents:(int)priceInCents
	currency:(NSString *)currencyCode;

- (void)addValue:(id)value forKey:(NSString *)key;




// DEPRECATED:
// These type-specific methods are deprecated.
// Instead, use the generic method:
//	- addValue:forKey:
- (void)addIntegerValue:(int)value forKey:(NSString *)key __attribute__((deprecated));
- (void)addUnsignedIntegerValue:(uint)value forKey:(NSString *)key __attribute__((deprecated));
- (void)addDoubleValue:(double)value forKey:(NSString *)key __attribute__((deprecated));
- (void)addBooleanValue:(BOOL)value forKey:(NSString *)key __attribute__((deprecated));

@end