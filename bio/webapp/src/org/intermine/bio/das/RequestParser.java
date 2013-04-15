package org.intermine.bio.das;

class RequestParser
{

    DASRequest parse(HttpServletRequest raw) throws DASException {
        DASRequest processed = new DASRequest();
        String pathInfo = raw.getPathInfo().replaceAll("^/", "");
        String[] parts = pathInfo.split("/");
        if (parts.length == 1) {
            processed.setCommand(parts[0]);
        } else if (parts.length == 2) {
            processed.setDataSource(parts[0]);
            processed.setCommand(parts[1]);
        } else {
            throw new DASException("Expected one or two path segments, not " + parts.length);
        }
        for (Entry<String, String[]> params: raw.getParameterMap()) {
            for (String val: params.getValue()) {
                processed.addArgument(params.getKey(), val);
            }
        }
        return processed;
    }
}
