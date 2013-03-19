#pragma once
#import <Foundation/Foundation.h>

@interface TSConfig : NSObject {
@private
	// Deprecated, hardware-id field
	NSString *hardware;

	// Optional hardware identifiers that can be provided by the caller
	NSString *udid;
	NSString *idfa;
	NSString *secureUdid;

	// Set these to false if you do NOT want to collect this data
	BOOL collectWifiMac;

#if !(TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
	BOOL collectSerialNumber;
#endif
}

@property(nonatomic, retain) NSString *hardware;
@property(nonatomic, retain) NSString *udid;
@property(nonatomic, retain) NSString *idfa;
@property(nonatomic, retain) NSString *secureUdid;
@property(nonatomic, assign) BOOL collectWifiMac;
#if !(TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
@property(nonatomic, assign) BOOL collectSerialNumber;
#endif

- (id)init;
+ (id)configWithDefaults;

@end