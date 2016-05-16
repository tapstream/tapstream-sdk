//
//  TestUniversalLinks.m
//  TapstreamIOSTest
//
//  Created by Adam Bard on 2016-05-16.
//  Copyright © 2016 Adam Bard. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <Foundation/Foundation.h>
#import "TSUniversalLink.h"
#import "TSError.h"

@interface TestUniversalLinks : XCTestCase
@property(nonatomic, strong) NSString* validJSON;
@property(nonatomic, strong) NSString* disabledJSON;
@property(nonatomic, strong) NSString* invalidJSON;
@end

@implementation TestUniversalLinks
- (void)setUp {
	[super setUp];
}
- (void)tearDown {
	// Put teardown code here. This method is called after the invocation of each test method in the class.
	[super tearDown];
}

- (void)testNilResponse {
	TSResponse* r = [[TSResponse alloc] initWithStatus:-1 message:nil data:nil];
	TSUniversalLink* ul = [TSUniversalLink universalLinkWithDeeplinkQueryResponse:r];

	XCTAssertEqual(ul.status, kTSULUnknown);
	XCTAssert(ul.error != nil);
	XCTAssertEqual(ul.error.code, kTSInvalidResponse);

	r = [[TSResponse alloc] initWithStatus:200 message:nil data:nil];
	ul = [TSUniversalLink universalLinkWithDeeplinkQueryResponse:r];

	XCTAssertEqual(ul.status, kTSULUnknown);
	XCTAssert(ul.error != nil);
	XCTAssertEqual(ul.error.code, kTSInvalidResponse);
}

- (void)testInvalidJSON {

	TSResponse* r = [[TSResponse alloc] initWithStatus:200 message:nil data:[NSKeyedArchiver archivedDataWithRootObject:@""]];
	TSUniversalLink* ul = [TSUniversalLink universalLinkWithDeeplinkQueryResponse:r];

	XCTAssertEqual(ul.status, kTSULUnknown);
	XCTAssert(ul.error != nil);
	XCTAssertEqual(ul.error.code, kTSInvalidResponse);
	XCTAssert([ul.error.userInfo valueForKey:@"message"], @"Invalid JSON response");
	XCTAssert([ul.error.userInfo valueForKey:@"cause"] != nil);


	r = [[TSResponse alloc] initWithStatus:200 message:nil data:[NSKeyedArchiver archivedDataWithRootObject:@"{]}"]];
	ul = [TSUniversalLink universalLinkWithDeeplinkQueryResponse:r];

	XCTAssertEqual(ul.status, kTSULUnknown);
	XCTAssert(ul.error != nil);
	XCTAssertEqual(ul.error.code, kTSInvalidResponse);
}

@end