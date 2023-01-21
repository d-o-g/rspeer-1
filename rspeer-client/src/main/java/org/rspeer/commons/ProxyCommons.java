package org.rspeer.commons;

import org.rspeer.BotArgs;
import org.rspeer.QuickStartArgs;
import org.rspeer.ui.Log;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by MadDev on 2/10/18.
 */
public class ProxyCommons {

    public static String trySet(BotArgs args, QuickStartArgs qsArgs) {

        //Kept bot args for backwards compatibility, will slowly fade it out.
        boolean hasBotArgProxy = args != null && args.getProxyIp() != null && args.getProxyPort() != null;
        boolean hasQsProxy = qsArgs != null && qsArgs.isUseProxy() && qsArgs.getProxyIp() != null;

        if (!hasBotArgProxy && !hasQsProxy) {
            return null;
        }

        String username = hasQsProxy ? qsArgs.getProxyUsername() : args.getProxyUsername();
        String password = hasQsProxy ? qsArgs.getProxyPass() : args.getProxyPassword();
        String ip = hasQsProxy ? qsArgs.getProxyIp() : args.getProxyIp();
        String port = hasQsProxy ? String.valueOf(qsArgs.getProxyPort()) : args.getProxyPort();
        return setProxy(ip, port, username, password);
    }

    public static String setProxy(String ip, String port, String username, String password) {
        Log.info("Setting proxy: " + ip + ":" + port + " " + username + " " + password);
        System.setProperty("proxySet", "true");
        System.setProperty("socksProxyHost", ip);
        System.setProperty("socksProxyPort", port);
        if (username != null) {
            System.setProperty("java.net.socks.username", username);
        }
        if (password != null) {
            System.setProperty("java.net.socks.password", password);
        }

        if (username != null || password != null) {
            Authenticator.setDefault(new ProxyAuth(username, password));
        }

        return ip + ":" + port;
    }

    private static class ProxyAuth extends Authenticator {

        private PasswordAuthentication auth;

        private ProxyAuth(String user, String password) {
            auth = new PasswordAuthentication(user, password == null
                    ? new char[]{} : password.toCharArray());
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return auth;
        }
    }

}
