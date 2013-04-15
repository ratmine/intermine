package org.intermine.bio.das.model;

public class Capability
{

    private String feature, uri;

    public String getType() {
        return String.format("das1:%s", feature);
    }

    public String getURI() {
        return uri;
    }
}
