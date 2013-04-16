package org.intermine.bio.das;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.intermine.api.InterMineAPI;
import org.intermine.bio.das.command.SequenceCommand;
import org.intermine.bio.das.command.SourcesCommand;
import org.intermine.web.context.InterMineContext;

/**
 * A DAS server for mounting within the context of an intermine application.
 *
 * This servlet should be mounted at the standard <code>.../service/das</code> path
 * within the web application.
 *
 */
public class Server extends HttpServlet
{
    private static final Logger LOG = Logger.getLogger(Server.class);

    protected InterMineAPI api;

    private RequestParser requestParser = new RequestParser();

    @Override
    public void init() {
        api = InterMineContext.getInterMineAPI();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        LOG.info("Responding to " + request.getPathInfo());
        try {
            OutputStream out = response.getOutputStream();
            DASRequest input = requestParser.parse(request);
            Command command = null;
            if ("sources".equals(input.getCommand())) {
                command = new SourcesCommand(input);
            } else if ("sequence".equals(input.getCommand())) {
                command = new SequenceCommand(input);
            }
            command.loadData(api);
            command.serve(out);
        } catch (DASException e) {
            LOG.error("DASException", e);
        } catch (IOException e) {
            LOG.error("IOE", e);
        } catch (XMLStreamException e) {
            LOG.error("XSE", e);
        } catch (DataLoadingException e) {
            LOG.error("DLE", e);
        }
    }

}
