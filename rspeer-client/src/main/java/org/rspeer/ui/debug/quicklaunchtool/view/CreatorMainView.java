package org.rspeer.ui.debug.quicklaunchtool.view;

import org.rspeer.ui.component.*;
import org.rspeer.ui.debug.quicklaunchtool.controller.LaunchCreatorController;
import org.rspeer.ui.debug.quicklaunchtool.model.InstanceDataGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author MalikDz
 */

public class CreatorMainView extends BaseView<LaunchCreatorController, JFrame> implements ActionListener {

    private static final Dimension MAIN_FRAME_DIMENSION = new Dimension(700, 189);
    private static final Dimension BUTTON_DIMENSION = new Dimension(290, 30);
    private JFrame mainFrame;
    private JPanel accountsPanel, buttonsPanel;
    private JList<InstanceDataGroup> instanceDataList;
    private DefaultListModel<InstanceDataGroup> instanceDataModel;
    private JButton removeInstanceDataGroupBtn, addInstanceDataGroupBtn, clientsJsonFileBtn, launcherJsonFileBtn;

    public CreatorMainView(LaunchCreatorController controller) {
        setController(controller);
        initView();
    }

    private void initView() {
        mainFrame = new JFrame();
        mainFrame.setResizable(false);
        mainFrame.setLayout(new GridLayout(0, 2));
        mainFrame.setTitle("QuickLauncher generator");

        accountsPanel = new JPanel();
        instanceDataModel = new DefaultListModel<InstanceDataGroup>();
        instanceDataList = new JList<InstanceDataGroup>(instanceDataModel);
        JScrollPane listScrollPane = new JScrollPane(instanceDataList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listScrollPane.setPreferredSize(new Dimension(340, 140));
        accountsPanel.add(listScrollPane);
        mainFrame.add(accountsPanel);

        buttonsPanel = new JPanel();
        addInstanceDataGroupBtn = new JButton("Add QuickLauncher");
        addInstanceDataGroupBtn.setPreferredSize(BUTTON_DIMENSION);
        addInstanceDataGroupBtn.addActionListener(this);
        buttonsPanel.add(addInstanceDataGroupBtn);

        removeInstanceDataGroupBtn = new JButton("Remove selected Quicklauncher");
        removeInstanceDataGroupBtn.setPreferredSize(BUTTON_DIMENSION);
        removeInstanceDataGroupBtn.addActionListener(this);
        buttonsPanel.add(removeInstanceDataGroupBtn);

        launcherJsonFileBtn = new JButton("Create launcher QuickLaunch file");
        launcherJsonFileBtn.setPreferredSize(BUTTON_DIMENSION);
        launcherJsonFileBtn.addActionListener(this);
        buttonsPanel.add(launcherJsonFileBtn);
        mainFrame.add(buttonsPanel);

        clientsJsonFileBtn = new JButton("Create client Quicklaunch files");
        clientsJsonFileBtn.setPreferredSize(BUTTON_DIMENSION);
        clientsJsonFileBtn.addActionListener(this);
        buttonsPanel.add(clientsJsonFileBtn);

        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setPreferredSize(MAIN_FRAME_DIMENSION);
        mainFrame.pack();

        BotTitlePane.decorate(mainFrame);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

    }

    public DefaultListModel<InstanceDataGroup> getInstanceDataModel() {
        return instanceDataModel;
    }

    @Override
    public JFrame component() {
        return mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(removeInstanceDataGroupBtn)) {
            controller().removeDataGroup(instanceDataList.getSelectedValue());
        } else if (e.getSource().equals(addInstanceDataGroupBtn)) {
            controller().launchDataGroupForm();
        } else if (e.getSource().equals(clientsJsonFileBtn)) {
            controller().generateClientQuickLaunchFiles();
        } else if (e.getSource().equals(launcherJsonFileBtn)) {
            controller().generateLauncherQuickLaunchFile();
        }
    }
}
