#!/usr/bin/perl
# rgd-sslp-to-xml.pl
# purpose: to create a target items xml file for intermine from dbSNP Chromosome Report file
# the script dumps the XML to STDOUT, as per the example on the InterMine wiki
# However, the script also creates a gff3 file to the location specified

use strict;


BEGIN {
  push (@INC, ($0 =~ m:(.*)/.*:)[0] . '../../intermine/perl/InterMine-Util/lib');
}

use InterMine::Item::Document;
use InterMine::Model;
use InterMine::Util qw(get_property_value);
use IO qw(Handle File);
use Getopt::Long;
use lib '../perlmods';
use RCM;
use List::MoreUtils qw/zip/;
use Data::Dumper;

my ($model_file, $help, $input_file, $output_file);

GetOptions( 'model=s' => \$model_file,
			'input=s' => \$input_file,
			'output=s' => \$output_file,
			'help' => \$help);
			
if($help or !$model_file or ! -e $input_file)
{
	&printHelp;
	exit(0);
}

my $model = new InterMine::Model(file => $model_file);
my $item_doc = new InterMine::Item::Document(model => $model, output => $output_file, auto_write => 1);

my $taxon_id = '10116';
my $org_item = $item_doc->add_item('Organism', taxonId => $taxon_id);

open(my $IN, '<', $input_file) or die "cannot open $input_file\n";
#process Header
my $line = <$IN>;
#my @header = split(/\t/, $line);
my @header=[];
my %strains;
print "Processing Data...\n";
while(<$IN>)
{
	chomp;
	my @data=[];

	if($_!~/#/){
		if($_=~/RGD_ID/g){
			@header = split("\t", $_);
		}else{
			@data = split("\t", $_);
			
			#print Dumper(@header);
			#print "\nYESSS!!!\n";
			#print Dumper(@data);

			my $sslp_item;
			for(my $x = 0; $x < @data; $x++){
		#		print Dumper($data[$x]);
				if($x == 0){
					print "Set SSLP $data[$x]...\n";
					$sslp_item = $item_doc->add_item('SimpleSequenceLengthVariation', primaryIdentifier => "RGD:".$data[$x]);
				}
				#elsif($x > 1 and $data[$x] =~ /^\d+$/)
				elsif($x>1)
				{
					unless($strains{$header[$x]} ne "")
					{
						print "\nSet Strain...\t data is:\t$data[$x] and symbol is:\t $header[$x]\n";
						my $strain_item = $item_doc->add_item('Strain', symbol => $header[$x]);
						$strains{$header[$x]} = $strain_item;
					}
				
				#	print "Create Allele...\n";
					
					if($data[$x] eq ""){
						$data[$x] = "N/A";
                                        }					

					print "\nDEBUGGING: \t $strains{$header[$x]}\t$sslp_item\t###$data[$x]***\n";
					$item_doc->add_item('Allele', strain => $strains{$header[$x]}, sslv => $sslp_item, length => $data[$x]);
				}
			} #end for
		}
	} #end if/unless line matches '#'
} #end while
close $IN;
$item_doc->close();

### Subroutines ###

sub printHelp
{
	
	print <<HELP
perl rgd-sslp-alleles-to-xml.pl
	
model	path to model file
input	path to input data file
output	path to output xml
help	prints this message
				
HELP
}
