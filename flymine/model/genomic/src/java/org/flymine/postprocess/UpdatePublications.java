package org.flymine.postprocess;

/*
 * Copyright (C) 2002-2004 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.intermine.dataloader.IntegrationWriter;
import org.intermine.dataloader.IntegrationWriterFactory;
import org.intermine.model.datatracking.Source;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.SingletonResults;
import org.intermine.util.SAXParser;
import org.intermine.util.DynamicUtil;
import org.intermine.util.StringUtil;

import org.intermine.model.InterMineObject;
import org.flymine.model.genomic.Publication;
import org.flymine.model.genomic.Author;

import org.apache.log4j.Logger;

/**
 * Class to fill in all publication information from pubmed
 * @author Mark Woodbridge
 */
public class UpdatePublications
{
    protected static final Logger LOG = Logger.getLogger(UpdatePublications.class);
    // see http://eutils.ncbi.nlm.nih.gov/entrez/query/static/esummary_help.html for details
    protected static final String ESUMMARY_URL =
        "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=";
    // number of summaries to retrieve per request
    protected static final int BATCH_SIZE = 50;
    // source name for IntegrationWriter
    protected static final String SOURCE_NAME = "pubmed";
    protected IntegrationWriter iw;

    /**
     * Constructor
     * @param iw the relevent IntegrationWriter
     */
    public UpdatePublications(IntegrationWriter iw) {
        this.iw = iw;
    }

    /**
     * Synchronize publications with pubmed using pmid
     * @throws Exception if an error occurs
     */
    public void execute() throws Exception {
        Map publications = new HashMap();
        for (Iterator i = getPublications().iterator(); i.hasNext();) {
            Publication publication = (Publication) i.next();
            publications.put(publication.getPubMedId(), publication);
            if (publications.size() == BATCH_SIZE || !i.hasNext()) {
                SAXParser.parse(new InputSource(getReader(publications.keySet())),
                                new Handler(publications));
                storePublications(publications.values());
                publications = new HashMap();
            }
        }
    }

    /**
     * Retrieve the publications to be updated
     * @return a List of publications
     * @throws ObjectStoreException if an error occurs
     */
    protected List getPublications() throws ObjectStoreException {
        Query q = new Query();
        QueryClass qc = new QueryClass(Publication.class);
        q.addFrom(qc);
        q.addToSelect(qc);
        return new SingletonResults(q, iw.getObjectStore(), iw.getObjectStore().getSequence());
    }

    /**
     * Obtain the pubmed esummary information for the publications
     * @param ids the pubMedIds of the publications
     * @return a Reader for the information
     * @throws Exception if an error occurs
     */
    protected Reader getReader(Set ids) throws Exception {
        return new BufferedReader(new InputStreamReader(new URL(ESUMMARY_URL + StringUtil
                                                                .join(ids, ",")).openStream()));
    }

    /**
     * Store a collection of publications
     * @param publications the publications
     * @throws ObjectStoreException if an error occurs
     */
    protected void storePublications(Collection publications) throws ObjectStoreException {
        Source mainSource = iw.getMainSource(SOURCE_NAME);
        Source skeletonSource = iw.getSkeletonSource(SOURCE_NAME);
        iw.beginTransaction();
        for (Iterator i = publications.iterator(); i.hasNext();) {
            Publication publication = (Publication) i.next();
            iw.store(publication, mainSource, skeletonSource);
            for (Iterator j = publication.getAuthors().iterator(); j.hasNext();) {
                iw.store((InterMineObject) j.next(), mainSource, skeletonSource);
            }
        }
        iw.commitTransaction();
        iw.close();
    }

    /**
     * Extension of DefaultHandler to handle an esummary for a publication
     */
    static class Handler extends DefaultHandler
    {
        Map publications;
        Publication publication;
        String name;
        StringBuffer characters;

        /**
         * Constructor
         * @param publications Map from pubMedId to publication that needs to be updated
         */
        public Handler(Map publications) {
            this.publications = publications;
        }
         
        /**
         * @see DefaultHandler#startElement
         */
        public void startElement(String uri, String localName, String qName, Attributes attrs)
            throws SAXException {
            if ("ERROR".equals(qName)) {
                throw new SAXException("esummary returned an error message - is the pmid valid?");
            } else if ("Id".equals(qName)) {
                name = "Id";
            } else {
                name = attrs.getValue("Name");
            }
            characters = new StringBuffer();
        }

        /**
         * @see DefaultHandler#characters
         */
        public void characters(char[] ch, int start, int length) throws SAXException {
            characters.append(new String(ch, start, length));
        }

        /**
         * @see DefaultHandler#endElement
         */
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("Id".equals(name)) {
                publication = (Publication) publications.get(characters.toString());
                publication.setAuthors(new ArrayList());
            } else if ("PubDate".equals(name)) {
                publication.setYear(new Integer(characters.toString().split(" ")[0]));
            } else if ("Source".equals(name)) {
                publication.setJournal(characters.toString());
            } else if ("Title".equals(name)) {
                publication.setTitle(characters.toString());
            } else if ("Volume".equals(name)) {
                publication.setVolume(characters.toString());
            } else if ("Issue".equals(name)) {
                publication.setIssue(characters.toString());
            } else if ("Pages".equals(name)) {
                publication.setPages(characters.toString());
            } else if ("Author".equals(name)) {
                Author author = (Author)
                    DynamicUtil.createObject(Collections.singleton(Author.class));
                author.setName(characters.toString());
                author.getPublications().add(publication);
                publication.getAuthors().add(author);
            }
            name = null;
        }
    }
    
    /**
     * Main method
     * @param args the arguments
     * @throws Exception if an error occurs
     */
    public static void main(String[] args) throws Exception {
        IntegrationWriter iw =
            IntegrationWriterFactory.getIntegrationWriter("integration.production");
        iw.setIgnoreDuplicates(true);
        List ids = new ArrayList();
        ids.add("10021333");
        for (Iterator i = ids.iterator(); i.hasNext();) {
            Publication publication = (Publication)
                DynamicUtil.createObject(Collections.singleton(Publication.class));
            publication.setPubMedId((String) i.next());
            iw.store(publication, iw.getMainSource("rnai"), iw.getSkeletonSource("rnai"));
        }
        new UpdatePublications(iw).execute();
    }
}
