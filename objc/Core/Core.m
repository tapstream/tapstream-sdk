#import "Core.h"
#import "helpers.h"
#import "Logging.h"

#define kVersion @"2.0"
#define kEventUrlTemplate @"https://api.tapstream.com/%@/event/%@/"
#define kHitUrlTemplate @"http://api.tapstream.com/%@/hit/%@.gif"

@interface Event(hidden)
- (void)firing;
@end




@interface Core()

@property(nonatomic, STRONG_OR_RETAIN) id<Delegate> del;
@property(nonatomic, STRONG_OR_RETAIN) id<Platform> platform;
@property(nonatomic, STRONG_OR_RETAIN) id<CoreListener> listener;
@property(nonatomic, STRONG_OR_RETAIN) NSString *accountName;
@property(nonatomic, STRONG_OR_RETAIN) NSMutableString *postData;
@property(nonatomic, STRONG_OR_RETAIN) NSMutableSet *firingEvents;
@property(nonatomic, STRONG_OR_RETAIN) NSMutableSet *firedEvents;
@property(nonatomic, STRONG_OR_RETAIN) NSString *failingEventId;

- (NSString *)clean:(NSString *)s;
- (void)increaseDelay;
- (void)appendPostPairWithKey:(NSString *)key value:(NSString *)value;
- (void)makePostArgsWithSecret:(NSString *)secret hardware:(NSString *)hardware;
@end


@implementation Core

@synthesize del, platform, listener, accountName, postData, firingEvents, firedEvents, failingEventId;

- initWithDelegate:(id<Delegate>)delegateVal
	platform:(id<Platform>)platformVal
	listener:(id<CoreListener>)listenerVal
	accountName:(NSString *)accountNameVal
	developerSecret:(NSString *)developerSecretVal
	hardware:(NSString *)hardwareVal
{
	if((self = [super init]) != nil)
	{
		self.del = delegateVal;
		self.platform = platformVal;
		self.listener = listenerVal;
		self.accountName = [self clean:accountNameVal];
		self.postData = nil;
		self.failingEventId = nil;

		[self makePostArgsWithSecret:developerSecretVal hardware:hardwareVal];

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
	RELEASE(accountName);
	RELEASE(postData);
	RELEASE(firingEvents);
	RELEASE(firedEvents);
	RELEASE(failingEventId);
	SUPER_DEALLOC;
}

- (void)fireEvent:(Event *)e
{
	@synchronized(self)
	{
		// Notify the event that we are going to fire it so it can record the time
        [e firing];

		if(e.oneTimeOnly)
		{
			if([firedEvents containsObject:e.name])
			{
				[Logging logAtLevel:kLoggingInfo format:@"Tapstream ignoring event named \"%@\" because it is a one-time-only event that has already been fired", e.name];
                [listener reportOperation:@"event-ignored-already-fired"];
                [listener reportOperation:@"job-ended"];
                return;
			}
			else if([firedEvents containsObject:e.name])
			{
				[Logging logAtLevel:kLoggingInfo format:@"Tapstream ignoring event named \"%@\" because it is a one-time-only event that is already in progress", e.name];
                [listener reportOperation:@"event-ignored-already-in-progress"];
                [listener reportOperation:@"job-ended"];
                return;
			}

			[firingEvents addObject:e.name];
		}

		NSString *url = [NSString stringWithFormat:kEventUrlTemplate, accountName, e.encodedName];
		NSString *data = [postData stringByAppendingString:e.postData];


		int actualDelay = [del getDelay];
		dispatch_time_t dispatchTime = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * actualDelay);
		dispatch_after(dispatchTime, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{

			Response *response = [platform request:url data:data];
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
						[listener reportOperation:@"fired-list-saved" arg:e.uid];
					}

					// Success of any event resets the delay
					delay = 0;
				}
			}

			if(failed)
            {
			    if(response.status < 0)
                {
				    [Logging logAtLevel:kLoggingError format:@"Tapstream Error: Failed to fire event, error=%@", response.message];
			    }
                else if(response.status == 404)
                {
				    [Logging logAtLevel:kLoggingError format:@"Tapstream Error: Failed to fire event, http code %d\nDoes your event name contain characters that are not url safe? This event will not be retried.", response.status];
			    }
                else if(response.status == 403)
                {
				   [Logging logAtLevel:kLoggingError format:@"Tapstream Error: Failed to fire event, http code %d\nAre your account name and application secret correct?  This event will not be retried.", response.status];
			    }
                else
                {
				    NSString *retryMsg = @"";
				    if(!shouldRetry)
                    {
					    retryMsg = @"  This event will not be retried.";
				    }
				    [Logging logAtLevel:kLoggingError format:@"Tapstream Error: Failed to fire event, http code %d.%@", response.status, retryMsg];
			    }

			    [listener reportOperation:@"event-failed" arg:e.uid];
			    if(shouldRetry)
                {
				    [listener reportOperation:@"retry" arg:e.uid];
				    [listener reportOperation:@"job-ended"];
				    if([del isRetryAllowed])
                    {
					    [self fireEvent:e];
				    }
				    return;
			    }
		    }
            else
            {
            	[Logging logAtLevel:kLoggingInfo format:@"Tapstream fired event named \"%@\"", e.name];
			    [listener reportOperation:@"event-succeeded"];
		    }
		
		    [listener reportOperation:@"job-ended"];
		});
	}
}

- (void)fireHit:(Hit *)hit completion:(void(^)(Response *))completion
{
	NSString *url = [NSString stringWithFormat:kHitUrlTemplate, accountName, hit.encodedTrackerName];
	NSString *data = hit.postData;

	dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
		Response *response = [platform request:url data:data];
		if(response.status < 200 || response.status >= 300)
		{
			[Logging logAtLevel:kLoggingError format:@"Tapstream Error: Failed to fire hit, http code: %d", response.status];
            [listener reportOperation:@"hit-failed"];
        }
        else
        {
            [Logging logAtLevel:kLoggingInfo format:@"Tapstream fired hit to tracker: %@", hit.trackerName];
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

- (NSString *)encodeString:(NSString *)s
{
	return AUTORELEASE((BRIDGE_TRANSFER NSString *)CFURLCreateStringByAddingPercentEscapes(
		NULL, (CFStringRef)s, NULL, (CFStringRef)@"!*'();:@&=+$,/?%#[]", kCFStringEncodingUTF8));
}

- (NSString *)clean:(NSString *)s
{
	s = [s lowercaseString];
	s = [s stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
	return [self encodeString:s];
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

- (void)appendPostPairWithKey:(NSString *)key value:(NSString *)value
{
	if(postData == nil)
    {
    	self.postData = [[NSMutableString alloc] initWithCapacity:256];
    }
    else
    {
        [postData appendString:@"&"];
    }
    [postData appendString:[self encodeString:key]];
    [postData appendString:@"="];
    [postData appendString:[self encodeString:value]];
}

- (void)makePostArgsWithSecret:(NSString *)secret hardware:(NSString *)hardware
{
	[self appendPostPairWithKey:@"secret" value:secret];
	[self appendPostPairWithKey:@"sdkversion" value:kVersion];

	if(hardware != nil)
	{
		if([hardware length] > 255)
		{
			[Logging logAtLevel:kLoggingWarn format:@"Tapstream Warning: Hardware argument exceeds 255 characters, it will not be included with fired events"];
		}
		else
		{
			[self appendPostPairWithKey:@"hardware" value:hardware];
		}
	}

	[self appendPostPairWithKey:@"uuid" value:[platform loadUuid]];

	#if TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
	[self appendPostPairWithKey:@"platform" value:@"iOS"];
	#else
	[self appendPostPairWithKey:@"platform" value:@"Mac"];
	#endif

	[self appendPostPairWithKey:@"vendor" value:[platform getManufacturer]];
	[self appendPostPairWithKey:@"model" value:[platform getModel]];
	[self appendPostPairWithKey:@"os" value:[platform getOs]];
	[self appendPostPairWithKey:@"resolution" value:[platform getResolution]];
	[self appendPostPairWithKey:@"locale" value:[platform getLocale]];

	NSString *offset = [NSString stringWithFormat:@"%d", (int)[[NSTimeZone systemTimeZone] secondsFromGMT]];
	[self appendPostPairWithKey:@"gmtoffset" value:offset];
}


@end