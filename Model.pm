package Model;

use strict;
use warnings;

use Trie;

sub new {
	my ($prot, $freqs) = @_;
	my $self = {
		'freqs' => $freqs, 
		'text' => [], 
		'p' => []
	};
	return bless($self, (ref $prot) || $prot);
}

sub calc {
	my ($self, $tree, $count, $depth, $mindepth) = @_;
	
	$count *= ($self->{'p'}[$depth-1] || 0);
	my $child;
	if ($depth < @{$self->{'text'}} and ($tree && ($child = $tree->child($self->{'text'}[$depth]))) || ($depth < $mindepth)) {
		$count += $tree->freq if $tree;
		return $self->calc($child, $count, $depth+1, $mindepth);
	} else {
		$count += $tree->freq_sum if $tree;
		return ($count, $depth);
	}
}

sub text {
	my $self = shift;
	return join('', reverse @{$self->{'text'}});
}

sub push {
	my ($self, $s) = @_;
	my ($char, $rest) = split(//, $s, 2);
	my $t = $self->{'freqs'};
	my ($c2, $depth2) = $self->calc($t, 0, 0);
	my ($c1, $depth1) = $self->calc($t->child($char), 0, 0, $depth2);
# 	my $text = $self->text;
# 	print STDERR "c($text$char): $c1 at depth $depth1; c($text): $c2 at depth $depth2\n";
	my $p = $c1/$c2;
	unshift(@{$self->{'p'}}, $p);
	unshift(@{$self->{'text'}}, $char);
	my $score = -log($p);
	if ($rest) {
		$score += $self->push($rest);
	}
	return $score;
}

sub pop {
	my ($self, $num) = @_;
	$num ||= 1;
	splice(@{$self->{'text'}}, 0, $num);
	splice(@{$self->{'p'}}, 0, $num);
}

1;