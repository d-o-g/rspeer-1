package org.rspeer.script;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;

import java.awt.*;

public abstract class ScriptBlockingEvent implements RenderListener {

    protected final Script ctx;
    private boolean up = false;
    private boolean paint = false;
    private int alpha = 50;

    public ScriptBlockingEvent(Script ctx) {
        this.ctx = ctx;
    }

    public abstract boolean validate();

    public abstract void process();

    public void togglePaint(boolean paint) {
        this.paint = paint;
    }

    @Override
    public void notify(RenderEvent event) {
        if (!paint) {
            return;
        }

        Graphics g = event.getSource();
        if (alpha >= 70 && !up) {
            alpha--;
        } else {
            alpha++;
            up = alpha < 130;
        }
        g.setColor(new Color(200, 0, 255, !Game.isLoggedIn() ? 3 : alpha));
        g.fillRect(0, 0, 765, 503);
        g.setColor(Color.RED);
        ScriptMeta meta = getClass().getAnnotation(ScriptMeta.class);
        String name = meta == null ? getClass().getSimpleName() : meta.name() + " [by " + meta.developer() + "]";
        g.drawString("Processing event: " + name, 540, 20);
    }
}
