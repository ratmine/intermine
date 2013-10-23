package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.apache.log4j.Logger;
import org.intermine.bio.io.gff3.GFF3Record;
import org.intermine.metadata.Model;
import org.intermine.util.StringUtil;
import org.intermine.xml.full.Item;

import java.util.*;

/**
 * A converter/retriever for the RgdGff3 dataset via GFF files.
 */

public class RgdGff3GFF3RecordHandler extends GFF3RecordHandler
{

    private static final String MOUSE_TAXON="10090";
    private static final String RAT_TAXON="10116";
    private static final String HUMAN_TAXON="9606";
    private Map<String, Item> geneMap = new HashMap<String, Item>();

    protected static final Logger LOG = Logger.getLogger(RgdGff3GFF3RecordHandler.class);

    /**
     * Create a new RgdGff3GFF3RecordHandler for the given data model.
     * @param model the model for which items will be created
     */
    public RgdGff3GFF3RecordHandler (Model model) {
        super(model);
    }
    




    private String getPrimaryIdentifier(GFF3Record record) {
        String primaryId = record.getId();

        List<String> dbxrefs = record.getDbxrefs();
        //List<String> dbxrefs = record.getAttributes().get("Dbxref");
        if (dbxrefs == null) return primaryId;

        String organism = getOrganism().getAttribute("taxonId").getValue();
        LOG.info("************************here is my organism.." + organism + "#############################");

        //Format changed. Ref to FlyReg.
        for (String xref: dbxrefs) {
                LOG.info("************************here is my xref.." + xref);
                String[] dbXrefList = StringUtil.split(xref, ",");
                for (String tag : dbXrefList) {
                    if (tag == null) {
                        //LOG.info("***************NULL tag:"+tag);
                        continue;
                    }
                    //LOG.info("*********************here is my dbxrefTag.." + tag);
                    tag = tag.trim();
                    //LOG.info("*********************here is trimmed tag.." + tag);
                	
		    if(tag.contains("OMIM")){
			LOG.info("***** found OMIM identiier.."+tag.substring(5));
		    }   
		 
		    if ((tag.contains("MGD")) && (organism.equals(MOUSE_TAXON))) {
                        LOG.info("**********************here is MGI id.." + tag.substring(4));
                        return tag.substring(4);
                    }else if((tag.contains("EntrezGene")) && (organism.equals(HUMAN_TAXON))){
			LOG.info("**********************here is EntrezGene id.." + tag.substring(11));
                        return tag.substring(11);
		    }else{
                        //LOG.info("************ NOT trimmed");
                    }
                }
        }
        return primaryId;
    }


/**
	private List<Item> getOmimRecords(GFF3Record record) {
        	String primaryId = record.getId();
        	List<String> dbxrefs = record.getDbxrefs();
        	if (dbxrefs == null) return primaryId;

        	String organism = getOrganism().getAttribute("taxonId").getValue();
        	LOG.info("************************here is my organism.." + organism + "#############################");
		
		List<Items> omimItems = new ArrayList<Items>();


        	for (String xref: dbxrefs) {
                	LOG.info("************************here is my xref.." + xref);
                	String[] dbXrefList = StringUtil.split(xref, ",");
                	for (String tag : dbXrefList) {
                    		if (tag == null) {
                        		//LOG.info("***************NULL tag:"+tag);
                        		continue;
                    		}
                    		//LOG.info("*********************here is my dbxrefTag.." + tag);
                    		tag = tag.trim();
                    		//LOG.info("*********************here is trimmed tag.." + tag);

                    		if(tag.contains("OMIM")){
                        		LOG.info("***** found OMIM identiier.."+tag.substring(5));
					Item omim = createItem("OMIM");
					omim.setAttribute("primaryIdentifier", tag.substring(5));
					omimItems.add(omim);
                    		}else{
                        		LOG.info("************ OMIM not found for primaryId:"+primaryId);
                    		}
                	}
        	}
        	
		if(omimItems!=null && omimItems.size()>0){
			return omimItems;	
		}else{
			return null;
		}
    	}
**/





    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GFF3Record record) {
        // This method is called for every line of GFF3 file(s) being read.  Features and their
        // locations are already created but not stored so you can make changes here.  Attributes
        // are from the last column of the file are available in a map with the attribute name as
        // the key.   For example:
        //
        //     Item feature = getFeature();
        //     String symbol = record.getAttributes().get("symbol");
        //     feature.setAttrinte("symbol", symbol);
        //
        // Any new Items created can be stored by calling addItem().  For example:
        // 
        //     String geneIdentifier = record.getAttributes().get("gene");
        //     gene = createItem("Gene");
        //     gene.setAttribute("primaryIdentifier", geneIdentifier);
        //     addItem(gene);
        //
        // You should make sure that new Items you create are unique, i.e. by storing in a map by
        // some identifier. 

        Item feature = getFeature();

	LOG.info("here is the name:"+record.getAttributes().get("fullName").get(0));

	feature.setAttribute("name", record.getAttributes().get("fullName").get(0));
        feature.setAttribute("description", record.getNote());

        String organism = getOrganism().getAttribute("taxonId").getValue();

        if (MOUSE_TAXON.equals(organism)) {
            String mgiId = getPrimaryIdentifier(record);
            LOG.info("^^^^^^^here is the mgiid:"+mgiId);
	    feature.setAttribute("primaryIdentifier", mgiId);
        }
	if(HUMAN_TAXON.equals(organism)){
	    String humanPrimaryId = getPrimaryIdentifier(record);
	    LOG.info("^^^^^^here is the entrez id:"+humanPrimaryId);
	    feature.setAttribute("primaryIdentifier", humanPrimaryId);
	}

	//List<Iterm> omimRecords = getOmimRecords(record);
	//if(omimRecords != null && omimRecords.size()>0){
	//	feature.setCollection(omimRecords);
	//}

    }



}
