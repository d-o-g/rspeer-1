package org.rspeer.ui.component;

import org.rspeer.runetek.api.scene.Projection;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Spencer on 04/02/2018.
 */
public final class BotSettings extends JFrame {

    public BotSettings() {
        setTitle("Settings");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel delaySettings = new JPanel();
        delaySettings.setLayout(new BorderLayout());
        JLabel tickLabel = new JLabel("Set engine tick delay");
        JSlider tickDelay = new JSlider(0, 200, 0);

        tickDelay.setValue(Projection.getTickDelay() >= 0 ? Projection.getTickDelay() : 0);
        tickDelay.setPaintTicks(true);
        tickDelay.setPaintLabels(true);
        tickDelay.setMajorTickSpacing(40);
        tickDelay.setOrientation(JSlider.HORIZONTAL);
        tickDelay.addChangeListener(e -> Projection.setTickDelay(tickDelay.getValue()));
        delaySettings.add(tickLabel, BorderLayout.NORTH);
        delaySettings.add(tickDelay, BorderLayout.SOUTH);

        JPanel renderingSettings = new JPanel();
        renderingSettings.setLayout(new BorderLayout());
        JCheckBox models = new JCheckBox("Disable model rendering");
        models.setSelected(!Projection.isModelRenderingEnabled());
        models.addActionListener(e -> Projection.setModelRenderingEnabled(!models.isSelected()));
        JCheckBox scenery = new JCheckBox("Disable scenery rendering");
        scenery.setSelected(!Projection.isLandscapeRenderingEnabled());
        scenery.addActionListener(e -> Projection.setLandscapeRenderingEnabled(!scenery.isSelected()));
        renderingSettings.add(models, BorderLayout.NORTH);
        renderingSettings.add(scenery, BorderLayout.SOUTH);

        add(delaySettings, BorderLayout.WEST);
        add(renderingSettings, BorderLayout.EAST);

        JButton lowCPU = new JButton("Click for low CPU");
        lowCPU.addActionListener(e -> {
            if (lowCPU.getText().equals("Click for low CPU")) {
                Projection.setLowCPUMode(true);
                lowCPU.setText("Click for super low CPU");
                models.setSelected(!Projection.isModelRenderingEnabled());
                scenery.setSelected(!Projection.isLandscapeRenderingEnabled());
                tickDelay.setValue(Projection.getTickDelay());
            } else if (lowCPU.getText().equals("Click for super low CPU")) {
                lowCPU.setText("Click to disable super low CPU");
                Projection.setTickDelay(90);
                models.setSelected(!Projection.isModelRenderingEnabled());
                scenery.setSelected(!Projection.isLandscapeRenderingEnabled());
                tickDelay.setValue(Projection.getTickDelay());
            } else if (lowCPU.getText().equals("Click to disable super low CPU")) {
                lowCPU.setText("Click for low CPU");
                Projection.setLowCPUMode(false);
                models.setSelected(!Projection.isModelRenderingEnabled());
                scenery.setSelected(!Projection.isLandscapeRenderingEnabled());
                tickDelay.setValue(Projection.getTickDelay());
            }
        });
        add(lowCPU, BorderLayout.SOUTH);

        BotTitlePane.decorate(this);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void display() {
        setVisible(true);
    }
}
