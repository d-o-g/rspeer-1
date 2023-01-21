package org.rspeer.ui.debug.quicklaunchtool.view;

import org.rspeer.ui.debug.quicklaunchtool.controller.InstanceDataGroupFormController;
import org.rspeer.ui.debug.quicklaunchtool.view.component.ComponentTitledBorder;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author MalikDz
 */

public class InstanceDataGroupView extends BaseView<InstanceDataGroupFormController, JFrame> implements ActionListener {

    private static final int X_OFFSET = 50;
    private static final int Y_OFFSET = 120;
    private static final Dimension MAIN_FRAME_DIMENSION = new Dimension(860, 410);
    private JFrame mainFrame;
    ;
    private JCheckBox isSdnScriptCb;
    private JTextField tickDelayTf;
    private JButton createDataGroupButton;
    private JCheckBox usingProxyCb, editBotConfigCb;
    private JTextField proxyHostTf, proxyPortTf, proxyUserTf, proxyPassTf;
    private JTextField rsUserTf, rsPassTf, worldTf, scriptNameTf, scriptArgsTf;
    private JCheckBox disableSceneRenderingCb, disableModelRenderingCb, superLowCpuCb, lowCpuCb;

    public InstanceDataGroupView(InstanceDataGroupFormController controller) {
        setController(controller);
        initView();
    }

    private void initView() {
        mainFrame = new JFrame();
        mainFrame.setLayout(null);
        mainFrame.setSize(MAIN_FRAME_DIMENSION);
        mainFrame.setResizable(true);
        mainFrame.setTitle("Add a data group");
        mainFrame.setBackground(Color.BLACK);

        final JLabel rsUserLbl = new JLabel("RSUsername : ");
        rsUserLbl.setBounds(85 - X_OFFSET, 140 - Y_OFFSET, 130, 30);
        rsUserLbl.setOpaque(false);
        mainFrame.add(rsUserLbl);

        final JLabel rsPassLbl = new JLabel("RSPassword  : ");
        rsPassLbl.setBounds(85 - X_OFFSET, 175 - Y_OFFSET, 130, 30);
        rsPassLbl.setOpaque(false);
        mainFrame.add(rsPassLbl);

        final JLabel worldLbl = new JLabel("World : ");
        worldLbl.setBounds(85 - X_OFFSET, 210 - Y_OFFSET, 130, 30);
        worldLbl.setOpaque(false);
        mainFrame.add(worldLbl);

        final JLabel scriptNameLbl = new JLabel("Script name : ");
        scriptNameLbl.setBounds(85 - X_OFFSET, 245 - Y_OFFSET, 130, 30);
        mainFrame.add(scriptNameLbl);

        final JLabel scriptArgs = new JLabel("Script args : ");
        scriptArgs.setBounds(85 - X_OFFSET, 280 - Y_OFFSET, 130, 30);
        mainFrame.add(scriptArgs);

        final JLabel passwordLabel = new JLabel("Execute a sdn script:");
        passwordLabel.setBounds(85 - X_OFFSET, 315 - Y_OFFSET, 180, 30);
        mainFrame.add(passwordLabel);

        isSdnScriptCb = new JCheckBox();
        isSdnScriptCb.setBounds(200, 321 - Y_OFFSET, 100, 25);
        mainFrame.add(isSdnScriptCb);

        final JPanel proxyPanel = new JPanel();
        proxyPanel.add(new JLabel("Proxy Host: "));
        proxyPanel.add(proxyHostTf = new JTextField("255.255.255.255"));
        proxyPanel.add(new JLabel("  Proxy Port :"));
        proxyPanel.add(proxyPortTf = new JTextField("9999"));
        proxyPanel.add(new JLabel("    Proxy user :"));
        proxyPanel.add(proxyUserTf = new JTextField("proxyUser"));

        proxyPanel.add(new JLabel("Proxy pass : "));
        proxyPanel.add(proxyPassTf = new JTextField("proxyPass"));

        usingProxyCb = new JCheckBox("Use Proxy", true);
        usingProxyCb.setFocusPainted(false);
        ComponentTitledBorder componentBorder = new ComponentTitledBorder(usingProxyCb, proxyPanel,
                BorderFactory.createEtchedBorder(Color.WHITE, Color.WHITE));
        proxyPanel.setBorder(componentBorder);

        usingProxyCb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disableActivateTitlePane(usingProxyCb, proxyPanel);
            }
        });
        usingProxyCb.setSelected(false);
        disableActivateTitlePane(usingProxyCb, proxyPanel);
        proxyPanel.setBounds(80 - X_OFFSET, 350 - Y_OFFSET, 460, 125);
        mainFrame.add(proxyPanel);

        final JPanel configPanel = new JPanel();
        configPanel.add(disableSceneRenderingCb = new JCheckBox("Disable Scene Rendering "));
        configPanel.add(disableModelRenderingCb = new JCheckBox("Disable Model Rendering"));
        configPanel.add(superLowCpuCb = new JCheckBox("Super Low Cpu Mode"));
        configPanel.add(lowCpuCb = new JCheckBox("Low cpu mode"));
        configPanel.add(new JLabel("Engine tick delay:"));
        configPanel.add(tickDelayTf = new JTextField("0", 3));
        editBotConfigCb = new JCheckBox("Custom bot configuration", true);
        editBotConfigCb.setFocusPainted(false);
        ComponentTitledBorder componentBorder1 = new ComponentTitledBorder(editBotConfigCb, configPanel,
                BorderFactory.createEtchedBorder(Color.WHITE, Color.WHITE));
        configPanel.setBorder(componentBorder1);

        editBotConfigCb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disableActivateTitlePane(editBotConfigCb, configPanel);
            }
        });

        editBotConfigCb.setSelected(false);
        disableActivateTitlePane(editBotConfigCb, configPanel);
        configPanel.setBounds(580 - X_OFFSET, 125 - Y_OFFSET, 300, 235);
        mainFrame.add(configPanel);

        rsUserTf = new JTextField(30);
        rsUserTf.setBorder(new LineBorder(Color.WHITE));
        rsUserTf.setBounds(201, 140 - Y_OFFSET, 290, 30);
        mainFrame.add(rsUserTf);

        rsPassTf = new JTextField(30);
        rsPassTf.setBorder(new LineBorder(Color.WHITE));
        rsPassTf.setBounds(201, 175 - Y_OFFSET, 290, 30);
        mainFrame.add(rsPassTf);

        worldTf = new JTextField(30);
        worldTf.setBorder(new LineBorder(Color.WHITE));
        worldTf.setBounds(201, 210 - Y_OFFSET, 290, 30);
        mainFrame.add(worldTf);

        scriptNameTf = new JTextField(30);
        scriptNameTf.setBorder(new LineBorder(Color.WHITE));
        scriptNameTf.setBounds(201, 245 - Y_OFFSET, 290, 30);
        mainFrame.add(scriptNameTf);

        scriptArgsTf = new JTextField(30);
        scriptArgsTf.setBorder(new LineBorder(Color.WHITE));
        scriptArgsTf.setBounds(201, 280 - Y_OFFSET, 290, 30);
        mainFrame.add(scriptArgsTf);

        createDataGroupButton = new JButton("Add QuickLauncher");
        createDataGroupButton.setBounds(580 - X_OFFSET, 375 - Y_OFFSET, 300, 100);
        createDataGroupButton.setBorder(new LineBorder(Color.WHITE));
        createDataGroupButton.addActionListener(this);
        mainFrame.add(createDataGroupButton);

        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void disableActivateTitlePane(JCheckBox checkBox, JPanel container) {
        boolean enable = checkBox.isSelected();
        for (Component c : container.getComponents()) {
            c.setEnabled(enable);
        }
    }

    @Override
    public JFrame component() {
        return mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (!event.getSource().equals(createDataGroupButton)) {
            return;
        }
        controller().createInstanceDataGroup(rsUserTf.getText(), rsPassTf.getText(),
                (worldTf.getText().matches("-?\\d+") ? Integer.parseInt(worldTf.getText()) : 82),
                scriptNameTf.getText(), scriptArgsTf.getText(), isSdnScriptCb.isSelected());
        if (usingProxyCb.isSelected()) {
            controller().addProxyData(proxyHostTf.getText(),
                    (proxyPortTf.getText().matches("-?\\d+") ? Integer.parseInt(proxyPortTf.getText()) : 0),
                    proxyUserTf.getText(), proxyPassTf.getText());
        }
        if (editBotConfigCb.isSelected()) {
            controller().addClientConfigurationData(disableSceneRenderingCb.isSelected(),
                    disableModelRenderingCb.isSelected(), superLowCpuCb.isSelected(), lowCpuCb.isSelected(),
                    (tickDelayTf.getText().matches("-?\\d+") ? Integer.parseInt(tickDelayTf.getText()) : 0));
        }
        controller().addCurrentInstanceDataGroup();
        mainFrame.dispose();
    }
}
