package org.rspeer.runetek.providers;

public interface RSLegacy2DBoundingBox extends RSBoundingBox {
    int getColor();
	int getMaxX();
	int getMaxY();
	int getMinX();
	int getMinY();
}