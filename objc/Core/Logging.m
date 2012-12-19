#import "Logging.h"
#import "helpers.h"

static void(^currentLogger)(int, NSString *) = nil;
static bool overridden = false;


@implementation Logging

+ (void)setLogger:(void(^)(int, NSString *))logger
{
	@synchronized(self)
	{
		overridden = true;
		currentLogger = logger;
	}
}

+ (void)logAtLevel:(LoggingLevel)level format:(NSString *)format, ...
{
	@synchronized(self)
	{
		if(currentLogger != nil || !overridden)
		{
			va_list ap;
    		va_start(ap, format);

			NSString *message = AUTORELEASE([[NSString alloc] initWithFormat:format arguments:ap]);

			if(currentLogger != nil)
			{
				currentLogger(level, message);
			}
			else
			{
				NSLog(@"%@", message);
			}

			va_end(ap);
		}
	}
}

@end