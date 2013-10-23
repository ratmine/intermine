#!/usr/bin/perl
# rgd-qtls-to-xml.pl
# purpose: to create a target items xml file for intermine from RGD FTP file
# the script dumps the XML to STDOUT, as per the example on the InterMine wiki
# However, the script also creates a gff3 file to the location specified

#use warnings;
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

my ($model_file, $qtls_file, $qtl_xml, $taxon, $help);

GetOptions(
	'model=s' => \$model_file,
	'qtl_input=s' => \$qtls_file,
	'xml_output=s' => \$qtl_xml,
	'taxonId=s' => \$taxon,
	'help' => \$help);

if($help or !$model_file or !(-e $model_file))
{
	print "\nrgd-qtls-to-xml.pl\n";
	print "Convert the QTLS_RAT flat file from RGD into InterMine XML\n";
	print "rgd-qtls-to-xml.pl --model model.xml --qtl_input QTLS_RAT --xml_output qtls.xml --taxonId 10116/10090/9606 \n";
	exit(0);
}

my $data_source = 'Rat Genome Database';
my $taxon_id = $taxon;
print Dumper($taxon, $taxon_id);


my %pubs;
my %genes;
my %strains;

# The item factory needs the model so that it can check that new objects have
# valid classnames and fields
my $model = new InterMine::Model(file => $model_file);
my $item_doc = new InterMine::Item::Document(model => $model, output => $qtl_xml, auto_write => 1);

####
#User Additions
my $org_item = $item_doc->add_item('Organism', 'taxonId' => $taxon_id);
my $so_item_gene = $item_doc->make_item('SOTerm', name => 'gene');
my $so_item_qtl = $item_doc->make_item('SOTerm', name => 'QTL');

my $chrom_items;
if($taxon_id eq "10116"){
	$chrom_items = RCM::addRatChromosomes($item_doc, $org_item);
}elsif($taxon_id eq "10090"){
	$chrom_items = RCM::addMouseChromosomes($item_doc, $org_item);
}elsif($taxon_id eq "9606"){
	$chrom_items = RCM::addHumanChromosomes($item_doc, $org_item);
}


# read the genes file
open(my $QTLS, '<', $qtls_file);
my $index;
my $count = 0;
while(<$QTLS>)
{
	chomp;
	if(/^\D/) #parses header line
	{
		$index = &RCM::parseHeader($_);
		next;
	}

	my @fields = split(/\t/);
   	my %qtl_info = zip(@$index, @fields);

	#print Dumper(@fields);

	my %qtl_attr = ( organism => $org_item,
					primaryIdentifier => "RGD:".$qtl_info{QTL_RGD_ID},
					secondaryIdentifier => "RGD:".$qtl_info{QTL_RGD_ID},
					symbol => $qtl_info{QTL_SYMBOL},
					name => $qtl_info{QTL_NAME});
	
	print Dumper($qtl_info{QTL_RGD_ID}, $qtl_info{QTL_SYMBOL}, $qtl_info{LOD}, $qtl_info{TRAIT_NAME});

	$qtl_attr{lod} = $qtl_info{LOD} if $qtl_info{LOD};
	$qtl_attr{pValue} = $qtl_info{P_VALUE} if $qtl_info{P_VALUE};
	$qtl_attr{trait} = $qtl_info{TRAIT_NAME} if $qtl_info{TRAIT_NAME};		
	#$qtl_item->set('synonyms', [$syn_item, $syn_item2]);

	#print Dumper($qtl_info{3_4_MAP_POS_CHR});	
	#my $chrom = $chrom_items->{$qtl_info{3_4_MAP_POS_CHR}};
	#$qtl_attr{chromosome} = $chrom if $chrom;
	
	
	#Add Publications
	if (my $ids = $qtl_info{CURATED_REF_PUBMED_ID}) 
	{
      	for my $id (split(/;/, $ids))
		{
			$pubs{$id} = $item_doc->add_item('Publication', pubMedId => $id) unless ($pubs{$id});
			push @{$qtl_attr{publications}}, $pubs{$id};
		}
	}
	
	#Add Strains
	if (my $ids = $qtl_info{STRAIN_RGD_IDS}) 
	{
      	for my $id (split(/;/, $ids))
		{
			$strains{$id} = $item_doc->add_item('Strain', primaryIdentifier => "RGD:".$id) unless ($strains{$id});
			push @{$qtl_attr{strains}}, $strains{$id};
		}
	}
	
	#Add Candidate Genes
	if (my $ids = $qtl_info{CANDIDATE_GENE_RGD_IDS}) 
	{
      	for my $id (split(/;/, $ids))
		{
			$genes{$id} = $item_doc->add_item('Gene', 
								primaryIdentifier => "RGD:".$id, 
								secondaryIdentifier => "RGD:".$id, 
								sequenceOntologyTerm => $so_item_gene) unless ($genes{$id});
			push @{$qtl_attr{candidateGenes}}, $genes{$id};
		}
	}
	
	#my $qtl_item = $item_doc->add_item('QTL',  sequenceOntologyTerm => $so_item_qtl, %qtl_attr);
	my $qtl_item = $item_doc->make_item("QTL");
	$qtl_item->set(sequenceOntologyTerm => $so_item_qtl);
	$qtl_item->set(%qtl_attr);
	
	
	if($taxon_id eq "10116"){
		
		#print Dumper($qtl_info{'3_4_MAP_POS_CHR'}, $qtl_info{'3_4_MAP_POS_START'});
	        my $chrom = "NA";
		$chrom = $qtl_info{CHROMOSOME_FROM_REF} if ($qtl_info{CHROMOSOME_FROM_REF});

		if($qtl_info{'3_4_MAP_POS_CHR'}){
			$chrom = $chrom_items->{$qtl_info{'3_4_MAP_POS_CHR'}};
        		$qtl_attr{chromosome} = $chrom;
		}

		#if( ($qtl_info{'3_4_MAP_POS_START'}) && ($qtl_info{'3_4_MAP_POS_START'} <= {'3_4_MAP_POS_STOP'}) ){
		if( $qtl_info{'3_4_MAP_POS_START'} ){
			my($start, $end) = ($qtl_info{'3_4_MAP_POS_START'}, $qtl_info{'3_4_MAP_POS_STOP'});
			if($start>$end){
				my $temp = $start;
				$start = $end;
				$end = $temp;
			}
			$item_doc->add_item( 'Location',
								locatedOn => $chrom,
								start => $start,
								end => $end,
								feature => $qtl_item);
		}
	}elsif($taxon_id eq "10090"){
		
		#print Dumper($qtl_info{'37_MAP_POS_CHR'}, $qtl_info{'37_MAP_POS_START'});
		my $chrom = "NA";
		$chrom = $qtl_info{CHROMOSOME_FROM_REF} if ($qtl_info{CHROMOSOME_FROM_REF});

                if($qtl_info{'37_MAP_POS_CHR'}){
                        $chrom = $chrom_items->{$qtl_info{'37_MAP_POS_CHR'}};
                        $qtl_attr{chromosome} = $chrom;
                }


		#if( ($qtl_info{'37_MAP_POS_START'}) && ($qtl_info{'37_MAP_POS_START'} < {'37_MAP_POS_STOP'}) ){
                if( $qtl_info{'37_MAP_POS_START'} ){
		        my($start, $end) = ($qtl_info{'37_MAP_POS_START'}, $qtl_info{'37_MAP_POS_STOP'});

                        if($start>$end){
				my $temp = $start;
                                $start = $end;
                                $end = $temp;
                        }

			$item_doc->add_item( 'Location',
                                                                locatedOn => $chrom,
                                                                start => $start,
                                                                end => $end,
                                                                feature => $qtl_item);
                }
	}elsif($taxon_id eq "9606"){
                
                #print Dumper($qtl_info{'36_MAP_POS_CHR'}, $qtl_info{'36_MAP_POS_START'});
		my $chrom = "NA";
		print Dumper("&&&");
		print Dumper($chrom);

		$chrom = $qtl_info{CHROMOSOME_FROM_REF} if ($qtl_info{CHROMOSOME_FROM_REF});
		print Dumper("***");
                print Dumper($qtl_info{CHROMOSOME_FROM_REF});

                if($qtl_info{'36_MAP_POS_CHR'}){
                        $chrom = $chrom_items->{$qtl_info{'36_MAP_POS_CHR'}};
                        $qtl_attr{chromosome} = $chrom;
                }
		print Dumper("###");
		print Dumper($qtl_info{'36_MAP_POS_CHR'});

                #if( ($qtl_info{'36_MAP_POS_START'}) && ($qtl_info{'36_MAP_POS_START'} <= {'36_MAP_POS_STOP'}) ){
                if( $qtl_info{'36_MAP_POS_START'} ){
		        my($start, $end) = ($qtl_info{'36_MAP_POS_START'}, $qtl_info{'36_MAP_POS_STOP'});
			if($start>$end){
                                my $temp = $start;
                                $start = $end;
                                $end = $temp;
                        }

                        $item_doc->add_item( 'Location',
                                                                locatedOn => $chrom,
                                                                start => $start,
                                                                end => $end,
                                                                feature => $qtl_item );
                }
        }

	
	my $syn_item = $item_doc->add_item('Synonym', value => $qtl_info{QTL_SYMBOL},
											subject => $qtl_item);
										
	my $syn_item2 = $item_doc->add_item('Synonym', value => $qtl_info{QTL_NAME},
											subject => $qtl_item);	

}#end while
close $QTLS;
$item_doc->close();
