package org.rspeer.ui.debug.varpexlorer;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.component.BotTitlePane;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Yasper
 * <p>
 * The JFrame which contains the main panel. Relative to null to center should be bot frame.
 */
public final class VarpChangeFrame extends JFrame implements Runnable {

    private final VarpChangePanel panel;
    private boolean stop = false;

    public VarpChangeFrame() {
        setTitle("Varps Debugger");
        panel = new VarpChangePanel();
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        validate();
        setVisible(true);
        setResizable(false);
        BotTitlePane.decorate(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop = true;
                super.windowClosing(e);
            }
        });
        new Thread(this).start();
    }

    public void update(int[] varps) {
        panel.update(varps);
    }

    public void update(int index, int oldValue, int newValue) {
        panel.update(index, oldValue, newValue);
    }

    @Override
    public void run() {
        while (!stop) {
            update(Game.getClient().getVarps());
            Time.sleep(50);
        }
    }
}
