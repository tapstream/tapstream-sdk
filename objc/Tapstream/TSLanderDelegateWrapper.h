//
//  TSLanderDelegateWrapper.h
//  WordOfMouth
//
//  Created by Adam Bard on 2015-11-05.
//  Copyright © 2015 Tapstream. All rights reserved.
//

#ifndef TSLanderDelegateWrapper_h
#define TSLanderDelegateWrapper_h

#import "TSHelpers.h"

@interface TSLanderDelegateWrapper : NSObject<TSLanderDelegate>
@property(nonatomic, STRONG_OR_RETAIN) id<TSPlatform> platform;
@property(nonatomic, STRONG_OR_RETAIN) id<TSLanderDelegate> delegate;
- initWithPlatformAndDelegate:(id<TSPlatform>)platform delegate:(id<TSLanderDelegate>)delegate;
@end

#endif /* TSLanderDelegateWrapper_h */
