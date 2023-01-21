package org.rspeer.networking.dax.walker.store;

import com.allatori.annotations.DoNotRename;
import org.rspeer.networking.dax.walker.engine.definitions.PathLink;

import java.util.ArrayList;
import java.util.List;

@DoNotRename
public class DaxStore {

    public DaxStore() {
        this.pathLinks = new ArrayList<>(PathLink.getValues());
    }

    @DoNotRename
    private List<PathLink> pathLinks;

    @DoNotRename
    public void addPathLink(PathLink link) {
        this.pathLinks.add(link);
    }

    @DoNotRename
    public void removePathLink(PathLink link) {
        this.pathLinks.remove(link);
    }

    @DoNotRename
    public List<PathLink> getPathLinks() {
        return pathLinks;
    }
}
