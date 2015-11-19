//
//  TSLanderController.h
//  ExampleApp
//
//  Created by Adam Bard on 2015-11-03.
//  Copyright © 2015 Example. All rights reserved.
//

#ifndef TSLanderController_h
#define TSLanderController_h

#import "TSLanderDelegate.h"
#import "TSLander.h"
#import <UIKit/UIKit.h>

@interface TSLanderController : UIViewController<UIWebViewDelegate>
@property(nonatomic, STRONG_OR_RETAIN) id<TSLanderDelegate> delegate;

+ (id)controllerWithLander:(TSLander*)lander delegate:(id<TSLanderDelegate>)delegate;
@end

#endif /* TSLanderController_h */
