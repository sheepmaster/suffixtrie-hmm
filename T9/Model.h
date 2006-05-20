//
//  Model.h
//  T9
//
//  Created by Bernhard Bauer on 19.05.06.
//  Copyright 2006 Black Sheep Software. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Trie;

@interface Model : NSObject {
	Trie* frequencies;
	NSMutableArray* text;
	NSMutableArray* probs;
}

- (double)countInTrie:(Trie*)trie minDepth:(unsigned int*)minDepth;

- (double)pushChar:(char)c;
- (double)pushString:(NSString*)s;
- (void)popOneChar;
- (void)popNumChars:(unsigned int)num;

@end
