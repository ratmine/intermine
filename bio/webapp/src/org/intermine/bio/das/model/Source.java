package org.intermine.bio.das.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Source
{
    private String uri, title, docHref, description, maintainer;

    private List<Version> versions = new ArrayList<Version>();
    
    public Source(
            String uri, String title, String docHref, String desc, String maintainer,
            Collection<Version> versions) {
        this.uri = uri;
        this.title = title;
        this.docHref = docHref;
        this.description = desc;
        this.maintainer = maintainer;
        this.versions.addAll(versions);
    }

    public String getURI() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public String getDocHref() {
       return docHref;
    }

    public String getDescription() {
        return description;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public Collection<Version> getVersions() {
        return Collections.unmodifiableList(versions);
    }
}
