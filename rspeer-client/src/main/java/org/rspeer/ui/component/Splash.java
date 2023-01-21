package org.rspeer.ui.component;

import org.rspeer.ui.commons.SwingResources;

import javax.swing.*;
import java.awt.*;

public final class Splash extends JPanel {

    private static final Color RED = new Color(0x004F8C);

    private int progress;
    private String state = "";

    public Splash() {
        setBackground(Color.BLACK);
        SwingResources.setStrictSize(this, 765, 503);
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        //TODO fix positioning
        int x = 220;
        int y = 220;
        int width = 304;
        int height = 34;
        g.setColor(RED);
        g.drawRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x + 1, y + 1, width - 2, height - 2);
        g.setColor(RED);
        g.fillRect(x + 2, y + 2, progress * 3, height - 3);
        g.setColor(Color.BLACK);
        g.fillRect(x + progress * 3, y + 2, width - 4 - progress * 3, height - 3);

        String message = state + " " + progress + "%";

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(message, 285, 242);
        repaint();
    }
}
