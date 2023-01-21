package org.rspeer.api_services.enitites;

import com.allatori.annotations.DoNotRename;

import java.util.List;

@DoNotRename
public class ClientInfo {

    @DoNotRename
    private String email;
    @DoNotRename
    private int userId;
    @DoNotRename
    private String ipAddress;
    @DoNotRename
    private String proxyIp;
    @DoNotRename
    private String proxyUsername;
    @DoNotRename
    private String scriptName;
    @DoNotRename
    private int scriptId;
    @DoNotRename
    private boolean isRepoScript;
    @DoNotRename
    private String rsn;
    @DoNotRename
    private String runescapeLogin;
    @DoNotRename
    private String machineName;
    @DoNotRename
    private String javaVersion;
    @DoNotRename
    private String operatingSystem;
    @DoNotRename
    private String machineUserName;
    @DoNotRename
    private List<String> rspeerFolderFileNames;
    @DoNotRename
    public String scriptClassName;
    @DoNotRename
    public String scriptDeveloper;
    @DoNotRename
    public double version;
    @DoNotRename
    public boolean atInstanceLimit;
    @DoNotRename
    public boolean isBanned;

    public String getRunescapeLogin() {
        return runescapeLogin;
    }

    public void setRunescapeLogin(String runescapeLogin) {
        this.runescapeLogin = runescapeLogin;
    }

    public String getScriptClassName() {
        return scriptClassName;
    }

    public void setScriptClassName(String scriptClassName) {
        this.scriptClassName = scriptClassName;
    }

    public String getScriptDeveloper() {
        return scriptDeveloper;
    }

    public void setScriptDeveloper(String scriptDeveloper) {
        this.scriptDeveloper = scriptDeveloper;
    }

    public String getEmail() {
        return email;
    }

    public int getUserId() {
        return userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getRsn() {
        return rsn;
    }

    public String getMachineName() {
        return machineName;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public void setRsn(String rsn) {
        this.rsn = rsn;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getMachineUserName() {
        return machineUserName;
    }

    public void setMachineUserName(String machineUserName) {
        this.machineUserName = machineUserName;
    }

    public boolean isRepoScript() {
        return isRepoScript;
    }

    public void setRepoScript(boolean repoScript) {
        isRepoScript = repoScript;
    }

    public List<String> getRspeerFolderFileNames() {
        return rspeerFolderFileNames;
    }

    public void setRspeerFolderFileNames(List<String> rspeerFolderFileNames) {
        this.rspeerFolderFileNames = rspeerFolderFileNames;
    }

    public int getScriptId() {
        return scriptId;
    }

    public void setScriptId(int scriptId) {
        this.scriptId = scriptId;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public double getVersion() {
        return version;
    }

    public void setAtInstanceLimit(boolean atInstanceLimit) {
        this.atInstanceLimit = atInstanceLimit;
    }

    public boolean isAtInstanceLimit() {
        return atInstanceLimit;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
