package org.rspeer.runetek.providers;

import java.awt.*;

public interface RSGraphicsProvider extends RSProvider {
    void drawGame(Graphics g, int i, int i2);
	Image getImage();
}