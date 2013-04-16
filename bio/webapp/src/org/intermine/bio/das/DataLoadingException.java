package org.intermine.bio.das;

public class DataLoadingException extends Exception {

    private static final long serialVersionUID = -9084064988632600505L;

    public DataLoadingException(Throwable cause) {
        super("Error loading data", cause);
    }
}
