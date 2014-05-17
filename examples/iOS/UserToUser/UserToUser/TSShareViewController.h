//
//  TSShareViewController.h
//  UserToUser
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TSShareViewController : UIViewController {
    @private
    int shareMethodsCompleted;
}

+ (id)shareViewController;
- (IBAction)onBtnClose:(id)sender;
- (IBAction)onBtnDone:(id)sender;
- (IBAction)onBtnMessaging:(id)sender;
- (IBAction)onBtnTwitter:(id)sender;
- (IBAction)onBtnFacebook:(id)sender;
- (IBAction)onBtnEmail:(id)sender;

@end
