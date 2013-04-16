package org.intermine.bio.das.command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.intermine.api.InterMineAPI;
import org.intermine.api.query.PathQueryExecutor;
import org.intermine.api.results.ExportResultsIterator;
import org.intermine.api.results.ResultElement;
import org.intermine.bio.das.Command;
import org.intermine.bio.das.DASRequest;
import org.intermine.bio.das.DataLoadingException;
import org.intermine.bio.das.model.Sequence;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.Query;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.PathConstraintRange;
import org.intermine.pathquery.PathQuery;
import org.jfree.util.Log;

public class SequenceCommand implements Command {

    private Collection<String> segments = new HashSet<String>();
    private String dataSource;
    private Collection<Sequence> sequences = new ArrayList<Sequence>();

    SequenceCommand() {
        // empty constructor. For testing.
    }

    public SequenceCommand(DASRequest arguments) {
        this.segments.addAll(arguments.getAll("segment"));
        this.dataSource = arguments.getDataSource();
    }

    public void loadData(InterMineAPI api) throws DataLoadingException {
        PathQuery pq = buildQuery(api.getModel());
        PathQueryExecutor pqe = api.getPathQueryExecutor();
        ExportResultsIterator results = pqe.execute(pq);
        while (results.hasNext()) {
            List<ResultElement> row = results.next();
            Sequence s = new Sequence(
                String.valueOf(row.get(0).getField()),
                String.valueOf(row.get(1).getField()),
                String.valueOf(row.get(2).getField()),
                "dummyVersion", // TODO: Replace with SF.chromosome.version
                String.valueOf(row.get(3).getField()),
                String.valueOf(row.get(4).getField())
            );
            addSequence(s);
        }
    }

    private PathQuery buildQuery(Model model) {
        PathQuery pq = new PathQuery(model);
        pq.addViews(
            "SequenceFeature.primaryIdentifier",
            "SequenceFeature.chromosomeLocation.start",
            "SequenceFeature.chromosomeLocation.end",
            "SequenceFeature.symbol",
            "SequenceFeature.sequence.residues"
        );
        pq.addConstraint(
           new PathConstraintRange(
               "SequenceFeature.chromosomeLocation",
               ConstraintOp.OVERLAPS,
               new ArrayList<String>(segments)
           )
        );
        pq.addConstraint(
            Constraints.eq(
                "SequenceFeature.organism.name",
                dataSource.replaceAll("_", " ")
            )
        );
        Log.info("Query: " + pq.toXml());
        return pq;
    }

    void addSequence(Sequence sequence) {
        sequences.add(sequence);
    }

    @Override
    public void serve(OutputStream out) throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out);

        writer.writeStartDocument();

        writer.writeStartElement("DASSEQUENCE");
        for (Sequence seq: sequences) {
            writer.writeStartElement("SEQUENCE");

            writer.writeAttribute("id", seq.getId());
            writer.writeAttribute("start", seq.getStart());
            writer.writeAttribute("stop", seq.getStop());
            writer.writeAttribute("version", seq.getVersion());
            writer.writeAttribute("label", seq.getLabel());

            writer.writeCharacters(seq.getSequence().toString());

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
