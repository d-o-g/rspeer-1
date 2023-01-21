package org.rspeer;

import com.beust.jcommander.Parameter;

/**
 * Created by MadDev on 2/10/18.
 */
public class BotArgs {

    @Parameter(names = "-proxyip")
    private String proxyIp;

    @Parameter(names = "-proxyport")
    private String proxyPort;

    @Parameter(names = "-proxyuser")
    private String proxyUsername;

    @Parameter(names = "-proxypass")
    private String proxyPassword;

    @Parameter(names = {"-qsArgs", "-qs"})
    private String quickStart;

    @Parameter(names = "-launcher")
    private String launcherUrl;

    @Parameter(names = "-script")
    private String script;

    public String getProxyIp() {
        return proxyIp;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getQuickStart() {
        return quickStart;
    }

    public String getLauncherUrl() {
        return launcherUrl;
    }

    public String getScript() {
        return script;
    }
}
