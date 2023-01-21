package org.rspeer.ui.debug.quicklaunchtool.model;

/**
 * @author MalikDz
 */

public class ProxyData {

    private int port;
    private boolean isUsingProxy;
    private String host, user, pass;

    public ProxyData() {
        this("", 0, "", "", false);
    }

    public ProxyData(String host, int port, String user, String pass, boolean isUsingProxy) {
        this.isUsingProxy = isUsingProxy;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.host = host;
    }

    public boolean isUsingProxy() {
        return isUsingProxy;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return user;
    }

    public String getPassword() {
        return pass;
    }
}
