package org.rspeer.runetek.providers;

public interface RSReferenceTable extends RSProvider {
    byte[] unpack(int i, int i2, int[] ia);
	RSIdentityTable getEntry();
	RSIdentityTable[] getChildren();
	Object[][] getBuffer();
}