package org.rspeer.ui.component;

import org.pushingpixels.substance.internal.ui.SubstanceRootPaneUI;
import org.pushingpixels.substance.internal.utils.border.SubstanceBorder;
import org.rspeer.Application;
import org.rspeer.commons.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Spencer on 26/01/2018.
 */
public final class BotTitlePane extends JMenuBar {

    private static final ImageIcon ICON = new ImageIcon(Application.class.getResource("/logo.png"));

    private BotTitlePane(Window owner) {
       /* SwingResources.setStrictSize(this, getWidth(), 30);

        add(Box.createHorizontalStrut(5));
        JLabel title = new JLabel(getTitle(owner));
        title.setFont(SwingResources.OPEN_SANS_BOLD);
        add(title);

        JButton close = new JButton(SwingResources.CLOSE_ICON);
        close.setFont(SwingResources.getFontAwesome(16));
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setBorder(null);
        close.setFocusPainted(false);
        close.setMargin(new Insets(5, 10, 5, 10));

        close.addActionListener(e -> owner.dispatchEvent(new WindowEvent(owner, WindowEvent.WINDOW_CLOSING)));

        JButton maximise = new JButton(SwingResources.MAXIMISE_ICON);
        maximise.setFont(SwingResources.getFontAwesome(16));
        maximise.setContentAreaFilled(false);
        maximise.setBorderPainted(false);
        maximise.setBorder(null);
        maximise.setFocusPainted(false);
        maximise.setMargin(new Insets(5, 10, 5, 10));

        maximise.addActionListener(e -> {
            if (owner instanceof JFrame) {
                JFrame f = (JFrame) owner;
                SubstanceRootPaneUI rpUI = (SubstanceRootPaneUI) ((JFrame) owner).getRootPane().getUI();
                rpUI.setMaximized();
                if ((f.getExtendedState() & 0x6) != 0) {
                    rpUI.setMaximized();
                    f.setExtendedState(f.getExtendedState() & 0xfffffff9);
                } else {
                    rpUI.setMaximized();
                    f.setExtendedState(f.getExtendedState() | 0x6);
                }
            }
        });

        JButton iconify = new JButton(SwingResources.ICONIFY_ICON);
        iconify.setFont(SwingResources.getFontAwesome(16));
        iconify.setContentAreaFilled(false);
        iconify.setBorderPainted(false);
        iconify.setBorder(null);
        iconify.setFocusPainted(false);
        iconify.setMargin(new Insets(5, 10, 5, 10));

        iconify.addActionListener(e -> {
            if (owner instanceof JFrame) {
                JFrame f = (JFrame) owner;
                f.setState(Frame.ICONIFIED);
            }
        });


        addHoverEffect(iconify, maximise, close);
        add(Box.createHorizontalGlue());
        add(iconify);

        if (owner instanceof JFrame && ((JFrame) owner).isResizable()) {
            add(maximise);
        }

        add(close);*/

        owner.setIconImage(ICON.getImage());
    }

    /**
     * Applies the title pane decoration to the given Window
     *
     * @param window The window to decorate
     */
    public static void decorate(Window window) {
        EventQueue.invokeLater(() -> {
            JRootPane root = null;
            if (window instanceof JFrame) {
                JFrame f = (JFrame) window;
                root = f.getRootPane();
            }

            if (window instanceof JDialog) {
                JDialog d = (JDialog) window;
                root = d.getRootPane();
            }

            if (root == null) {
                throw new IllegalStateException("Invalid window");
            }

            root.setBorder(new SubstanceBorder(0.1f));

            SubstanceRootPaneUI ui = (SubstanceRootPaneUI) root.getUI();
            new BotTitlePane(window);
        });
    }
}
