package org.rspeer.script.events;

import org.rspeer.RSPeer;
import org.rspeer.api_services.PingService;
import org.rspeer.commons.BotPreferences;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Login;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.event.listeners.LoginResponseListener;
import org.rspeer.runetek.event.types.LoginResponseEvent;
import org.rspeer.script.GameAccount;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptBlockingEvent;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/**
 * Created by Spencer on 05/05/2018.
 */
@ScriptMeta(name = "Login screen handler", developer = "Spencer", desc = "Logs you back in, if you are logged out")
public final class LoginScreen extends ScriptBlockingEvent implements LoginResponseListener {

    private static final Rectangle LOGIN_BUTTON_COMPONENT = new Rectangle(230, 300, 140, 40);

    private static final BooleanSupplier PROCESSED
            = () -> Interfaces.isVisible(InterfaceComposite.WELCOME_SCREEN.getGroup(), 0);

    private final List<LoginResponseEvent.Response> stoppers;

    private int delay = -1;
    private boolean delayOnLoginLimit = true;

    public LoginScreen(Script ctx) {
        super(ctx);
        stoppers = new ArrayList<>();
        stoppers.add(LoginResponseEvent.Response.ACCOUNT_DISABLED);
        stoppers.add(LoginResponseEvent.Response.ACCOUNT_LOCKED);
        stoppers.add(LoginResponseEvent.Response.RUNESCAPE_UPDATE);
        stoppers.add(LoginResponseEvent.Response.RUNESCAPE_UPDATE_2);
    }

    @Override
    public boolean validate() {
        return !Game.isLoggedIn() && ctx.getAccount() != null && ctx.getAccount().validate()
                && Game.getState() != Game.STATE_LOADING_REGION
                && Game.getState() != Game.STATE_HOPPING_WORLD;
    }

    @Override
    public void process() {
        if (delay != -1 && delayOnLoginLimit) {
            Time.sleep(delay);
            delay = -1;
            return;
        }
        GameAccount account = ctx.getAccount();
        if ((Game.getState() == Game.STATE_CREDENTIALS_SCREEN || Game.getState() == 11) && account != null && account.validate()) {
            Login.enterCredentials(account.getUsername(), account.getPassword());
            Time.sleep(920, 1530);
            if (Game.getClient().isLoginWorldSelectorOpen()) {
                Game.getClient().setLoginWorldSelectorOpen(false);
            }
            Time.sleep(125, 225);
            Keyboard.pressEnter();
            Keyboard.pressEnter();
            if (Time.sleepUntilForDuration(PROCESSED, 600, 1200)) {
                return;
            }
        }
        Time.sleep(600, 900);
    }

    @Override
    public void notify(LoginResponseEvent e) {
        if (e.getResponse() == LoginResponseEvent.Response.TOO_MANY_ATTEMPTS) {
            delay = Random.nextInt(30000, 45000);
        }
        else if(e.getResponse() == LoginResponseEvent.Response.ACCOUNT_DISABLED && BotPreferences.getInstance().isCloseOnBan()) {
            Log.severe("Account has been disabled, shutting down client in 5 seconds.");
            RsPeerExecutor.schedule(() -> {
                PingService.getInstance().onBan();
                PingService.getInstance().onClientClose();
                recordBan();
                Time.sleep(4000);
                RSPeer.shutdown();
            }, 100, TimeUnit.MILLISECONDS);
            ctx.setStopping(true);
        }
        else if (stoppers.contains(e.getResponse())) {
            ctx.setStopping(true);
        }
    }

    public void setStopScriptOn(LoginResponseEvent.Response response) {
        setStopScriptOn(response, false);
    }

    public void setStopScriptOn(LoginResponseEvent.Response response, boolean remove) {
        if (remove) {
            stoppers.remove(response);
        } else {
            stoppers.add(response);
        }
    }

    private void recordBan() {
        try {
            String formatted = Game.getClient().getUsername() + ":" + Instant.now().toString();
            File file = new File(Paths.get(Configuration.DATA, "bans.txt").toUri());
            file.createNewFile();
            Files.write(file.toPath(),
                    (formatted + System.lineSeparator()).getBytes(),
                    StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDelayOnLoginLimit(boolean delayOnLoginLimit) {
        this.delayOnLoginLimit = delayOnLoginLimit;
    }
}
