//
//  Trie.m
//  T9
//
//  Created by Bernhard Bauer on 19.05.06.
//  Copyright 2006 Black Sheep Software. All rights reserved.
//

#import "Trie.h"

#include <ctype.h>

static inline int indexForChar(char c) {
	return tolower(c)-'a';
}

static inline int charForIndex(int i) {
	return 'a'+i;
}

@implementation Trie

- (id)init {
	if (self = [super init]) {
		int i;
		for (i=0; i<NUM_CHARS; i++) {
			children[i] = nil;
		}
		freq = freqSum = 0;
	}
	return self;
}

- (id)initWithCoder:(NSCoder *)coder {
	self = [super initWithCoder:coder];
	int i;
	freqSum = freq = [coder decodeIntForKey:@"T9Freq"];
	for (i=0; i<NUM_CHARS; i++) {
		Trie* child = [coder decodeObjectForKey:[NSString stringWithFormat:@"T9Child%c", charForIndex(i)]];
		children[i] = [child retain];
		freqSum += [child frequencySum];
	}
	return self;
}

- (void)dealloc {
	int i;
	for (i=0; i<NUM_CHARS; i++) {
		[children[i] release];
		children[i] = nil;
	}
	[super dealloc];
}
	

- (void)encodeWithCoder:(NSCoder*)coder {
	[super encodeWithCoder: coder];
	[coder encodeInt:freq forKey:@"T9Freq"];
	int i;
	for (i=0; i<NUM_CHARS; i++) {
		[coder encodeObject:children[i] forKey:[NSString stringWithFormat:@"T9Child%c", charForIndex(i)]];
	}
}

- (unsigned int)frequency {
	return freq;
}

- (unsigned int)frequencySum {
	return freqSum;
}

- (Trie*)childForChar:(char)c {
	return children[indexForChar(c)];
}

- (void)learnNgramFrom:(char*)start to:(char*)end {
	freqSum++;
	if (start > end) {
		freq++;
		return;
	}
	int index = indexForChar(*end);
	Trie* child = children[index];
	if (child == nil) {
		children[index] = [[Trie alloc] init];
	}
	[child learnNgramFrom:start to:(end-1)];
}

- (void)learnString:(NSString*)s {
	const char* buffer = [s cString];
	int i;
	int length = [s cStringLength];
	for (i=-1; i<length; i++) {
		[self learnNgramFrom:buffer to:(buffer+i)];
	}
}

@end
