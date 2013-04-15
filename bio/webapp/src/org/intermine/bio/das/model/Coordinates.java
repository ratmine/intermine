package org.intermine.bio.das.model;

public class Coordinates
{
    private String uri, dataType, authority, version, testRange, taxonid, species;

    public Coordinates(String uri, String dataType, String authority,
            String version, String testRange, String taxonid, String species) {
        this.uri = uri;
        this.dataType = dataType;
        this.authority = authority;
        this.version = version;
        this.testRange = testRange;
        this.taxonid = taxonid;
        this.species = species;
    }

    public String getURI() {
        return uri;
    }

    public String getDataType() {
        return dataType;
    }

    public String getAuthority() {
        return authority;
    }

    public String getTaxonId() {
        return taxonid;
    }

    public String getVersion() {
        return version;
    }

    public String getTestRange() {
        return testRange;
    }

    public String getCoordString() {
        return String.format("%s_%s,%s,%s", authority, version, dataType, species);
    }

}

