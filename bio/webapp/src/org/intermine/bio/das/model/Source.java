package org.intermine.bio.das.model;

public class Source
{
    private String uri, title, docHref, description;

    private List<Version> versions = new ArrayList<Version>();
    
    public Source(String uri, String title, String docHref, String desc, Collection<Version> versions) {
        this.uri = uri;
        this.title = title;
        this.docHref = docHref;
        this.description = desc;
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

    public Collection<Version> getVersions() {
        return Collection.unmodifiableList(versions);
    }
}
