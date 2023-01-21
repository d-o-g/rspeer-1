package org.rspeer.runetek.providers;

public interface RSReferenceCache extends RSProvider {
    RSQueue getQueue();

	RSRS3CopiedNodeTable getTable();
}