package org.rspeer.runetek.api.commons;

public interface Identifiable {

    int getId();

    // by default this throws an exception unless overriden
    // Consider: should this be abstracted into a Nameable interface?
    default String getName() {
        throw new UnsupportedOperationException();
    }
}
