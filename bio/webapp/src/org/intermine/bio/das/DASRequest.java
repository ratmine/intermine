package org.intermine.bio.das;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.intermine.bio.das.util.MultiGet;

public class DASRequest
    extends AbstractMap<String, String>
    implements MultiGet<String, String>
{
    private String dataSource, command;
    private Collection<Entry<String, String>> arguments =
            new ArrayList<Entry<String, String>>();

    DASRequest() {
        // default scope constructor.
    }

    DASRequest(Map<String, String> mappings) {
        for (Entry<String, String> pair: mappings.entrySet()) {
            addArgument(pair.getKey(), pair.getValue());
        }
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getCommand() {
        return command;
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return new HashSet<Map.Entry<String, String>>(arguments);
    }

    void setCommand(String command) {
        this.command = command;
    }

    void setDataSource(String src) {
        this.dataSource = src;
    }

    void addArgument(String name, String value) {
        arguments.add(new SimpleImmutableEntry(name, value));
    }

    @Override
    public Collection<String> getAll(String key) {
        Set<String> ret = new LinkedHashSet<String>();
        if (key != null) {
            for (Entry<String, String> pair: arguments) {
                if (key.equals(pair.getKey())) {
                    ret.add(pair.getValue());
                }
            }
        }

        return ret;
    }

}
