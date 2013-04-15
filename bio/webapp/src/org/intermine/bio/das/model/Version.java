package org.intermine.bio.das.model;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class Version
{

    private String uri, created;

    private List<Coordinates> coords = new ArrayList<Coordinates>();
    private List<Capability> capabilities = new ArrayList<Capability>();

    public Version(String uri, String created,
            Collection<Coordinates> coords, Collection<Capability> capabilities) {
        this.uri = uri;
        this.created = created;
        this.coords.addAll(coords);
        this.capabilities.addAll(capabilities);
    }

    public String getURI() {
        return uri;
    }

    public String getCreated() {
        return created;
    }

    public Collection<Coordinates> getCoordinates() {
        return Collection.unmodifiableList(coords);
    }

    public Collection<Capability> getCapabilities() {
        return Collection.unmodifiableList(capabilities);
    }
}
