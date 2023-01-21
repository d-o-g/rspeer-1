package org.rspeer.script;

import org.rspeer.QuickStartArgs;
import org.rspeer.QuickStartService;
import org.rspeer.script.provider.ScriptSource;
import org.rspeer.ui.Log;

public class ScriptReloader {

    public void execute() {
        Script script = ScriptExecutor.getCurrent();
        if(script == null) {
            return;
        }
        Log.fine("Attempting to reload script: " + script.getMeta().name() + " by " + script.getMeta().developer() + ".");
        QuickStartArgs args = buildArgs(script, ScriptExecutor.getScriptSource());
        ScriptExecutor.stop();
        QuickStartService service = new QuickStartService(args, null);
        service.apply();
    }

    private QuickStartArgs buildArgs(Script script, ScriptSource source) {
        QuickStartArgs args = new QuickStartArgs();
        args.setScriptName(source.getName());
        args.setRepoScript(!source.isLocal());
        if(script.getProfile() != null) {
            args.setBreakProfile(script.getProfile().getName());
        }
        GameAccount account = script.getAccount();
        if(account != null) {
            args.setRsUsername(account.getUsername());
            args.setRsPassword(account.getPassword());
        }
        return args;
    }

}
