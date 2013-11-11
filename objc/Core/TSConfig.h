#pragma once
#import <Foundation/Foundation.h>

typedef void(^TSConversionListener)(id jsonObject, NSString *jsonString);

@interface TSConfig : NSObject {
@private
	// Deprecated, hardware-id field
	NSString *hardware;

	// Optional hardware identifiers that can be provided by the caller
	NSString *odin1;
#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR	
	NSString *udid;
	NSString *idfa;
	NSString *secureUdid;
	NSString *openUdid;
#else
	NSString *serialNumber;
#endif
	
	// Set these to false if you do NOT want to collect this data
	BOOL collectWifiMac;

	// Set these if you want to override the names of the automatic events sent by the sdk
	NSString *installEventName;
	NSString *openEventName;

	// Unset these if you want to disable the sending of the automatic events
	BOOL fireAutomaticInstallEvent;
	BOOL fireAutomaticOpenEvent;

	// If this handler is set, and if there was a conversion that lead to this application
	// install, then the handler will be called with the conversion details.
	//
	// On iOS >= 5, the first parameter will be an instance of a json object, and the
	// second parameter will be nil.  On iOS < 5, the first parameter will be nil,
	// and the second will be a string containing a json object definition.
	TSConversionListener conversionListener;
}

@property(nonatomic, retain) NSString *hardware;
@property(nonatomic, retain) NSString *odin1;
#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR	
@property(nonatomic, retain) NSString *udid;
@property(nonatomic, retain) NSString *idfa;
@property(nonatomic, retain) NSString *secureUdid;
@property(nonatomic, retain) NSString *openUdid;
#else
@property(nonatomic, retain) NSString *serialNumber;
#endif

@property(nonatomic, assign) BOOL collectWifiMac;

@property(nonatomic, retain) NSString *installEventName;
@property(nonatomic, retain) NSString *openEventName;

@property(nonatomic, assign) BOOL fireAutomaticInstallEvent;
@property(nonatomic, assign) BOOL fireAutomaticOpenEvent;

@property(nonatomic, copy) TSConversionListener conversionListener;

- (id)init;
+ (id)configWithDefaults;

@end