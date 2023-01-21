package org.rspeer.ui.debug.quicklaunchtool.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rspeer.commons.Configuration;
import org.rspeer.ui.debug.quicklaunchtool.model.InstanceDataGroup;
import org.rspeer.ui.debug.quicklaunchtool.view.CreatorMainView;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MalikDz
 */

public class LaunchCreatorController extends BaseController<CreatorMainView> {

    private static final String LAUNCHER_QUICKLAUNCH_MESSAGE = "The LauncherQuickLaunch.json file has been generated, please check your rspeer cache folder";
    private static final String CLIENTS_QUICKLAUNCH_MESSAGE = "The client QuickLaunch files has been generated, please you check the quicklaunch forlder (located in the cache folder)";
    private InstanceDataGroupFormController instanceDataFormController;
    private Map<String, InstanceDataGroup> instanceDataGroupMap = new HashMap<String, InstanceDataGroup>();

    public LaunchCreatorController() {
        this.instanceDataFormController = new InstanceDataGroupFormController(this);
    }

    @Override
    public CreatorMainView view() {
        return view;
    }

    public void launchDataGroupForm() {
        instanceDataFormController.initView();
    }

    public void addInstanceDataGroup(String key, InstanceDataGroup data) {
        instanceDataGroupMap.put(key, data);
        view().getInstanceDataModel().addElement(data);
    }

    public void removeDataGroup(Object selectedValue) {
        if (selectedValue != null && selectedValue instanceof InstanceDataGroup) {
            view().getInstanceDataModel().removeElement(selectedValue);
        }
    }

    @Override
    public void initView() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view = new CreatorMainView(LaunchCreatorController.this);
            }
        });
    }

    public void generateClientQuickLaunchFiles() {
        int index = 0;
        if (checkForFolder(Paths.get(Configuration.CACHE, "quicklaunch"))) {
            for (Map.Entry<String, InstanceDataGroup> entry : instanceDataGroupMap.entrySet()) {
                Path path = Paths.get(Configuration.CACHE, "quicklaunch/" + entry.getKey() + ".json");
                generateJsonFile(path, CLIENTS_QUICKLAUNCH_MESSAGE, "Files generated", entry.getValue().getJSonObject(),
                        index++ == instanceDataGroupMap.size() - 1);
            }
        }
    }

    public void generateLauncherQuickLaunchFile() {
        JSONObject LauncherJsonObj = new JSONObject();
        JSONArray instanceDataJsonArray = new JSONArray();
        for (Map.Entry<String, InstanceDataGroup> entry : instanceDataGroupMap.entrySet()) {
            instanceDataJsonArray.put(entry.getValue().getJSonObject());
        }
        LauncherJsonObj.put("AutoUpdateClient", true);
        LauncherJsonObj.put("Clients", instanceDataJsonArray);
        Path path = Paths.get(Configuration.CACHE, "LauncherQuickLaunch.json");
        generateJsonFile(path, LAUNCHER_QUICKLAUNCH_MESSAGE, "File generated", LauncherJsonObj, true);
    }

    private void generateJsonFile(Path fullOutputPath, String message, String title, JSONObject object, boolean show) {
        try (FileWriter file = new FileWriter(fullOutputPath.toString())) {
            file.write(object.toString(4));
            if (show) {
                JOptionPane.showMessageDialog(view().component(), message, title, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkForFolder(Path folderPath) {
        try {
            if (!Files.exists(folderPath)) {
                Files.createDirectory(folderPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Files.exists(folderPath);
    }
}
