#! /usr/bin/perl

use strict;
use warnings;

use Trie;

use Storable;
use Getopt::Long;

my ($infile, $outfile);

unless (GetOptions(
	'infile|i=s' => \$infile, 
	'outfile|o=s' => \$outfile
) && ($infile || $outfile)) {
	print STDERR "Usage: $0 [--infile=<file> [--outfile=outfile]] <word files> ...\n\n";
	exit(1);
}

my $freqs = $infile ? (retrieve $infile) : (new Trie());

while (<>) {
	my $word;
	foreach $word(m/([a-zA-Z]+)/g) {
		$freqs->learn(lc $word);
	}
}

store $freqs, $outfile || $infile;

__DATA__
foo bar baz blurp foobie bletch klaus haus maus abracadabra hokus pokus