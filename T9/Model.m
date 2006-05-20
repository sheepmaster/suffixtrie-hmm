//
//  Model.m
//  T9
//
//  Created by Bernhard Bauer on 19.05.06.
//  Copyright 2006 Black Sheep Software. All rights reserved.
//

#import "Model.h"
#import "Trie.h"

#include <math.h>

@implementation Model

- (id)initWithFrequencies:(Trie*)freq {
	if (self = [super init]) {
		frequencies = [freq retain];
	}
	return self;
}

- (void)dealloc {
	[frequencies release];
	[super dealloc];
}

- (double)countInTrie:(Trie*)trie minDepth:(unsigned int*)minDepth {
	int depth = 0;
	double count = 0.0;
	NSEnumerator* probsEnum = [probs objectEnumerator];
	NSEnumerator* textEnum = [text objectEnumerator];
	Trie* child;
	while ((child = [trie childForChar:(NSNumber*)[textEnum nextObject]]) || (depth < *minDepth)) {
		if (trie) count += [trie frequency];
		depth++;
		trie = child;
		count *= [(NSNumber*)[probsEnum nextObject] doubleValue];
	}
	if (trie) count += [trie frequencySum];
	*minDepth = depth;
	return count;
}

- (double)pushChar:(char)c {
	unsigned int depth = 0;
	NSNumber* ch = [NSNumber numberWithChar:c];
	double count2 = [self countInTrie:frequencies minDepth:&depth];
	double count1 = [self countInTrie:[frequencies childForChar:ch] minDepth:&depth];
	
	double p = count1/count2;
	
	[text insertObject:ch atIndex:0];
	[probs insertObject:[NSNumber numberWithDouble:p] atIndex:0];
	return -log(p);
}

- (double)pushString:(NSString*)s {
	const char* buffer = [s cString];
	int i;
	int length = [s cStringLength];
	double score = 0.0;
	for (i=-1; i<length; i++) {
		score += [self pushChar:buffer[i]];
	}
	return score;
}

- (void)popOneChar {
	[text removeObjectAtIndex:0];
	[probs removeObjectAtIndex:0];
}

- (void)popNumChars:(unsigned int)num {
	[text removeObjectsInRange:NSMakeRange(0, num)];
	[probs removeObjectsInRange:NSMakeRange(0, num)];
}

@end
