//
//  Trie.h
//  T9
//
//  Created by Bernhard Bauer on 19.05.06.
//  Copyright 2006 Black Sheep Software. All rights reserved.
//

#import <Foundation/Foundation.h>

#define NUM_CHARS 26

@interface Trie : NSObject<NSCoding> {
	unsigned int freq;
	unsigned int freqSum;
	Trie* children[NUM_CHARS];
}

- (unsigned int)frequency;
- (unsigned int)frequencySum;
- (Trie*)childForChar:(const char)c;

- (void)learnNgramFrom:(const char*)start to:(const char*)end;
- (void)learnString:(NSString*)s;


@end
