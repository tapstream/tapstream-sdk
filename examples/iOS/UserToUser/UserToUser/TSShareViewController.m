//
//  TSShareViewController.m
//  UserToUser
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSShareViewController.h"
#import "TSHelpers.h"

#define kTSShareViewBackgroundTag 100

@interface TSShareViewController ()

@end

@implementation TSShareViewController

+ (id)shareViewController
{
    return AUTORELEASE([[TSShareViewController alloc] initWithNibName:@"TSShareView" bundle:nil]);
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    if(self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    UIView *v = [self.view viewWithTag:kTSShareViewBackgroundTag];
    v.layer.backgroundColor = [UIColor colorWithWhite:0.949 alpha:1.0].CGColor;
    v.layer.cornerRadius = 10;
    v.layer.masksToBounds = YES;
    v.layer.borderWidth = 1;
    v.layer.borderColor = [UIColor colorWithRed:0.137 green:0.122 blue:0.125 alpha:1.0].CGColor;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
