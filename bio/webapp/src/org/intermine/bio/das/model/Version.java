package org.intermine.bio.das.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Version
{

    private String uri, created;

    private List<Coordinates> coords = new ArrayList<Coordinates>();
    private List<Capability> capabilities = new ArrayList<Capability>();
    private Map<String, String> extraProperties = new HashMap<String, String>();

    public Version(String uri, String created,
            Collection<Coordinates> coords, Collection<Capability> capabilities,
            Map<String, String> properties) {

        this.uri = uri;
        this.created = created;

        this.coords.addAll(coords);
        this.capabilities.addAll(capabilities);
        this.extraProperties.putAll(properties);
    }

    public String getURI() {
        return uri;
    }

    public String getCreated() {
        return created;
    }

    public Collection<Coordinates> getCoordinates() {
        return Collections.unmodifiableList(coords);
    }

    public Collection<Capability> getCapabilities() {
        return Collections.unmodifiableList(capabilities);
    }

    public Map<String, String> getExtraProperties() {
        return Collections.unmodifiableMap(extraProperties);
    }
}
