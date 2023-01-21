package org.rspeer.runetek.providers;

public interface RSNode extends RSProvider {
    long getKey();
    RSNode getNext();
    RSNode getPrevious();
}