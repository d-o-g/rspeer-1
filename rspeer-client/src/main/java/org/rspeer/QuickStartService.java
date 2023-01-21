package org.rspeer;

import org.rspeer.api_services.Logger;
import org.rspeer.commons.Configuration;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.providers.RSWorld;
import org.rspeer.script.GameAccount;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptExecutor;
import org.rspeer.script.events.breaking.BreakProfile;
import org.rspeer.script.provider.*;
import org.rspeer.ui.Log;
import org.rspeer.ui.account.XorSerializedAccountList;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class QuickStartService {

    private final QuickStartArgs args;
    //Kept for backwards compatibility.
    private final BotArgs botArgsLegacy;
    private final ScriptProvider<ScriptSource> localProvider;
    private final ScriptProvider<ScriptSource> remoteProvider;

    public QuickStartService(QuickStartArgs args, BotArgs botArgsLegacy) {
        this.args = args;
        this.botArgsLegacy = botArgsLegacy;
        this.localProvider = new LocalScriptProvider(new File(Configuration.SCRIPTS));
        this.remoteProvider = new RemoteScriptProvider();
    }

    public void apply() {

        SwingUtilities.invokeLater(this::tryApplyScript);

        if (args == null) {
            return;
        }

        QuickStartArgs.Config config = args.getConfig();
        if (config == null) {
            return;
        }
        if (config.isSuperLowCpuMode()) {
            Log.info("Setting super low cpu mode.");
            Projection.setLowCPUMode(true);
            Projection.setTickDelay(90);
        } else if (config.getLowCpuMode()) {
            Log.info("Setting low cpu mode.");
            Projection.setLowCPUMode(true);
        } else {
            if (config.getEngineTickDelay() > 0) {
                Log.info("Setting engine tick delay to: " + config.getEngineTickDelay());
                Projection.setTickDelay(config.getEngineTickDelay());
            }
            if (config.getDisableModelRendering()) {
                Log.info("Disabling model rendering.");
                Projection.setModelRenderingEnabled(false);
            }
            if (config.getDisableSceneRendering()) {
                Log.info("Disabling scene rendering.");
                Projection.setLandscapeRenderingEnabled(false);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean tryApplyScript() {
        if ((args == null || args.getScriptName() == null) && (botArgsLegacy == null || botArgsLegacy.getScript() == null)) {
            return false;
        }

        String name =
                args != null && args.getScriptName() != null ?
                        args.getScriptName().toLowerCase().trim() : botArgsLegacy.getScript().toLowerCase().trim();

        Log.info("Attempting to start script by name: " + name);

        List<ScriptSource> sources = new ArrayList<>();

        if (args != null && args.getIsRepoScript()) {
            ScriptSource[] remoteSources = remoteProvider.load();
            sources.addAll(Arrays.asList(remoteSources));
        } else {
            ScriptSource[] remoteSources = remoteProvider.load();
            ScriptSource[] localSources = localProvider.load();
            sources.addAll(Arrays.asList(remoteSources));
            sources.addAll(Arrays.asList(localSources));
        }
        ScriptSource script = null;
        for (ScriptSource s : sources) {
            if (s.getName().toLowerCase().trim().equals(name)) {
                if (s instanceof RemoteScriptSource && args != null && !args.getIsRepoScript()) {
                    continue;
                }
                script = s;
                break;
            }
        }

        if (script == null) {
            Log.severe("quickStart", "Failed to find script by name " + name + ". Unable to start.");
            return false;
        }

        try {
            if (script instanceof RemoteScriptSource) {
                remoteProvider.prepare(script);
            } else {
                localProvider.prepare(script);
            }
            Script invoked = script.getTarget().newInstance();
            if (invoked == null) {
                Log.severe("quickStart", "Failed to find script by name " + name + ". Unable to start.");
                return false;
            }
            if (args != null && args.getScriptArgs() != null) {
                invoked.setArgs(args.getScriptArgs());
            }
            if (args != null && args.getRsUsername() != null && args.getRsPassword() != null) {
                XorSerializedAccountList accountList = new XorSerializedAccountList();
                boolean set = false;
                for (GameAccount account : accountList) {
                    if (account.getUsername().equals(args.getRsUsername())) {
                        set = true;
                        invoked.setAccount(account);
                        break;
                    }
                }

                if (!set) {
                    invoked.setAccount(new GameAccount(args.getRsUsername(), args.getRsPassword()));
                }
            }
            if (args != null && args.getBreakProfile() != null) {
                BreakProfile named = BreakProfile.fromName(args.getBreakProfile());
                if (named != null) {
                    invoked.setBreakProfile(named);
                }
            }
            Log.fine("Successfully started script: " + invoked.getMeta().name() + " by " + invoked.getMeta().developer());
            ScriptExecutor.start(script, invoked);
        } catch (Exception e) {
            Logger.getInstance().capture(e);
            Log.severe("quickStart", e);
            ScriptExecutor.stop();
            return false;
        }
        return true;
    }
}
