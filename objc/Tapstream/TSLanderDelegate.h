#ifndef TSLanderDelegate_h
#define TSLanderDelegate_h

#import <Foundation/Foundation.h>

@protocol TSLanderDelegate <NSObject>

- (void)showedLander:(NSUInteger)landerId;
- (void)dismissedLander;
- (void)submittedLander;

@end


#endif /* TSLanderDelegate_h */
