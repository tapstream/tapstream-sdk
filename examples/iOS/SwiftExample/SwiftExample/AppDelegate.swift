import UIKit
import StoreKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, SKProductsRequestDelegate, SKPaymentTransactionObserver {
                            
    var window: UIWindow?
    var products: [SKProduct] = []


	func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject : AnyObject]?) -> Bool {

        if(UIDevice.currentDevice().userInterfaceIdiom == UIUserInterfaceIdiom.Pad){
            let splitViewController : UISplitViewController = self.window?.rootViewController as! UISplitViewController
            let navigationController : UINavigationController = splitViewController.viewControllers[splitViewController.viewControllers.endIndex] as! UINavigationController
            splitViewController.delegate = navigationController.topViewController as? UISplitViewControllerDelegate
        }
        

        let config = TSConfig.configWithDefaults() as! TSConfig
        config.globalEventParams.setValue(25.4, forKey: "degrees")
        TSTapstream.createWithAccountName("sdktest", developerSecret: "YGP2pezGTI6ec48uti4o1w", config: config)
        
        let tracker = TSTapstream.instance() as! TSTapstream
        
        func handleConversionData(jsonInfo: NSData!) -> Void {
            if jsonInfo == nil {
                NSLog("No conversion data");
            }else{
                NSLog("Conversion Data: %@", NSString(data: jsonInfo, encoding:NSUTF8StringEncoding)!)
				let maybeData: NSDictionary?
				do {
					maybeData = try NSJSONSerialization.JSONObjectWithData(jsonInfo, options: NSJSONReadingOptions()) as? NSDictionary
				} catch {
					maybeData = nil
				}


                if maybeData != nil {
                    // Read some data from the JSON object
                }
            }
            
            
        }
        tracker.getConversionData(handleConversionData)
        
        let e = TSEvent.eventWithName("test-event", oneTimeOnly: false) as! TSEvent
        e.addValue("John Doe", forKey: "player")
        e.addValue(10.1, forKey: "degrees")
        e.addValue(5, forKey: "score")
        tracker.fireEvent(e)
        
        SKPaymentQueue.defaultQueue().addTransactionObserver(self)
        
        
        let productIds = NSSet(array: ["com.tapstream.catalog.tiddlywinks"])
        let request = SKProductsRequest(productIdentifiers: productIds as! Set<String>)
        request.delegate = self
        request.start()
        
        return true
    }
    
    func request(request: SKRequest, didFailWithError error: NSError) {
        NSLog("%@", error.description);
    }
    
    func productsRequest(request: SKProductsRequest, didReceiveResponse response: SKProductsResponse) {
        self.products = response.products as [SKProduct]
        
        for ident in response.invalidProductIdentifiers as [NSString] {
            NSLog("Invalid Product Id: %@", ident)
        }

        for ident in response.products as [SKProduct] {
            NSLog("Valid Product Id: %@", ident)
        }
        
        if self.products.count > 0 {
            let product = self.products[0]
            let payment = SKMutablePayment(product: product)
            payment.quantity = 2
            
            SKPaymentQueue.defaultQueue().addPayment(payment)
        }
        
    }
    
    func paymentQueue(queue: SKPaymentQueue, updatedTransactions transactions: [SKPaymentTransaction]) {
        for transaction in transactions {
            NSLog("TX: %@", transaction);
        }
    }
	
	func application(application: UIApplication,
		continueUserActivity userActivity: NSUserActivity,
		restorationHandler: ([AnyObject]?) -> Void) -> Bool {
			let tracker = TSTapstream.instance() as! TSTapstream
			let ul = tracker.handleUniversalLink(userActivity)
			if(ul.status == kTSULValid)
			{
				// Do deeplink things
				let deeplinkURL = ul.deeplinkURL, fallbackURL = ul.fallbackURL
				NSLog("Universal Link Handled: %@, %@", deeplinkURL, fallbackURL);
				return true;
			}
			else if(userActivity.activityType == NSUserActivityTypeBrowsingWeb)
			{
				// Fall back to openURL if link not handled.
				UIApplication.sharedApplication().openURL(userActivity.webpageURL!);
			}

			return false;
	}


    func applicationWillResignActive(application: UIApplication!) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(application: UIApplication!) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(application: UIApplication!) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(application: UIApplication!) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(application: UIApplication!) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }


}

