package org.intermine.bio.das.model;

public class Capability
{

    private String feature, uri;

    public Capability(String feature, String uri) {
        this.feature = feature;
        this.uri = uri;
    }

    public String getType() {
        return String.format("das1:%s", feature);
    }

    public String getURI() {
        return uri;
    }
}
