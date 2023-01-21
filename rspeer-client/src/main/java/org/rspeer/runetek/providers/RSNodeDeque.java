package org.rspeer.runetek.providers;

public interface RSNodeDeque<T extends RSNode> extends RSProvider, Iterable<T> {

    T current();

    T next();

    RSNode getHead();

    RSNode getTail();
}