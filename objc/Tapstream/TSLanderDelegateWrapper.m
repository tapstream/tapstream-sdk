//
//  TSLanderDelegateWrapper.m
//
//  Wraps TSLanderDelegate to record the lander being shown when it completes.
//
//  Created by Adam Bard on 2015-11-05.
//  Copyright © 2015 Tapstream. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TSLanderDelegate.h"
#import "TSPlatform.h"
#import "TSLanderDelegateWrapper.h"

@implementation TSLanderDelegateWrapper
- initWithPlatformAndDelegate:(id<TSPlatform>)platform delegate:(id<TSLanderDelegate>)delegate
{
	if(self = [super init])
	{
		self.platform = platform;
		self.delegate = delegate;
	}
	return self;
}
- (void)showedLander:(NSUInteger)landerId
{
	[self.platform setLanderShown:landerId];
	[self.delegate showedLander:landerId];
}
- (void)dismissedLander
{
	[self.delegate dismissedLander];
}
- (void)submittedLander
{
	[self.delegate dismissedLander];
}
@end