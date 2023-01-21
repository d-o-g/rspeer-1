package org.rspeer.startup.children;

import org.json.JSONObject;
import org.rspeer.RSPeer;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.api_services.UserService;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.startup.Login;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SessionValidator {

    public void execute(Runnable callback) {
        if(RsPeerApi.getCurrentUser() != null) {
            onSuccess(callback);
            return;
        }
        EventQueue.invokeLater(() -> {
            RSPeer.getView().getSplash().setVisible(false);
            RSPeer.getView().revalidate();
            addListeners(RSPeer.getView().getLogin(), callback);
        });
    }

    private void addListeners(Login login, Runnable callback) {
        login.getPasswordInput().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                doLogin(login, callback);
            }
        });
        login.getLoginButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                doLogin(login, callback);
            }
        });
        login.getNoAccount().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://sso.rspeer.org/?redirect=https://scripts.rspeer.org"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
                super.mousePressed(e);
            }
        });
    }

    private void doLogin(Login login, Runnable callback) {
        login.getLoginButton().setText("Logging in...");
        String email = login.getEmailInput().getText();
        String password = new String(login.getPasswordInput().getPassword());
        RsPeerExecutor.execute(() -> {
            Time.sleep(1000);
            try {
                UserService.getInstance().login(email, password);
            } catch (Exception e) {
                login.getLoginButton().setText(e.getMessage());
                return;
            }
            login.getLoginButton().setText("Successfully logged in.");
            RSPeer.getView().remove(RSPeer.getView().getLogin());
            onSuccess(callback);
        });
    }

    private void onSuccess(Runnable callback)  {
        try {
            RsPeerApi.initialize();
        } catch (Exception e) {
            try {
                Files.deleteIfExists(Paths.get(Configuration.ME));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        RSPeer.getView().getSplash().setVisible(true);
        callback.run();
    }
}
