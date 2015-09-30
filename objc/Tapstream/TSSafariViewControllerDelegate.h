//
//  TSSafariViewControllerDelegate.h
//  ExampleApp
//
//  Created by Adam Bard on 2015-09-12.
//  Copyright © 2015 Example. All rights reserved.
//


#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 90000

#ifndef TS_SAFARI_VIEW_CONTROLLER_ENABLED
#define TS_SAFARI_VIEW_CONTROLLER_ENABLED
#endif

#import <Foundation/Foundation.h>
#import <SafariServices/SafariServices.h>

@interface TSSafariViewControllerDelegate : UIViewController<SFSafariViewControllerDelegate>

@property(nonatomic, strong) NSURL* url;
@property(nonatomic, strong) void (^completion)(void);
@property(nonatomic, weak) UIViewController* parent;

- (TSSafariViewControllerDelegate*)initWithURLAndCompletion:(NSURL*)url completion:(void (^)(void))completion;

@end
#else // IOS < 9
@interface TSSafariViewControllerDelegate : NSObject
- (TSSafariViewControllerDelegate*)initWithURLAndCompletion:(NSURL*)url completion:(void (^)(void))completion;
@end
#endif