package org.rspeer.runetek.providers;

import java.awt.image.BufferedImage;

public interface RSSprite extends RSProvider {

    int getHeight();

    int getWidth();

    int[] getPixels();

    default BufferedImage createImage() {
        int width = getWidth();
        int height = getHeight();
        int[] pixels = getPixels();

        int[] transformed = new int[pixels.length];
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] != 0) {
                transformed[i] = pixels[i] | 0xff000000;
            }
        }

        img.setRGB(0, 0, width, height, transformed, 0, width);
        return img;
    }
}