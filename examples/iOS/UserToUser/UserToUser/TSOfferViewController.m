//
//  TSOfferViewController.m
//  UserToUser
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSOfferViewController.h"

@interface TSOfferViewController ()

@property(STRONG_OR_RETAIN, nonatomic) TSOffer *offer;
@property(STRONG_OR_RETAIN, nonatomic) UIViewController *parentViewController;
@property(assign, nonatomic) id<TSUserToUserDelegate> delegate;

@end

@implementation TSOfferViewController

@synthesize parentViewController;
@synthesize offer;

+ (id)controllerWithOffer:(TSOffer *)offer parentViewController:(UIViewController *)parentViewController delegate:(id<TSUserToUserDelegate>)delegate
{
    return AUTORELEASE([[TSOfferViewController alloc] initWithNibName:@"TSOfferView" bundle:nil offer:offer parentViewController:parentViewController delegate:delegate]);
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil offer:(TSOffer *)offerVal parentViewController:(UIViewController *)parentViewControllerVal delegate:(id<TSUserToUserDelegate>)delegateVal
{
    if(self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        self.offer = offerVal;
        self.parentViewController = parentViewControllerVal;
        self.delegate = delegateVal;
        
        //NSString *url = [NSString stringWithFormat:@"%u", (unsigned int)self.offer.ident];
        NSString *url = @"http://google.ca/";
        [((UIWebView *)self.view) loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
    }
    return self;
}

- (void)dealloc
{
    RELEASE(self->offer);
    RELEASE(self->parentViewController);
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
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
