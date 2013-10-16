package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2013 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.util.FormattedTextParser;
import org.intermine.util.PropertiesUtil;

/**
 * ID resolver for RGD genes.
 *
 * @author Fengyuan Hu
 */
public class RgdHumanEntrezIdentifiersResolverFactory extends IdResolverFactory
{
    protected static final Logger LOG = Logger.getLogger(RgdHumanEntrezIdentifiersResolverFactory.class);

    // data file path set in ~/.intermine/MINE.properties
    // e.g. resolver.zfin.file=/micklem/data/rgd-identifiers/current/GENES_RAT.txt
    private final String propKey = "resolver.file.rootpath";
    private final String resolverFileSymbo = "rgd_human";
    public static final String TAXON_ID = "9606";

    /**
     * Construct with SO term of the feature type.
     */
    public RgdHumanEntrezIdentifiersResolverFactory() {
        this.clsCol = this.defaultClsCol;
    }

    /**
     * Construct with SO term of the feature type.
     * TODO as class name is fixed as gene, this method is not useful
     * @param soTerm the feature type to resolve
     */
    public RgdHumanEntrezIdentifiersResolverFactory(String clsName) {
        this.clsCol = new HashSet<String>(Arrays.asList(new String[] {clsName}));
    }

    @Override
    protected void createIdResolver() {
        if (resolver != null
                && resolver.hasTaxonAndClassName(TAXON_ID, this.clsCol
                        .iterator().next())) {
		LOG.info("***resolver is not null and has taxon and className:"+TAXON_ID+ this.clsCol);
            return;
        } else {
            if (resolver == null) {
                if (clsCol.size() > 1) {
                    resolver = new IdResolver();
			LOG.info("newIdResolver");
                } else {
                    resolver = new IdResolver(clsCol.iterator().next());
                }
            }
        }

        try {
            boolean isCachedIdResolverRestored = restoreFromFile();
		LOG.info("restoring cache idresolver File form HUmanEntrezIdResolverFactory");

            if (!isCachedIdResolverRestored || (isCachedIdResolverRestored
                    && !resolver.hasTaxonAndClassName(TAXON_ID, this.clsCol.iterator().next()))) {
                String resolverFileRoot =
                        PropertiesUtil.getProperties().getProperty(propKey);

                if (StringUtils.isBlank(resolverFileRoot)) {
                    String message = "Resolver data file root path is not specified";
                    LOG.warn(message);
                    return;
                }

                LOG.info("Creating id resolver from data file and caching it.");
                String resolverFileName = resolverFileRoot.trim() + resolverFileSymbo;
                File f = new File(resolverFileName);
                if (f.exists()) {
		    LOG.info("creating from File:"+resolverFileName);
                    createFromFile(f);
                    resolver.writeToFile(new File(ID_RESOLVER_CACHED_FILE_NAME));
                } else {
                    LOG.warn("Resolver file not exists: " + resolverFileName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createFromFile(File f) throws IOException {
        Iterator<?> lineIter = FormattedTextParser.
                parseTabDelimitedReader(new BufferedReader(new FileReader(f)));
	LOG.info("in createFromFile():"+f);
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
		LOG.info("here is the line with entrezid from gene_human.txt file:"+line[0]+" "+line[1]+" "+line[20]);

            if (line[0].startsWith("GENE_RGD_ID") || line[0].startsWith("#")) {
                continue;
            }

            String rgdId = "RGD:" + line[0];
            String symbol = line[1];
            String name = line[2];
            String entrez = line[20];
            String ensembl = line[37];

		LOG.info("here is the rgdid in rgdentrezresolverfactory:"+ rgdId + " " + entrez);
            
		if(entrez.length()>0){
	    		resolver.addMainIds(TAXON_ID, entrez, Collections.singleton(rgdId));
            		resolver.addMainIds(TAXON_ID, entrez, Collections.singleton(symbol));
	    		resolver.addMainIds(TAXON_ID, entrez, Collections.singleton(entrez));

            		Set<String> ensemblIds = parseEnsemblIds(ensembl);
            		resolver.addSynonyms(TAXON_ID, entrez, ensemblIds);

            		if (!StringUtils.isBlank(name)) {
                		resolver.addMainIds(TAXON_ID, entrez, Collections.singleton(name));
            		}

            		if (!StringUtils.isBlank(entrez)) {
                		resolver.addSynonyms(TAXON_ID, entrez, Collections.singleton(entrez));
            		}
		}
        }
    }

    private Set<String> parseEnsemblIds(String fromFile) {
        Set<String> ensembls = new HashSet<String>();
        if (!StringUtils.isBlank(fromFile)) {
            ensembls.addAll(Arrays.asList(fromFile.split(";")));
        }
        return ensembls;
    }
}
