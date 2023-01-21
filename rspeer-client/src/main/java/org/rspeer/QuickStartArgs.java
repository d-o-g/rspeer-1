package org.rspeer;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.rspeer.script.events.breaking.BreakProfile;
import org.rspeer.ui.Log;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@DoNotRename
public class QuickStartArgs {

    @Expose
    @SerializedName(value = "RsUsername", alternate = {"rsUsername"})
    @DoNotRename
    private String rsUsername;
    @Expose
    @SerializedName(value = "RsPassword", alternate = {"rsPassword"})
    @DoNotRename
    private String rsPassword;
    @Expose
    @SerializedName(value = "World", alternate = "world")
    @DoNotRename
    private int world;
    @Expose
    @SerializedName(value = "ScriptName", alternate = "scriptName")
    @DoNotRename
    private String scriptName;
    @Expose
    @SerializedName(value = "IsRepoScript", alternate = "isRepoScript")
    @DoNotRename
    private boolean isRepoScript;
    @Expose
    @SerializedName(value = "ScriptArgs", alternate = "scriptArgs")
    @DoNotRename
    private String scriptArgs;
    @Expose
    @SerializedName(value = "Config", alternate = "config")
    @DoNotRename
    private Config config;
    @Expose
    @SerializedName(value = "BreakProfile", alternate = "breakProfile")
    @DoNotRename
    private String breakProfile;
    @SerializedName(value = "UseProxy", alternate = "useProxy")
    @DoNotRename
    private boolean useProxy;
    @SerializedName(value = "ProxyIp", alternate = "proxyIp")
    @DoNotRename
    private String proxyIp;
    @SerializedName(value = "ProxyUser", alternate = "proxyUser")
    @DoNotRename
    private String proxyUsername;
    @SerializedName(value = "ProxyPass", alternate = "proxyPass")
    @DoNotRename
    private String proxyPass;
    @SerializedName(value = "ProxyPort", alternate = "proxyPort")
    @DoNotRename
    private int proxyPort;

    public void setRsUsername(String rsUsername) {
        this.rsUsername = rsUsername;
    }

    public void setRsPassword(String rsPassword) {
        this.rsPassword = rsPassword;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public void setRepoScript(boolean repoScript) {
        isRepoScript = repoScript;
    }

    public void setScriptArgs(String scriptArgs) {
        this.scriptArgs = scriptArgs;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setBreakProfile(String breakProfile) {
        this.breakProfile = breakProfile;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public void setProxyPass(String proxyPass) {
        this.proxyPass = proxyPass;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @DoNotRename
    public static QuickStartArgs parse(String value) throws Exception {
        List<Exception> errors = new ArrayList<>();
        try {
            String json = new String(Base64.getDecoder().decode(value));
            Log.fine(json);
            return new Gson().fromJson(json, QuickStartArgs.class);
        } catch (Exception e) {
            errors.add(e);
        }
        try {
            URL url = new URL(value);
            if (url.getHost() != null) {
                HttpResponse<String> res = Unirest.get(value).asString();
                Log.fine(res);
                return new Gson().fromJson(res.getBody(), QuickStartArgs.class);
            }
        } catch (Exception e) {
            errors.add(e);
        }
        if (Files.exists(Paths.get(value))) {
            try {
                String text = new String(Files.readAllBytes(Paths.get(value)));
                Log.fine(text);
                return new Gson().fromJson(text, QuickStartArgs.class);
            } catch (IOException e) {
               errors.add(e);
            }
        }
        else {
            errors.add(new Exception("Configuration file does not exist at path: " + value + ". Unable to process quick launch args. Make sure to provide the absolute path to a file."));
        }
        Log.severe("Exhausted all options to parse provided quick launch configuration.");
        StringBuilder stack = new StringBuilder();
        for (Exception error : errors) {
            stack.append(error.toString());
        }
        throw new Exception(stack.toString());
    }

    @DoNotRename
    public String getRsUsername() {
        return rsUsername;
    }

    @DoNotRename
    public String getRsPassword() {
        return rsPassword;
    }

    @DoNotRename
    public int getWorld() {
        return world;
    }

    @DoNotRename
    public String getScriptName() {
        return scriptName;
    }

    @DoNotRename
    public boolean getIsRepoScript() {
        return isRepoScript;
    }

    @DoNotRename
    public String getScriptArgs() {
        return scriptArgs;
    }

    @DoNotRename
    public Config getConfig() {
        return config;
    }

    @DoNotRename
    public String getBreakProfile() {
        return breakProfile;
    }

    @DoNotRename
    public boolean getRepoScript() {
        return isRepoScript;
    }

    @DoNotRename
    public boolean isUseProxy() {
        return useProxy;
    }

    @DoNotRename
    public String getProxyIp() {
        return proxyIp;
    }

    @DoNotRename
    public String getProxyUsername() {
        return proxyUsername;
    }

    @DoNotRename
    public String getProxyPass() {
        return proxyPass;
    }

    @DoNotRename
    public int getProxyPort() {
        return proxyPort;
    }

    @DoNotRename
    public class Config {

        @Expose
        @SerializedName(value = "LowCpuMode", alternate = "lowCpuMode")
        @DoNotRename
        private boolean lowCpuMode;

        @Expose
        @SerializedName(value = "SuperLowCpuMode", alternate = "superLowCpuMode")
        @DoNotRename
        private boolean superLowCpuMode;


        @Expose
        @SerializedName(value = "EngineTickDelay", alternate = "engineTickDelay")
        @DoNotRename
        private int engineTickDelay;
        @Expose
        @SerializedName(value = "DisableModelRendering", alternate = "disableModelRendering")
        @DoNotRename
        private boolean disableModelRendering;
        @Expose
        @SerializedName(value = "DisableSceneRendering", alternate = "disableSceneRendering")
        @DoNotRename
        private boolean disableSceneRendering;

        @DoNotRename
        public boolean getLowCpuMode() {
            return lowCpuMode;
        }

        @DoNotRename
        public int getEngineTickDelay() {
            return engineTickDelay;
        }

        @DoNotRename
        public boolean getDisableModelRendering() {
            return disableModelRendering;
        }

        @DoNotRename
        public boolean getDisableSceneRendering() {
            return disableSceneRendering;
        }

        @DoNotRename
        public boolean isSuperLowCpuMode() {
            return superLowCpuMode;
        }
    }
}
