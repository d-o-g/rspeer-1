package org.rspeer.runetek.providers;

public interface RSMouseRecorder extends RSProvider {

    int getIndex();

    Object getLock();

    boolean isEnabled();

    int[] getXHistory();

    int[] getYHistory();

    long[] getTimeHistory();
}