package org.rspeer.runetek.api.query.results;

import org.rspeer.runetek.adapter.component.InterfaceComponent;

import java.util.Collection;

public final class InterfaceComponentQueryResults extends QueryResults<InterfaceComponent, InterfaceComponentQueryResults> {

    public InterfaceComponentQueryResults(Collection<? extends InterfaceComponent> results) {
        super(results);
    }
}