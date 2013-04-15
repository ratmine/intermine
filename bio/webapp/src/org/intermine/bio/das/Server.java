package org.intermine.bio.das;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.intermine.api.InterMineAPI;
import org.intermine.bio.das.command.Sources;
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
        try {
            OutputStream out = response.getOutputStream();
            DASRequest input = requestParser.parse(request);
            if ("sources".equals(input.getCommand())) {
                Sources command = new Sources(input);
                command.serve(out);
            }
        } catch (DASException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
