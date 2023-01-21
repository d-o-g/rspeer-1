/*
 * Created by JFormDesigner on Mon Aug 27 21:46:30 CDT 2018
 */

package org.rspeer.ui.startup;

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;

/**
 * @author RSPeer
 */
public class StartupFailure extends JPanel {
    public StartupFailure() {
        initComponents();
    }

    public JLabel getError() {
        return error;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - RSPeer
        panel1 = new JPanel();
        label4 = new JLabel();
        label1 = new JLabel();
        error = new JLabel();

        //======== this ========

        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setMinimumSize(new Dimension(765, 503));
            panel1.setOpaque(false);

            //---- label4 ----
            label4.setIcon(new ImageIcon(getClass().getResource("/logo.png")));

            //---- label1 ----
            label1.setText("Failed to load RuneScape.");
            label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 6f));

            //---- error ----
            error.setText("text");
            error.setForeground(new Color(255, 79, 123));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addContainerGap(271, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 245, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label4))
                        .addGap(249, 249, 249))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(154, 154, 154)
                        .addComponent(error, GroupLayout.PREFERRED_SIZE, 461, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(150, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(label4)
                        .addGap(18, 18, 18)
                        .addComponent(label1, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(error, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(74, Short.MAX_VALUE))
            );
        }
        add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - RSPeer
    private JPanel panel1;
    private JLabel label4;
    private JLabel label1;
    private JLabel error;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
