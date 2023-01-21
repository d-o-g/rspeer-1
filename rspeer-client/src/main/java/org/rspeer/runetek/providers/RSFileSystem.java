package org.rspeer.runetek.providers;

public interface RSFileSystem extends RSNode {
    RSCacheIndex getIndex();
	RSCacheReferenceTable getTable();
	byte[] getBuffer();
}