package org.intermine.bio.das;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

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

    protected final InterMineAPI api;

    private RequestParser requestParser = new RequestParser();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        DASRequest input = requestParser.parse(request);

    }

}
