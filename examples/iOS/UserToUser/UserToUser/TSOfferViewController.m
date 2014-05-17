//
//  TSOfferViewController.m
//  UserToUser
//
//  Created by Eric on 2014-05-16.
//  Copyright (c) 2014 Tapstream. All rights reserved.
//

#import "TSOfferViewController.h"

@interface TSOfferViewController ()
@end

@implementation TSOfferViewController

@synthesize offer;

+ (id)controllerWithOffer:(TSOffer *)offer
{
    return AUTORELEASE([[TSOfferViewController alloc] initWithNibName:@"TSOfferView" bundle:nil offer:offer]);
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil offer:(TSOffer *)offerVal
{
    if(self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        self.offer = offerVal;
        //NSString *url = [NSString stringWithFormat:@"%u", (unsigned int)self.offer.ident];
        NSString *url = @"http://google.ca/";
        [((UIWebView *)self.view) loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
    }
    return self;
}

- (void)dealloc
{
    RELEASE(self->offer);
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
