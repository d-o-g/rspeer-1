package org.rspeer.runetek.providers;

public interface RSLinkedList extends RSProvider {
    RSNode getSentinel();
    RSNode getTail();
}