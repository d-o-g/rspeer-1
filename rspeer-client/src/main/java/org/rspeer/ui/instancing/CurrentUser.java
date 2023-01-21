/*
 * Created by JFormDesigner on Sat Aug 04 13:10:18 CDT 2018
 */

package org.rspeer.ui.instancing;

import javax.swing.*;
import java.awt.*;

/**
 * @author RSPeer
 */
public final class CurrentUser extends JFrame {

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - RSPeer
    private JLabel username;
    private JLabel email;
    private JLabel balance;
    private JLabel instances;
    private JLabel groups;
    private JLabel ip;

    public CurrentUser() {
        initComponents();
    }

    public JLabel getEmail() {
        return email;
    }

    public JLabel getUsername() {
        return username;
    }

    public JLabel getBalance() {
        return balance;
    }

    public JLabel getInstances() {
        return instances;
    }

    public JLabel getGroups() {
        return groups;
    }

    public JLabel getIp() {
        return ip;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - RSPeer
        username = new JLabel();
        email = new JLabel();
        balance = new JLabel();
        instances = new JLabel();
        groups = new JLabel();
        ip = new JLabel();

        //======== this ========
        setTitle("RSPeer");
        setMinimumSize(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //---- username ----
        username.setText("Username");
        username.setFont(username.getFont().deriveFont(username.getFont().getSize() + 4f));

        //---- email ----
        email.setText("Email");
        email.setFont(email.getFont().deriveFont(email.getFont().getSize() + 4f));

        //---- balance ----
        balance.setText("Token Balance");
        balance.setFont(balance.getFont().deriveFont(balance.getFont().getSize() + 4f));

        //---- instances ----
        instances.setText("Allowed Instances");
        instances.setFont(instances.getFont().deriveFont(instances.getFont().getSize() + 4f));
        instances.setToolTipText("The amount of allowed RSPeer clients you can open.");

        //---- groups ----
        groups.setText("Groups");
        groups.setFont(groups.getFont().deriveFont(groups.getFont().getSize() + 4f));

        ip.setText("IP");
        ip.setFont(ip.getFont().deriveFont(ip.getFont().getSize() + 4f));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(email, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout.createParallelGroup()
                                                        .addComponent(instances, GroupLayout.PREFERRED_SIZE, 402, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(groups, GroupLayout.PREFERRED_SIZE, 402, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(ip, GroupLayout.PREFERRED_SIZE, 402, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(balance, GroupLayout.PREFERRED_SIZE, 402, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE))
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(username, GroupLayout.PREFERRED_SIZE, 402, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 22, Short.MAX_VALUE)
                                                .addComponent(ip, GroupLayout.PREFERRED_SIZE, 402, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 22, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(username)
                                .addGap(18, 18, 18)
                                .addComponent(email)
                                .addGap(18, 18, 18)
                                .addComponent(balance)
                                .addGap(18, 18, 18)
                                .addComponent(instances)
                                .addGap(18, 18, 18)
                                .addComponent(groups)
                                .addGap(18, 18, 18)
                                .addComponent(ip)
                                .addContainerGap(26, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
