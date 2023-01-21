package org.rspeer.ui.debug.quicklaunchtool.model;

import org.json.JSONObject;

/**
 * @author MalikDz
 */

public class InstanceDataGroup {

    private int world;
    private boolean sdnScript;
    private ProxyData proxyData = new ProxyData();
    private BotConfigurationData botConfigurationData;
    private String user, pass, scriptName, scriptArgs;

    public InstanceDataGroup(String user, String pass, int world, String scriptName, String scriptArgs,
                             boolean sdnScript) {
        this.scriptName = scriptName;
        this.scriptArgs = scriptArgs;
        this.sdnScript = sdnScript;
        this.world = world;
        this.user = user;
        this.pass = pass;
    }

    public void setBotConfig(BotConfigurationData botConfigurationData) {
        this.botConfigurationData = botConfigurationData;
    }

    public void setProxyData(ProxyData proxyData) {
        this.proxyData = proxyData;
    }

    public String generateKey() {
        return user + "(" + scriptName + ")";
    }

    @Override
    public String toString() {
        return generateKey();
    }

    public String getJsonStringRepresentation() {
        return getJSonObject().toString(4);
    }

    public JSONObject getJSonObject() {
        JSONObject botDataJsonObj = new JSONObject();
        botDataJsonObj.put("RsUsername", user);
        botDataJsonObj.put("RsPassword", pass);
        botDataJsonObj.put("World", world);
        botDataJsonObj.put("ScriptName", scriptName);
        botDataJsonObj.put("IsRepoScript", sdnScript);
        botDataJsonObj.put("ScriptArgs", scriptArgs);
        botDataJsonObj.put("UseProxy", proxyData.isUsingProxy());
        botDataJsonObj.put("ProxyPort", proxyData.getPort());
        botDataJsonObj.put("ProxyIp", proxyData.getHost());
        botDataJsonObj.put("ProxyUser", proxyData.getUsername());
        botDataJsonObj.put("ProxyPass", proxyData.getPassword());
        if (botConfigurationData != null) {
            JSONObject botConfigJsonObj = new JSONObject();
            botConfigJsonObj.put("LowCpuMode", botConfigurationData.activatingLowCpu());
            botConfigJsonObj.put("SuperLowCpuMode", botConfigurationData.activatingSuperLowCpu());
            botConfigJsonObj.put("EngineTickDelay", botConfigurationData.getEngineTick());
            botConfigJsonObj.put("DisableModelRendering", botConfigurationData.disablingModelRendering());
            botConfigJsonObj.put("DisableSceneRendering", botConfigurationData.disablingSceneRendering());
            botDataJsonObj.put("Config", botConfigJsonObj);
        }
        return botDataJsonObj;
    }
}
