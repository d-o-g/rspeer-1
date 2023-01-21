package org.rspeer.ui.debug.quicklaunchtool.controller;

import org.rspeer.ui.debug.quicklaunchtool.model.BotConfigurationData;
import org.rspeer.ui.debug.quicklaunchtool.model.InstanceDataGroup;
import org.rspeer.ui.debug.quicklaunchtool.model.ProxyData;
import org.rspeer.ui.debug.quicklaunchtool.view.InstanceDataGroupView;

import javax.swing.*;

/**
 * @author MalikDz
 */

public class InstanceDataGroupFormController extends BaseController<InstanceDataGroupView> {

    public LaunchCreatorController launchCreatorController;
    private InstanceDataGroup insDataGroup;

    public InstanceDataGroupFormController(LaunchCreatorController launchCreatorController) {
        this.launchCreatorController = launchCreatorController;
    }

    @Override
    public InstanceDataGroupView view() {
        return view;
    }

    public void addClientConfigurationData(boolean sceneRendering, boolean modelRendering, boolean sLowCpu,
                                           boolean lowCpu, int tick) {
        insDataGroup.setBotConfig(new BotConfigurationData(sceneRendering, modelRendering, sLowCpu, lowCpu, tick));
    }

    public void createInstanceDataGroup(String user, String pass, int world, String scriptName, String scriptArgs,
                                        boolean sdnScript) {
        insDataGroup = new InstanceDataGroup(user, pass, world, scriptName, scriptArgs, sdnScript);
    }

    public void addCurrentInstanceDataGroup() {
        launchCreatorController.addInstanceDataGroup(insDataGroup.generateKey(), insDataGroup);
    }

    public void addProxyData(String host, int port, String user, String pass) {
        insDataGroup.setProxyData(new ProxyData(host, port, user, pass, true));
    }

    @Override
    public void initView() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view = new InstanceDataGroupView(InstanceDataGroupFormController.this);
            }
        });
    }
}
