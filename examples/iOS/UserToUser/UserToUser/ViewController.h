//
//  ViewController.h
//  UserToUser
//
//  Created by Eric on 2014-05-09.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController

@property(strong) IBOutlet UITextField *insertionPointText;

- (IBAction)onTestInsertionPoint:(id)sender;
- (IBAction)onMakeInstallTimeAgesAgo:(id)sender;
- (IBAction)onEraseLastShownTimes:(id)sender;
- (IBAction)onKill:(id)sender;

@end
