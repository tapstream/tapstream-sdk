//
//  TSLanderDelegate.h
//  WordOfMouth
//
//  Created by Adam Bard on 2015-11-04.
//  Copyright © 2015 Tapstream. All rights reserved.
//

#ifndef TSLanderDelegate_h
#define TSLanderDelegate_h

#import <Foundation/Foundation.h>

@protocol TSLanderDelegate <NSObject>

- (void)showedLander:(NSUInteger)landerId;
- (void)dismissedLander;
- (void)submittedLander;

@end


#endif /* TSLanderDelegate_h */
