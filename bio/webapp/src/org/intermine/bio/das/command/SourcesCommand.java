package org.intermine.bio.das.command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.intermine.api.InterMineAPI;
import org.intermine.bio.das.Command;
import org.intermine.bio.das.model.Capability;
import org.intermine.bio.das.model.Coordinates;
import org.intermine.bio.das.model.Source;
import org.intermine.bio.das.model.Version;

public class SourcesCommand implements Command
{
    private String capability, type, authority, version, organism, label;
    private Collection<Source> sources = new ArrayList<Source>();

    public SourcesCommand(Map<String, String> request) {
        capability = request.get("capability");
        type = request.get("type");
        version = request.get("version");
        authority = request.get("authority");
        organism = request.get("organism");
    }

    public void add(Source source) {
        sources.add(source);
    }

    public void addAll(Collection<Source> srcs) {
        sources.addAll(srcs);
    }

    public void loadData(InterMineAPI api) {
        // TODO: stub.
    }

    public void serve(OutputStream out) throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out);

        writer.writeStartDocument();
        writer.writeProcessingInstruction("xml-stylesheet",
                "type=\"text/xsl\" href=\"das.xsl\"");
        writer.writeStartElement("SOURCES");

        for (Source source: sources) {
            writer.writeStartElement("SOURCE");
            writer.writeAttribute("uri", source.getURI());
            writer.writeAttribute("title", source.getTitle());
            writer.writeAttribute("doc_href", source.getDocHref());
            writer.writeAttribute("description", source.getDescription());

            writer.writeStartElement("MAINTAINER");
            writer.writeAttribute("email", source.getMaintainer());
            writer.writeEndElement();

            for (Version version: source.getVersions()) {
                writer.writeStartElement("VERSION");
                writer.writeAttribute("uri", version.getURI());
                writer.writeAttribute("created", version.getCreated());
                
                for (Coordinates coords: version.getCoordinates()) {
                    writer.writeStartElement("COORDINATES");
                    writer.writeAttribute("uri", coords.getURI());
                    writer.writeAttribute("source", coords.getDataType());
                    writer.writeAttribute("authority", coords.getAuthority());
                    writer.writeAttribute("taxid", coords.getTaxonId());
                    writer.writeAttribute("version", coords.getVersion());
                    writer.writeAttribute("test_range", coords.getTestRange());
                    writer.writeCharacters(coords.getCoordString());
                    writer.writeEndElement();
                }

                for (Capability capability: version.getCapabilities()) {
                    writer.writeStartElement("CAPABILITY");
                    writer.writeAttribute("type", capability.getType());
                    writer.writeAttribute("query_uri", capability.getURI());
                    writer.writeEndElement();
                }

                for (Entry<String, String> prop: version.getExtraProperties().entrySet()) {
                    writer.writeStartElement("PROP");
                    writer.writeAttribute("name", prop.getKey());
                    writer.writeAttribute("value", prop.getValue());
                    writer.writeEndElement();
                }

                writer.writeEndElement();
            }
            writer.writeEndElement();
        }

        writer.writeEndElement();
        writer.writeEndDocument();

        try {
            out.flush();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

