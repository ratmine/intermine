package org.intermine.bio.das;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

class RequestParser
{
    private static final Set<String> COMMANDS = new HashSet<String>(
        Arrays.asList("sources", "entry_points", "sequence"));

    DASRequest parse(HttpServletRequest raw) throws DASException {
        DASRequest processed = new DASRequest();
        String pathInfo = raw.getPathInfo().replaceAll("^/", "");
        String[] parts = pathInfo.split("/");
        if (parts.length == 1) {
            if (COMMANDS.contains(parts[0])) {
                processed.setCommand(parts[0]);
            } else {
                processed.setCommand("sources");
                processed.setDataSource(parts[0]);
            }
        } else if (parts.length == 2) {
            processed.setDataSource(parts[0]);
            processed.setCommand(parts[1]);
        } else {
            throw new DASException("Expected one or two path segments, not " + parts.length);
        }
        @SuppressWarnings("unchecked")
        Set<Entry<String, String[]>> params = raw.getParameterMap().entrySet();
        for (Entry<String, String[]> param: params) {
            for (String val: param.getValue()) {
                processed.addArgument(param.getKey(), val);
            }
        }
        return processed;
    }
}
