package org.rspeer.runetek.providers;

public interface RSAxisAlignedBoundingBox extends RSBoundingBox {
    int getColor();
	int getMaxX();
	int getMaxY();
	int getMaxZ();
	int getMinX();
	int getMinY();
	int getMinZ();
}