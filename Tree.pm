package Tree;

use strict;
use warnings;

sub new {
	my $prot = shift;
	my $self = {
		'freq' => 0
	};
	return bless($self, (ref $prot) || $prot);
}

sub learn_ngram {
	my ($self, $word) = @_;
	$self->{'freq'}++;
	return if $word eq '';
	my $char = substr($word, -1);
	my $rest = substr($word, 0, -1);
	my $children = ($self->{'children'} ||= {});
	my $child = ($children->{$char} ||= new Tree());
	$child->learn_ngram($rest);
}

sub learn {
	my ($self, $word) = @_;
	my $prefix = '';
	for (split(//, $word)) {
		$prefix .= $_;
		$self->learn_ngram($prefix);
	}
}

sub child {
	my ($self, $name) = @_;
	my $children = $self->{'children'};
	return $children && $children->{$name};
}

sub freq {
	my ($self, $word, $maxdepth) = @_;
	my ($freq, $depth) = ($self->{'freq'}, 0);
	if ($word ne '') {
		my $char = substr($word, -1);
		my $rest = substr($word, 0, -1);
		my $child = $self->child($char);
		if ($child && $maxdepth != 0) {
			($freq, $depth) = $child->freq($rest, $maxdepth-1);
			$depth++;
		}
	}
	return (wantarray) ? ($freq, $depth) : $freq;
}

sub prob {
	my ($self, $char, $prefix) = @_;
	my $rev = $prefix.$char;
	my ($freq1, $depth1) = $self->freq($rev, -1);
	if ($depth1 == 0) {
		print STDERR "couldn't find '$rev'\n";
		return 0; 
	}
	my ($freq2, $depth2) = $self->freq($prefix, $depth1-1);
	if ($depth1-1 != $depth2) {
		print STDERR "Uh-Oh! depths differ (found '$rev' at depth $depth1, '$prefix' at depth $depth2)\n";
	}
	return -log($freq1/$freq2);
}

sub leaves {
	my ($self, $prefix) = @_;
	$prefix ||= '';
	my $children = $self->{'children'};
	unless ($children && keys %$children) {
		 return $prefix;
	}
	my @leaves;
	for (keys %$children) {
		push(@leaves, $children->{$_}->leaves($_.$prefix));
	}
	return sort @leaves;
}


1;