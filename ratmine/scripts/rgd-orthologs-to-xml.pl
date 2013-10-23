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

my ($model_file, $help, $input_file, $output_file);

GetOptions( 'model=s' => \$model_file,
			'input=s' => \$input_file,
			'output=s' => \$output_file,
			'help' => \$help);
			
if($help or !($model_file and $input_file))
{
	printHelp();
	exit(0);
}

my $model = new InterMine::Model(file => $model_file);
my $item_doc = new InterMine::Item::Document(model => $model, output => $output_file, auto_write => 1);

my $rat_id = '10116';
my $mouse_id = '10090';
my $human_id = '9606';

my $rat_item = $item_doc->add_item('Organism', taxonId => $rat_id);
my $mouse_item = $item_doc->add_item('Organism', taxonId => $mouse_id);
my $human_item = $item_doc->add_item('Organism', taxonId => $human_id);
my $so_item = $item_doc->make_item('SOTerm', name => 'gene');

#process Header

print "Processing Data...\n";
open(my $IN, '<', $input_file) or die "cannot open $input_file\n";

my $index;
my %mouse_genes;
my %human_genes;
my %sources;
my $header_flag = 0;
while(<$IN>)
{
	next if /^#/; #ignore comment lines
	chomp;
	unless($header_flag) #parses header line
	{
		$index = RCM::parseHeader($_);
		$header_flag = 1;
		next
	}
  
	my @fields = split(/\t/);
   	my %data = zip(@$index, @fields);

	my $rat_gene = $item_doc->add_item('Gene', 
												primaryIdentifier => "RGD:".$data{RAT_GENE_RGD_ID},
												secondaryIdentifier => "RGD:".$data{RAT_GENE_RGD_ID},
												organism => $rat_item,
												symbol => $data{RAT_GENE_SYMBOL},
												sequenceOntologyTerm => $so_item);

	my @mouse_ids = split(/\|/, $data{MOUSE_ORTHOLOG_RGD});
	my @mouse_source = split(/\|/, $data{MOUSE_ORTHOLOG_SOURCE});
	my @mouse_primaryIds = split(/\|/, $data{MOUSE_ORTHOLOG_MGI});
	my @mouse_entrezIds = split(/\|/, $data{MOUSE_ORTHOLOG_ENTREZ});
	my @mouse_symbols = split(/\|/, $data{MOUSE_ORTHOLOG_SYMBOL});

	for(my $mid=0; $mid<scalar(@mouse_ids); $mid++) {
                
		my $mouseId=$mouse_ids[$mid];
		unless($mouse_genes{$mouse_ids[$mid]})
                {
                        if($mouse_primaryIds[$mid]){
				$mouseId = $mouse_primaryIds[$mid];
			}elsif($mouse_entrezIds[$mid]){
				$mouseId = $mouse_entrezIds[$mid];
			}
			$mouse_genes{$mouse_ids[$mid]} = $item_doc->add_item('Gene', 
                                                                              	primaryIdentifier => $mouseId,
                                                                              	secondaryIdentifier => "RGD:".$mouse_ids[$mid],
                                                                              	organism => $mouse_item,
										symbol => $mouse_symbols[$mid],
										sequenceOntologyTerm => $so_item);
                }
                my $source = shift(@mouse_source);
                unless ($sources{$source}) {
                        $sources{$source} = $item_doc->add_item('DataSet', name => $source);
                }



                $item_doc->add_item('Homologue', gene => $rat_gene,
                                                                                homologue => $mouse_genes{$mouse_ids[$mid]},
                                                                                type => 'orthologue',
                                                                                dataSets => [$sources{$source}]);
                $item_doc->add_item('Homologue', gene => $mouse_genes{$mouse_ids[$mid]},
                                                                                homologue => $rat_gene,
                                                                                type => 'orthologue',
                                                                                dataSets => [$sources{$source}]);
        } #end for @mouse_ids


	my @human_ids = split(/\|/, $data{HUMAN_ORTHOLOG_RGD});
	my @human_source = split(/\|/, $data{HUMAN_ORTHOLOG_SOURCE});
	my @human_entrezIds = split(/\|/, $data{HUMAN_ORTHOLOG_ENTREZ});
	my @human_symbols = split(/\|/, $data{HUMAN_ORTHOLOG_SYMBOL});

	for (my $hid=0; $hid<scalar(@human_ids); $hid++) {

		my $humanId=$human_ids[$hid];

		unless($human_genes{$human_ids[$hid]})
		{

			if($human_entrezIds[$hid]){
                                $humanId = $human_entrezIds[$hid];
                        }


			$human_genes{$human_ids[$hid]} = $item_doc->add_item('Gene', 
												primaryIdentifier => $humanId,
												secondaryIdentifier => "RGD:".$human_ids[$hid],
												organism => $human_item,
												symbol => $human_symbols[$hid],
												sequenceOntologyTerm => $so_item);
		}
		my $source = shift(@human_source);

		unless ($sources{$source}) {
			$sources{$source} = $item_doc->add_item('DataSet', name => $source);
		}

		$item_doc->add_item('Homologue', gene => $rat_gene, 
										homologue => $human_genes{$human_ids[$hid]}, 
										type => 'orthologue',
										dataSets => [$sources{$source}]);
		$item_doc->add_item('Homologue', gene => $human_genes{$human_ids[$hid]}, 
										homologue => $rat_gene, 
										type => 'orthologue',
										dataSets => [$sources{$source}]);
	} #end for @human_ids

}#end while(<IN>)
close $IN;
$item_doc->close();

exit(0);

### Subroutines ###

sub printHelp
{
print<<HELP;
perl rgd-orthologs-to-xml.pl

arguments:
model	model file
input	RGD flat file
output	XML output file
help	prints this message
			
HELP
}
