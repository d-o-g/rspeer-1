/*
 * Created by JFormDesigner on Mon Aug 27 20:52:03 CDT 2018
 */

package org.rspeer.ui.startup;

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;

/**
 * @author RSPeer
 */
public class InstanceLimit extends JPanel {
    public InstanceLimit() {
        initComponents();
    }

    public JButton getPurchaseButton() {
        return purchaseButton;
    }

    public JLabel getStoreLink() {
        return storeLink;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - RSPeer
        panel1 = new JPanel();
        label4 = new JLabel();
        label3 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();
        label8 = new JLabel();
        label9 = new JLabel();
        label10 = new JLabel();
        storeLink = new JLabel();
        purchaseButton = new JButton();

        //======== this ========

        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setMinimumSize(new Dimension(765, 503));
            panel1.setOpaque(false);

            //---- label4 ----
            label4.setIcon(new ImageIcon(getClass().getResource("/logo.png")));

            //---- label3 ----
            label3.setText("You have currently hit your instance limit. ");
            label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 2f));

            //---- label5 ----
            label5.setText("Please close another running client to run this one.");
            label5.setFont(label5.getFont().deriveFont(label5.getFont().getSize() + 2f));

            //---- label6 ----
            label6.setText("What are instances and why have I hit the limit?");
            label6.setForeground(new Color(204, 204, 204));
            label6.setFont(label6.getFont().deriveFont(label6.getFont().getStyle() | Font.BOLD));

            //---- label7 ----
            label7.setText("We give 1 free instance to each user if you are not discord verified.");

            //---- label8 ----
            label8.setText("Instances are the amount of clients you are able to run.");

            //---- label9 ----
            //---- label10 ----
            label10.setText("To purchase more instances, click the button below or visit:");

            //---- storeLink ----
            storeLink.setText("https://forums.rspeer.org/topic/688/how-to-get-more-client-instances");
            storeLink.setForeground(new Color(0, 157, 186));

            //---- purchaseButton ----
            purchaseButton.setText("Get More Instances");

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addContainerGap(204, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(purchaseButton, GroupLayout.PREFERRED_SIZE, 370, GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addComponent(label4))
                            .addComponent(label3, GroupLayout.PREFERRED_SIZE, 376, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label5, GroupLayout.PREFERRED_SIZE, 370, GroupLayout.PREFERRED_SIZE)
                            .addComponent(storeLink)
                            .addComponent(label10)
                            .addComponent(label9)
                            .addComponent(label7)
                            .addComponent(label8, GroupLayout.PREFERRED_SIZE, 421, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label6))
                        .addGap(140, 140, 140))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(label4)
                        .addGap(18, 18, 18)
                        .addComponent(label3, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(label5, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(label6)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label8)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label7)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label9)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label10)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(storeLink)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(purchaseButton, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))
            );
        }
        add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - RSPeer
    private JPanel panel1;
    private JLabel label4;
    private JLabel label3;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JLabel label10;
    private JLabel storeLink;
    private JButton purchaseButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
