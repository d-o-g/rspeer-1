package org.rspeer.runetek.providers;

public interface RSSceneGraph extends RSProvider {

	void addBoundary(int i, int i2, int i3, int i4, RSEntity e, RSEntity e2, int i5, int i6, int i7, int i8);
	void addBoundaryDecor(int i, int i2, int i3, int i4, RSEntity e, RSEntity e2, int i5, int i6, int i7, int i8, int i9, int i10);
	boolean addEntityMarker(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, RSEntity e, int i9, boolean z, int i10, int i11);
	void addPickableDecor(int i, int i2, int i3, int i4, RSEntity e, int i5, RSEntity e2, RSEntity e3);
	void addTileDecor(int i, int i2, int i3, int i4, RSEntity e, int i5, int i6);
	RSEntityMarker[] getTempMarkers();
	RSTile[][][] getTiles();
}