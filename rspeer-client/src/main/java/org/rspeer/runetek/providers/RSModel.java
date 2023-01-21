package org.rspeer.runetek.providers;

public interface RSModel extends RSEntity {
    boolean isAabbEnabled();

    boolean isUseAABBBoundingBoxes();

    int[] getXTriangles();

    int[] getXVertices();

    int[] getYTriangles();

    int[] getYVertices();

    int[] getZTriangles();

    int[] getZVertices();

}