#import <UIKit/UIKit.h>
#import <StoreKit/StoreKit.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate, SKProductsRequestDelegate, SKPaymentTransactionObserver> {
}

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) NSArray *products;
@property (strong, nonatomic) SKProductsRequest *request;

@end
