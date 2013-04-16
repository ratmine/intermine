package org.intermine.bio.das;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.intermine.api.InterMineAPI;

public interface Command {

    void loadData(InterMineAPI api) throws DataLoadingException;

    void serve(OutputStream out) throws XMLStreamException;
}
