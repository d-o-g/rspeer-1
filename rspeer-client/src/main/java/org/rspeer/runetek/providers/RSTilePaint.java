package org.rspeer.runetek.providers;

public interface RSTilePaint extends RSProvider {
    int getRgb();

    void setRgb(int rgb);

    int getTexture();

    boolean isFlatShade();
}