#! /usr/bin/perl

use strict;
use warnings;

local $/;

my $text = <>;

while ($text =~ /([a-zA-Z]+)/g) {
	print "$1\n";
}