#! /usr/bin/perl

use strict;
use warnings;

use Storable;

use Tree;


unless (@ARGV) {
	print STDERR "Usage: $0 <freqfile>\n\n";
	exit(1);
}

my $freqs = retrieve shift;

my $old = '';
for($freqs->leaves) {
	if (index($_, $old) == -1) {
		print "$old\n";
	}
	$old = $_;
}