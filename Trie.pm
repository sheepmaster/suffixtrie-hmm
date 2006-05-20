package Trie::Dummy;

use strict;
use warnings;

my $shared_instance;

sub new {
	my $prot = shift;
	unless ($shared_instance) {
		my $self;
		$shared_instance = bless(\$self, (ref $prot) || $prot);
	}
	return $shared_instance;
}

sub child {
	return shift;
}

use constant 'freq' => 0;
use constant 'is_empty' => 1;

package Trie;

use strict;
use warnings;

sub new {
	my $prot = shift;
	my $self = {
		'freq' => 0
	};
	return bless($self, (ref $prot) || $prot);
}

use constant 'is_empty' => 0;

sub learn_ngram {
	my ($self, $word) = @_;
	$self->{'freq_sum'}++;
	 if ($word eq '') {
		$self->{'freq'}++;
		return;
	}
	my $char = substr($word, -1);
	my $rest = substr($word, 0, -1);
	my $children = ($self->{'children'} ||= {});
	my $child = ($children->{$char} ||= new Trie());
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
	return shift->{'freq'};
}

sub freq_sum {
	return shift->{'freq_sum'};
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