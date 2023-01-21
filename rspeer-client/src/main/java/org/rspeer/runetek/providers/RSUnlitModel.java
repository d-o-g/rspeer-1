package org.rspeer.runetek.providers;

public interface RSUnlitModel extends RSEntity {
    RSModel light(int i, int i2, int i3, int i4, int i5);
	byte getDefaultRenderPriority();
	int[] getXVertices();
	int[] getYVertices();
	int[] getZVertices();
}