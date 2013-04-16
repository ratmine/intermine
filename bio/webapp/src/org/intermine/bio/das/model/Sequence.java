package org.intermine.bio.das.model;

public class Sequence {

    private String id, start, stop, version, label;

    private CharSequence sequence;

    public Sequence(String id, String start, String stop,
            String version, String label, CharSequence sequence) {
        this.id = id;
        this.start = start;
        this.stop = stop;
        this.version = version;
        this.label = label;
        this.sequence = sequence;
    }

    public String getId() {
        return id;
    }

    public String getStart() {
        return start;
    }

    public String getStop() {
        return stop;
    }

    public String getVersion() {
        return version;
    }

    public String getLabel() {
        return label;
    }

    public CharSequence getSequence() {
        return sequence;
    }

}
