#import "TSCore.h"
#import "TSHelpers.h"
#import "TSLogging.h"
#import "TSUtils.h"

#define kTSVersion @"2.5"
#define kTSEventUrlTemplate @"https://api.tapstream.com/%@/event/%@/"
#define kTSHitUrlTemplate @"http://api.tapstream.com/%@/hit/%@.gif"
#define kTSConversionUrlTemplate @"https://reporting.tapstream.com/v1/timelines/lookup?secret=%@&event_session=%@"
#define kTSConversionPollInterval 1
#define kTSConversionPollCount 10

@interface TSEvent(hidden)
- (void)firing;
@end




@interface TSCore()

@property(nonatomic, STRONG_OR_RETAIN) id<TSDelegate> del;
@property(nonatomic, STRONG_OR_RETAIN) id<TSPlatform> platform;
@property(nonatomic, STRONG_OR_RETAIN) id<TSCoreListener> listener;
@property(nonatomic, STRONG_OR_RETAIN) id<TSAppEventSource> appEventSource;
@property(nonatomic, STRONG_OR_RETAIN) TSConfig *config;
@property(nonatomic, STRONG_OR_RETAIN) NSString *accountName;
@property(nonatomic, STRONG_OR_RETAIN) NSString *secret;
@property(nonatomic, STRONG_OR_RETAIN) NSString *encodedAppName;
@property(nonatomic, STRONG_OR_RETAIN) NSMutableString *postData;
@property(nonatomic, STRONG_OR_RETAIN) NSMutableSet *firingEvents;
@property(nonatomic, STRONG_OR_RETAIN) NSMutableSet *firedEvents;
@property(nonatomic, STRONG_OR_RETAIN) NSString *failingEventId;

- (NSString *)clean:(NSString *)s;
- (void)increaseDelay;
- (void)makePostArgs;
@end


@implementation TSCore

@synthesize del, platform, listener, appEventSource, config, accountName, secret, encodedAppName, postData, firingEvents, firedEvents, failingEventId;

- (id)initWithDelegate:(id<TSDelegate>)delegateVal
	platform:(id<TSPlatform>)platformVal
	listener:(id<TSCoreListener>)listenerVal
	appEventSource:(id<TSAppEventSource>)appEventSourceVal
	accountName:(NSString *)accountNameVal
	developerSecret:(NSString *)developerSecretVal
	config:(TSConfig *)configVal
{
	if((self = [super init]) != nil)
	{
		self.del = delegateVal;
		self.platform = platformVal;
		self.listener = listenerVal;
		self.config = configVal;
		self.appEventSource = appEventSourceVal;
		self.accountName = [self clean:accountNameVal];
		self.secret = developerSecretVal;
		self.encodedAppName = nil;
		self.postData = nil;
		self.failingEventId = nil;

		[self makePostArgs];

		self.firingEvents = [[NSMutableSet alloc] initWithCapacity:32];
		self.firedEvents = [platform loadFiredEvents];
	}
	return self;
}

- (void)dealloc
{
	RELEASE(del);
	RELEASE(platform);
	RELEASE(listener);
	RELEASE(appEventSource);
	RELEASE(accountName);
	RELEASE(secret);
	RELEASE(encodedAppName);
	RELEASE(postData);
	RELEASE(firingEvents);
	RELEASE(firedEvents);
	RELEASE(failingEventId);
	SUPER_DEALLOC;
}

- (void)start
{
#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
	NSString *platformName = @"ios";
#else
	NSString *platformName = @"mac";
#endif

	self.encodedAppName = [platform getAppName];
	if(self.encodedAppName == nil)
	{
		self.encodedAppName = @"";
	}
	self.encodedAppName = [TSUtils encodeString:self.encodedAppName];


	if(config.fireAutomaticInstallEvent)
	{
		if(config.installEventName != nil)
		{
			[self fireEvent:[TSEvent eventWithName:config.installEventName oneTimeOnly:YES]];
		}
		else
		{
			NSString *eventName = [NSString stringWithFormat:@"%@-%@-install", platformName, self.encodedAppName];
			[self fireEvent:[TSEvent eventWithName:eventName oneTimeOnly:YES]];
		}
	}

	__unsafe_unretained TSCore *me = self;
		
	if(config.fireAutomaticOpenEvent)
	{
		// Fire the initial open event
		if(config.openEventName != nil)
		{
			[self fireEvent:[TSEvent eventWithName:config.openEventName oneTimeOnly:NO]];
		}
		else
		{
			NSString *eventName = [NSString stringWithFormat:@"%@-%@-open", platformName, self.encodedAppName];
			[self fireEvent:[TSEvent eventWithName:eventName oneTimeOnly:NO]];
		}
	
		// Subscribe to be notified whenever the app enters the foreground
		[appEventSource setOpenHandler:^() {
			if(me.config.openEventName != nil)
			{
				[me fireEvent:[TSEvent eventWithName:me.config.openEventName oneTimeOnly:NO]];
			}
			else
			{
				NSString *eventName = [NSString stringWithFormat:@"%@-%@-open", platformName, me.encodedAppName];
				[me fireEvent:[TSEvent eventWithName:eventName oneTimeOnly:NO]];
			}
		}];
	}

	if(config.fireAutomaticIAPEvents)
	{
		[appEventSource setTransactionHandler:^(NSString *transactionId, NSString *productId, int quantity, int priceInCents, NSString *currencyCode) {
			[me fireEvent:[TSEvent
				iapEventWithName:[NSString stringWithFormat:@"%@-%@-purchase-%@", platformName, me.encodedAppName, productId]
				transactionId:transactionId
				productId:productId
				quantity:quantity
				priceInCents:priceInCents
				currency:currencyCode]];
		}];
	}

	if(config.conversionListener != nil)
	{
		__block int tries = 0;
		
		NSString *url = [NSString stringWithFormat:kTSConversionUrlTemplate, secret, [platform loadUuid]];

		__block void (^conversionCheck)();
		__block void (^ __unsafe_unretained weakConversionCheck)();
		
		weakConversionCheck = conversionCheck = ^{
			tries++;
			bool retry = true;

			TSResponse *response = [platform request:url data:nil method:@"GET"];
			if(response.status >= 200 && response.status < 300)
			{
				NSString *jsonString = AUTORELEASE([[NSString alloc] initWithData:response.data encoding:NSUTF8StringEncoding]);
				
				// If it is not an empty json array, then make the callback
				NSError *error = nil;
				NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:@"^\\s*\\[\\s*\\]\\s*$" options:0 error:&error];
				if(error == nil && [regex numberOfMatchesInString:jsonString options:NSMatchingAnchored range:NSMakeRange(0, [jsonString length])] == 0)
				{
					retry = false;
					config.conversionListener(response.data);
				}
			}
			
			if(retry && tries <= kTSConversionPollCount)
			{
				dispatch_after(dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * kTSConversionPollInterval), dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), weakConversionCheck);	
			}
		};

		dispatch_after(dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * kTSConversionPollInterval), dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), conversionCheck);
	}
}

- (void)fireEvent:(TSEvent *)e
{
	@synchronized(self)
	{
		// Notify the event that we are going to fire it so it can record the time
		[e firing];

		if(e.oneTimeOnly)
		{
			if([firedEvents containsObject:e.name])
			{
				[TSLogging logAtLevel:kTSLoggingInfo format:@"Tapstream ignoring event named \"%@\" because it is a one-time-only event that has already been fired", e.name];
				[listener reportOperation:@"event-ignored-already-fired" arg:e.name];
				[listener reportOperation:@"job-ended" arg:e.name];
				return;
			}
			else if([firingEvents containsObject:e.name])
			{
				[TSLogging logAtLevel:kTSLoggingInfo format:@"Tapstream ignoring event named \"%@\" because it is a one-time-only event that is already in progress", e.name];
				[listener reportOperation:@"event-ignored-already-in-progress" arg:e.name];
				[listener reportOperation:@"job-ended" arg:e.name];
				return;
			}

			[firingEvents addObject:e.name];
		}

		NSString *url = [NSString stringWithFormat:kTSEventUrlTemplate, accountName, e.encodedName];
		NSString *data = [postData stringByAppendingString:e.postData];


		int actualDelay = [del getDelay];
		dispatch_time_t dispatchTime = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * actualDelay);
		dispatch_after(dispatchTime, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{

			TSResponse *response = [platform request:url data:data method:@"POST"];
			bool failed = response.status < 200 || response.status >= 300;
			bool shouldRetry = response.status < 0 || (response.status >= 500 && response.status < 600);

			@synchronized(self)
			{
				if(e.oneTimeOnly)
				{
					[firingEvents removeObject:e.name];
				}

				if(failed)
				{
					// Only increase delays if we actually intend to retry the event
					if(shouldRetry)
					{
						// Not every job that fails will increase the retry delay.  It will be the responsibility of
						// the first failed job to increase the delay after every failure.
						if(delay == 0)
						{
							// This is the first job to fail, it must be the one to manage delay timing
							self.failingEventId = e.uid;
							[self increaseDelay];
						}
						else if([failingEventId isEqualToString:e.uid])
						{
							[self increaseDelay];
						}
					}
				}
				else
				{
					if(e.oneTimeOnly)
					{
						[firedEvents addObject:e.name];

						[platform saveFiredEvents:firedEvents];
						[listener reportOperation:@"fired-list-saved" arg:e.name];
					}

					// Success of any event resets the delay
					delay = 0;
				}
			}

			if(failed)
			{
				if(response.status < 0)
				{
					[TSLogging logAtLevel:kTSLoggingError format:@"Tapstream Error: Failed to fire event, error=%@", response.message];
				}
				else if(response.status == 404)
				{
					[TSLogging logAtLevel:kTSLoggingError format:@"Tapstream Error: Failed to fire event, http code %d\nDoes your event name contain characters that are not url safe? This event will not be retried.", response.status];
				}
				else if(response.status == 403)
				{
				   [TSLogging logAtLevel:kTSLoggingError format:@"Tapstream Error: Failed to fire event, http code %d\nAre your account name and application secret correct?  This event will not be retried.", response.status];
				}
				else
				{
					NSString *retryMsg = @"";
					if(!shouldRetry)
					{
						retryMsg = @"  This event will not be retried.";
					}
					[TSLogging logAtLevel:kTSLoggingError format:@"Tapstream Error: Failed to fire event, http code %d.%@", response.status, retryMsg];
				}

				[listener reportOperation:@"event-failed" arg:e.name];
				if(shouldRetry)
				{
					[listener reportOperation:@"retry" arg:e.name];
					[listener reportOperation:@"job-ended" arg:e.name];
					if([del isRetryAllowed])
					{
						[self fireEvent:e];
					}
					return;
				}
			}
			else
			{
				[TSLogging logAtLevel:kTSLoggingInfo format:@"Tapstream fired event named \"%@\"", e.name];
				[listener reportOperation:@"event-succeeded" arg:e.name];
			}
		
			[listener reportOperation:@"job-ended" arg:e.name];
		});
	}
}

- (void)fireHit:(TSHit *)hit completion:(void(^)(TSResponse *))completion
{
	NSString *url = [NSString stringWithFormat:kTSHitUrlTemplate, accountName, hit.encodedTrackerName];
	NSString *data = hit.postData;

	dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
		TSResponse *response = [platform request:url data:data method:@"POST"];
		if(response.status < 200 || response.status >= 300)
		{
			[TSLogging logAtLevel:kTSLoggingError format:@"Tapstream Error: Failed to fire hit, http code: %d", response.status];
			[listener reportOperation:@"hit-failed"];
		}
		else
		{
			[TSLogging logAtLevel:kTSLoggingInfo format:@"Tapstream fired hit to tracker: %@", hit.trackerName];
			[listener reportOperation:@"hit-succeeded"];
		}

		if(completion != nil)
		{
			completion(response);
		}
	});
}

- (int)getDelay
{
	return delay;
}

- (NSString *)clean:(NSString *)s
{
	s = [s lowercaseString];
	s = [s stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
	return [TSUtils encodeString:s];
}

- (void)increaseDelay
{
	if(delay == 0)
	{
		// First failure
		delay = 2;
	}
	else
	{
		// 2, 4, 8, 16, 32, 60, 60, 60...
		int newDelay = (int)pow( 2, log2( delay ) + 1 );
		delay = newDelay > 60 ? 60 : newDelay;
	}
	[listener reportOperation:@"increased-delay"];
}

- (void)appendPostPairWithPrefix:(NSString *)prefix key:(NSString *)key value:(NSString *)value
{
	NSString *encodedPair = [TSUtils encodeEventPairWithPrefix:prefix key:key value:value];
	if(encodedPair == nil)
	{
		return;
	}

	if(postData == nil)
	{
		self.postData = [[NSMutableString alloc] initWithCapacity:256];
	}
	else
	{
		[postData appendString:@"&"];
	}
	[postData appendString:encodedPair];
}

- (void)makePostArgs
{
	[self appendPostPairWithPrefix:@"" key:@"secret" value:secret];
	[self appendPostPairWithPrefix:@"" key:@"sdkversion" value:kTSVersion];

	[self appendPostPairWithPrefix:@"" key:@"hardware" value:config.hardware];
	[self appendPostPairWithPrefix:@"" key:@"hardware-odin1" value:config.odin1];
#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
	[self appendPostPairWithPrefix:@"" key:@"hardware-open-udid" value:config.openUdid];
	[self appendPostPairWithPrefix:@"" key:@"hardware-ios-udid" value:config.udid];
	[self appendPostPairWithPrefix:@"" key:@"hardware-ios-idfa" value:config.idfa];
	[self appendPostPairWithPrefix:@"" key:@"hardware-ios-secure-udid" value:config.secureUdid];
#else
	[self appendPostPairWithPrefix:@"" key:@"hardware-mac-serial-number" value:config.serialNumber];
#endif

	if(config.collectWifiMac)
	{
		[self appendPostPairWithPrefix:@"" key:@"hardware-wifi-mac" value:[platform getWifiMac]];
	}

	[self appendPostPairWithPrefix:@"" key:@"uuid" value:[platform loadUuid]];

#if TEST_IOS || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
	[self appendPostPairWithPrefix:@"" key:@"platform" value:@"iOS"];
#else
	[self appendPostPairWithPrefix:@"" key:@"platform" value:@"Mac"];
#endif

	[self appendPostPairWithPrefix:@"" key:@"vendor" value:[platform getManufacturer]];
	[self appendPostPairWithPrefix:@"" key:@"model" value:[platform getModel]];
	[self appendPostPairWithPrefix:@"" key:@"os" value:[platform getOs]];
	[self appendPostPairWithPrefix:@"" key:@"resolution" value:[platform getResolution]];
	[self appendPostPairWithPrefix:@"" key:@"locale" value:[platform getLocale]];
	[self appendPostPairWithPrefix:@"" key:@"app-name" value:[platform getAppName]];
	[self appendPostPairWithPrefix:@"" key:@"package-name" value:[platform getPackageName]];
	[self appendPostPairWithPrefix:@"" key:@"gmtoffset" value:[TSUtils stringifyInteger:[[NSTimeZone systemTimeZone] secondsFromGMT]]];

	// Add global custom params
	for (NSString *key in config.globalEventParams) {
		id value = [config.globalEventParams objectForKey:key];
		[self appendPostPairWithPrefix:@"custom-" key:key value:value];
	}
}


@end