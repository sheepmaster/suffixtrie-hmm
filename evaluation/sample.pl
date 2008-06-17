#! /usr/bin/perl

use strict;
use warnings;

unless (@ARGV) {
	print STDERR "Usage: $0 <sample percentage> <file(s)>\n";
	exit(1);
}
my $sample = shift;
$sample /= 100;

while (<>) {
	if (rand() < $sample) {
		print $_;
	} else {
		print STDERR $_;
	}
}