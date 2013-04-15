package org.intermine.bio.das.command;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.intermine.bio.das.DASRequest;
import org.intermine.bio.das.model.Source;
import org.intermine.bio.das.model.Version;
import org.intermine.bio.das.model.Coordinates;
import org.intermine.bio.das.model.Capability;

public class Sources
{
    private String capability, type, authority, version, organism, label;

    public Sources(DASRequest request) {
        capability = request.get("capability");
        type = request.get("type");
        version = request.get("version");
        authority = request.get("authority");
        organism = request.get("organism");
    }

    public void serve(OutputStream out) throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out);

        writer.writeStartDocument();
        writer.writeProcessingInstruction("xml-stylesheet",
                "type=\"text/xsl\" href=\"das.xsl\"");
        writer.writeStartElement("SOURCES");

        Collection<Source> sources = new ArrayList<Source>();

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

    }
}

