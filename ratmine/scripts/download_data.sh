#!/bin/bash

TEMP_DIR="/home/intermine/ratmine_src_data_mod/tmp/";
DOWNL_DIR="/home/intermine/ratmine_src_data_mod/";

wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/rattus_genes_go -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/homo_genes_go -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/mus_genes_go -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/gene_association.rgd.gz -p $TEMP_DIR
gunzip -v $TEMP_DIR/gene_association.gz
VAR1 = "sort -k 2,2 "$TEMP_DIR"rattus_genes_go > "$DOWNL_DIR"go-annotation/rattus_genes_go";
`$VAR1`;
VARa = "sort -k 2,2 "$TEMP_DIR"gene_association.rgd > "$DOWNL_DIR"go-annotation/gene_association.rgd";
`$VARa`;
VAR2 = "sort -k 2,2 "$TEMP_DIR"homo_genes_go > "$DOWNL_DIR"go-annotation/homo_genes_go";
`$VAR2`;
VAR3 = "sort -k 2,2 "$TEMP_DIR"mus_genes_go > "$DOWNL_DIR"go-annotation/mus_genes_go";
`$VAR3`;




wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/rattus_genes_mp -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/homo_genes_mp -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/mus_genes_mp -p $TEMP_DIR;
VAR1 = "sort -k 2,2 "$TEMP_DIR"rattus_genes_mp > "$DOWNL_DIR"mp-annotation/rattus_genes_mp";
`$VAR1`;
VAR2 = "sort -k 2,2 "$TEMP_DIR"homo_genes_mp > "$DOWNL_DIR"mp-annotation/homo_genes_mp";
`$VAR2`;
VAR3 = "sort -k 2,2 "$TEMP_DIR"mus_genes_mp > "$DOWNL_DIR"mp-annotation/mus_genes_mp";
`$VAR3`;



wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/rattus_genes_nbo -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/homo_genes_nbo -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/mus_genes_nbo -p $TEMP_DIR;
VAR1 = "sort -k 2,2 "$TEMP_DIR"rattus_genes_nbo > "$DOWNL_DIR"nbo-annotation/rattus_genes_nbo";
`$VAR1`;
VAR2 = "sort -k 2,2 "$TEMP_DIR"homo_genes_nbo > "$DOWNL_DIR"nbo-annotation/homo_genes_nbo";
`$VAR2`;
VAR3 = "sort -k 2,2 "$TEMP_DIR"mus_genes_nbo > "$DOWNL_DIR"nbo-annotation/mus_genes_nbo";
`$VAR3`;

wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/rattus_genes_pw -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/homo_genes_pw -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/mus_genes_pw -p $TEMP_DIR;
VAR1 = "sort -k 2,2 "$TEMP_DIR"rattus_genes_pw > "$DOWNL_DIR"pw-annotation/rattus_genes_pw";
`$VAR1`;
VAR2 = "sort -k 2,2 "$TEMP_DIR"homo_genes_pw > "$DOWNL_DIR"pw-annotation/homo_genes_pw";
`$VAR2`;
VAR3 = "sort -k 2,2 "$TEMP_DIR"mus_genes_pw > "$DOWNL_DIR"pw-annotation/mus_genes_pw";
`$VAR3`;

wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/rattus_genes_rdo -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/homo_genes_rdo -p $TEMP_DIR;
wget ftp://rgd.mcw.edu/pub/data_release/annotated_rgd_objects_by_ontology/mus_genes_rdo -p $TEMP_DIR;
VAR1 = "sort -k 2,2 "$TEMP_DIR"rattus_genes_rdo > "$DOWNL_DIR"rdo-annotation/rattus_genes_rdo";
`$VAR1`;
VAR2 = "sort -k 2,2 "$TEMP_DIR"homo_genes_rdo > "$DOWNL_DIR"rdo-annotation/homo_genes_rdo";
`$VAR2`;
VAR3 = "sort -k 2,2 "$TEMP_DIR"mus_genes_rdo > "$DOWNL_DIR"rdo-annotation/mus_genes_rdo";
`$VAR3`;

wget ftp://rgd.mcw.edu/pub/data_release/ontology_obo_files/pathway/pathway.obo
wget http://www.geneontology.org/ontology/obo_format_1_2/gene_ontology.1_2.obo
wget ftp://rgd.mcw.edu/pub/data_release/ontology_obo_files/disease/RDO.obo
wget ftp://ftp.informatics.jax.org/pub/reports/MPheno_OBO.ontology
wget ftp://rgd.mcw.edu/pub/data_release/ontology_obo_files/rat_strain/rat_strain.obo

