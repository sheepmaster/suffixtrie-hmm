#! /usr/bin/perl

use strict;
use warnings;

use Storable;
use Term::ReadLine;

use Model;

my $model;

my %keys = (
	'2' => [qw[a b c]], 
	'3' => [qw[d e f]], 
	'4' => [qw[g h i]], 
	'5' => [qw[j k l]], 
	'6' => [qw[m n o]], 
	'7' => [qw[p q r s]], 
	'8' => [qw[t u v]], 
	'9' => [qw[w x y z]]
);

sub words_rec {
	my $prefix = shift;
	my $score = shift;
	my @words;
	for (@{$keys{shift()}}) {
		my $word = $prefix.$_;
		my $new_score = $score + $model->push($_);
		if (@_) {
			push(@words, words_rec($word, $new_score, @_));
		} else {
			push(@words, [$word, $new_score]);
		}
		$model->pop;
	}
	return sort({ $a->[1] <=> $b->[1] } @words);
}

sub words {
	my $number = shift;

	return map {$_->[0]} words_rec('', 0, split(//, $number));
}

unless (@ARGV) {
	print STDERR "Usage: $0 <freqfile>\n\n";
	exit(1);
}

$model = new Model(retrieve shift);

my $term = new Term::ReadLine 'Word score';
my $prompt = "> ";
my $out = $term->OUT || \*STDOUT;
while ( defined ($_ = $term->readline($prompt)) ) {
	if (my ($number) = /([2-9]+)/) {
		print $out join(', ', words($number)), "\n";
		$term->addhistory($number);
	} else {
		my ($word) = /([a-zA-Z]+)/;
		print $out $model->push(lc $word), "\n";
		$model->pop(length $word);
		$term->addhistory($word);
	}
}

print $out "\n";