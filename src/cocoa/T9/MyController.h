//
//  MyController.h
//  T9
//
//  Created by Bernhard Bauer on 19.05.06.
//  Copyright 2006 Black Sheep Software. All rights reserved.
//

#import <Cocoa/Cocoa.h>


@interface MyController : NSObject {
	IBOutlet NSTextView* screen;
}

- (IBAction)keyPressed:(id)sender;
- (IBAction)left:(id)sender;
- (IBAction)right:(id)sender;
- (IBAction)up:(id)sender;
- (IBAction)down:(id)sender;

@end
