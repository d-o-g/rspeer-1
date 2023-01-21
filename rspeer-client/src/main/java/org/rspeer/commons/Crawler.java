package org.rspeer.commons;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.FileUtils;
import org.rspeer.RSPeer;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.ui.Log;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarInputStream;

public final class Crawler {

    private final Map<String, String> parameters = new HashMap<>();
    private final String pack;
    private String home, config;
    private int percent;
    private int hash = -1;
    private int customWorld = -1;

    private boolean downloadedNew = false;

    public Crawler() {
        pack = Configuration.CACHE + "gamepack.jar";
        home = "http://oldschool" + RSPeer.getDefaultWorld() + ".runescape.com/";
        config = "http://oldschool.runescape.com/jav_config.ws";
        if (RSPeer.getQuickStartArgs() != null) {
            int world = RSPeer.getQuickStartArgs().getWorld();
            if (world == 0) {
                return;
            }
            world = world >= 300 ? world - 300 : world;
            if (world <= 0) {
                return;
            }
            RSPeer.getQuickStartArgs().setWorld(world);
            home = "http://oldschool" + world + ".runescape.com/";
            config = home + "jav_config.ws";
        }
    }

    public Applet create(ClassLoader classloader) {
        try {
            String main = parameters.get("initial_class").replace(".class", "");
            Applet applet = (Applet) classloader.loadClass(main).newInstance();
            applet.setBackground(Color.BLACK);
            applet.setSize(getAppletSize());
            applet.setLayout(null);
            applet.setStub(getEnvironment(applet));
            applet.setVisible(true);
            return applet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AppletStub getEnvironment(Applet applet) {

        return new AppletStub() {
            @Override
            public boolean isActive() {
                return true;
            }

            @Override
            public URL getDocumentBase() {
                try {
                    return new URL(parameters.get("codebase"));
                } catch (MalformedURLException e) {
                    return null;
                }
            }

            @Override
            public URL getCodeBase() {
                try {
                    return new URL(parameters.get("codebase"));
                } catch (MalformedURLException e) {
                    return null;
                }
            }

            @Override
            public String getParameter(String name) {
                return parameters.get(name);
            }

            @Override
            public void appletResize(int width, int height) {
                Dimension size = new Dimension(width, height);
                applet.setSize(size);
            }

            @Override
            public AppletContext getAppletContext() {
                return null;
            }
        };
    }

    private int getLocalHash() {
        try {
            URL url = new File(pack).toURI().toURL();
            try (JarInputStream stream = new JarInputStream(url.openStream())) {
                return stream.getManifest().hashCode();
            } catch (Exception e) {
                return -1;
            }
        } catch (MalformedURLException e) {
            return -1;
        }
    }

    public int getHash() {
        return hash;
    }

    public int getRemoteHash() {
        try {
            URL url = new URL(home + parameters.get("initial_jar"));
            try (JarInputStream stream = new JarInputStream(url.openStream())) {
                return stream.getManifest().hashCode();
            } catch (Exception e) {
                return -1;
            }
        } catch (IOException e) {
            return -1;
        }
    }

    public boolean isOutdated() {
        File gamepack = new File(pack);
        if (!gamepack.exists()) {
            return true;
        }
        if (hash == -1) {
            hash = getLocalHash();
        }
        boolean outdated = hash == -1 || hash != getRemoteHash();
        if (!outdated) {
            percent = 100;
        }
        if (outdated) {
            downloadedNew = true;
        }
        return outdated;
    }

    public boolean crawl() {
        try {
            String world = config.replace("36", customWorld != -1 ? String.valueOf(customWorld) : "");
            return doCrawl(world, 0);
        } catch (Exception e) {
            e.printStackTrace();
            EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(null, "Failed to crawl, unable to start bot, please check console for errors. Shutting RSPeer down in 20 seconds."));
            RsPeerExecutor.schedule(RSPeer::shutdown, 20, TimeUnit.SECONDS);
            return false;
        }
    }

    private boolean doCrawl(String world, int tries) throws Exception {
        try {
            Log.info("Attempting to load " + world + ". Do not close the client if this is taking awhile, it will be retried with a new world after 30 seconds.");
            HttpResponse<String> res = Unirest.get(world).asString();
            String source = res.getBody();
            if (res.getStatus() != 200 || source == null) {
                throw new Exception("Failed to crawl, unable to start bot, please check console for errors.");
            }
            Log.info("Successfully loaded, attempting to parse.");
            String[] split = source.split("\\r?\\n");
            for (String line : split) {
                if (line.startsWith("param=")) {
                    line = line.substring(6);
                }
                int idx = line.indexOf("=");
                if (idx == -1) {
                    continue;
                }
                parameters.put(line.substring(0, idx), line.substring(idx + 1));
            }
            return true;
        } catch(Exception e) {
            Log.severe("Failed to load Runescape World config, attempt number: " + tries + ". Attempting " + (50 - tries) + " more times.");
            if(tries < 50) {
                return doCrawl(getRandomWorld(), tries + 1);
            }
            throw e;
        }
    }

    private String getRandomWorld() {
        int world = Random.nextInt(300, 510);
        world = world - 300;
        return "http://oldschool" + world + ".runescape.com/jav_config.ws";
    }

    public boolean download() throws UnirestException, IOException {
        hash = getRemoteHash();
        HttpResponse<InputStream> gamepack = Unirest.get(home + parameters.get("initial_jar")).asBinary();
        FileUtils.copyInputStreamToFile(gamepack.getBody(), new File(pack));
        return true;
    }

    public Dimension getAppletSize() {
        try {
            return new Dimension(Integer.parseInt(parameters.get("applet_minwidth")), Integer.parseInt(parameters.get("applet_minheight")));
        } catch (NumberFormatException e) {
            return new Dimension(765, 503);
        }
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getPack() {
        return pack;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public boolean downloadedNew() {
        return downloadedNew;
    }
}