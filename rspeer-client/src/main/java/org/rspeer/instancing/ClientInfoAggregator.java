package org.rspeer.instancing;

import org.json.JSONObject;
import org.rspeer.RSPeer;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.api_services.enitites.ClientInfo;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.HttpUtil;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.GameAccount;
import org.rspeer.script.ScriptExecutor;
import org.rspeer.script.provider.RemoteScriptSource;
import org.rspeer.script.provider.ScriptSource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientInfoAggregator {

    public static ClientInfo execute() {
        ClientInfo info = new ClientInfo();
        JSONObject currentUser = RsPeerApi.getCurrentUser();
        if (currentUser != null) {
            info.setEmail(currentUser.getString("email"));
            info.setUserId(currentUser.getInt("id"));
        }
        info.setMachineName(getComputerName());
        info.setJavaVersion(String.valueOf(getJavaVersion()));
        info.setProxyIp(System.getProperty("socksProxyHost"));
        info.setProxyUsername(System.getProperty("java.net.socks.username"));
        info.setOperatingSystem(System.getProperty("os.name"));
        info.setMachineUserName(System.getProperty("user.name"));
        info.setVersion(RsPeerApi.getOurVersion());
        try {
            try(Stream<Path> stream = Files.walk(Paths.get(Configuration.HOME))) {
                info.setRspeerFolderFileNames(stream.filter(Files::isRegularFile)
                        .filter(s -> !s.getParent().toString().contains("bot-assets"))
                        .limit(10)
                        .map(s -> s.getFileName().toString()).collect(Collectors.toList()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Game.getClient() != null) {
            String name = Game.getClient().getUsername();
            GameAccount gameAccount = RSPeer.getGameAccount();
            if(gameAccount != null && (name == null || name.length() == 0)) {
                name = gameAccount.getUsername();
            }
            info.setRunescapeLogin(name);
            if (name != null) {
                Player player = Players.getLocal();
                if (player != null) {
                    info.setRsn(player.getName());
                }
            }
        }

        if (ScriptExecutor.getCurrent() != null) {
            ScriptSource source = ScriptExecutor.getScriptSource();
            if (source == null) {
                info.setScriptName(ScriptExecutor.getCurrent().getMeta().name());
            } else {
                String name = source.getName();
                if (source instanceof RemoteScriptSource) {
                    RemoteScriptSource remoteScriptSource = (RemoteScriptSource) source;
                    info.setScriptId(remoteScriptSource.getId());
                    info.setScriptDeveloper(remoteScriptSource.getDeveloper());
                } else if (source.getTarget() != null) {
                    info.setScriptDeveloper(source.getDeveloper());
                    info.setScriptClassName(source.getTarget().getSimpleName());
                }
                info.setScriptName(name);
            }
            info.setRepoScript(ScriptExecutor.isRemoteScript());
        }

        String ip = RSPeer.getOriginalIp();
        if (ip == null) {
            RSPeer.setOriginalIpOnce(HttpUtil.getIpAddress());
        }
        info.setIpAddress(RSPeer.getOriginalIp());
        return info;
    }

    public static Callable<String> executeToString() {
        return () -> RsPeerApi.gson.toJson(execute());
    }

    private static String getComputerName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String host = System.getenv("COMPUTERNAME");
        if (host != null) {
            return host;
        }
        host = System.getenv("HOSTNAME");
        if (host != null) {
            return host;
        }
        return null;
    }

    private static double getJavaVersion() {
        String version = System.getProperty("java.version");
        int pos = version.indexOf('.');
        pos = version.indexOf('.', pos + 1);
        return Double.parseDouble(version.substring(0, pos));
    }
}
