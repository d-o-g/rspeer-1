package org.rspeer.ui.component;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rspeer.RSPeer;
import org.rspeer.api_services.AuthorizationService;
import org.rspeer.api_services.PingService;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.commons.BotPreferences;
import org.rspeer.commons.HttpUtil;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.providers.subclass.GameCanvas;
import org.rspeer.script.ScriptExecutor;
import org.rspeer.script.ScriptReloader;
import org.rspeer.ui.Log;
import org.rspeer.ui.ScriptSelector;
import org.rspeer.ui.account.AccountManager;
import org.rspeer.ui.breaksettings.BreakSettingsPanel;
import org.rspeer.ui.commons.IconButton;
import org.rspeer.ui.commons.SwingResources;
import org.rspeer.ui.component.log.LogFileBuffer;
import org.rspeer.ui.debug.Debugger;
import org.rspeer.ui.debug.InterfaceExplorer;
import org.rspeer.ui.debug.quicklaunchtool.controller.LaunchCreatorController;
import org.rspeer.ui.debug.varpexlorer.VarpChangeFrame;
import org.rspeer.ui.instancing.CurrentUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class BotToolBar extends JComponent {

    private final JButton run, stop, refresh, input, cpusave, settings;
    private final Debugger debugger;
    private final Color border = new Color(0x2E2E2E);
    private final Color bg = new Color(0x212121);

    public BotToolBar() {
        SwingResources.setStrictSize(this, getWidth(), 30);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 1));

        run = createButton(SwingResources.PLAY);
        refresh = createButton(SwingResources.REFRESH);
        stop = createButton(SwingResources.STOP);
        input = createButton(SwingResources.MOUSE);
        cpusave = createButton(SwingResources.LOW_CPU_DISABLED);
        settings = createButton(SwingResources.SETTINGS);

        ImageIcon[] icons = SwingResources.loadButtonImages(BotTitlePane.class.getResourceAsStream("person-icon.png"), 16, 16, .7f);
        JButton accounts;
        if (icons.length > 0) {
            accounts = new IconButton(icons[0]);
            accounts.setRolloverIcon(icons[1]);
            accounts.setBorder(new EmptyBorder(0, 8, 0, 8));
        } else {
            accounts = createButton(SwingResources.ACCOUNT);
        }

        debugger = new Debugger();

        run.addActionListener(e -> {
            if (ScriptExecutor.getCurrent() != null) {
                if (run.getToolTipText().equals("Pause")) {
                    ScriptExecutor.pause();
                } else if (run.getToolTipText().equals("Play")) {
                    ScriptExecutor.resume();
                }
            } else {
                EventQueue.invokeLater(ScriptSelector::new);
            }
        });

        refresh.setVisible(false);
        refresh.setToolTipText("Reload current script");
        refresh.addActionListener(e -> {
            ScriptReloader reloader = new ScriptReloader();
            reloader.execute();
        });

        accounts.addActionListener((act) -> {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Account Manager");
                AccountManager manager = new AccountManager();
                frame.setContentPane(manager);
                frame.pack();
                frame.validate();
                frame.setLocationRelativeTo(this);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        manager.saveAllAccounts();
                        super.windowClosing(e);
                    }
                });
                BotTitlePane.decorate(frame);
                frame.setVisible(true);
            });
        });

        JPopupMenu popupMenu = createSettingsMenu();

        cpusave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Game.getClient() != null) {
                    if (cpusave.getText().equals(SwingResources.LOW_CPU_DISABLED) && !Projection.isLowCPUMode()) {
                        cpusave.setText(SwingResources.LOW_CPU_ENABLED);
                        Projection.setLowCPUMode(true);
                        Log.fine("Low Cpu Mode has been enabled.");
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            Log.fine("Super Low Cpu Mode has been enabled.");
                            Projection.setTickDelay(90);
                        }
                    } else {
                        cpusave.setText(SwingResources.LOW_CPU_DISABLED);
                        Projection.setLowCPUMode(false);
                        Log.fine("Low Cpu Mode has been disabled.");
                        Projection.setTickDelay(0);
                    }
                }
            }
        });

        settings.addActionListener(e -> popupMenu.show(settings, settings.getWidth(), settings.getHeight()));
        stop.addActionListener(e -> ScriptExecutor.stop());
        stop.setEnabled(false);

        add(refresh);
        add(run);
        add(stop);
        add(input);
        add(accounts);
        add(cpusave);
        add(settings);

        Consumer<Boolean> action = enabled -> input.setText(enabled ? SwingResources.MOUSE : SwingResources.KEYBOARD);
        GameCanvas.setCallback(action);
        input.addActionListener(x -> {
            boolean toggle = input.getText().equals(SwingResources.KEYBOARD);
            GameCanvas.setInputEnabled(toggle);
            action.accept(toggle);
        });
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(28, 28));
        button.setFont(SwingResources.getFontAwesome(14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(button.getForeground().darker().darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setForeground(button.getForeground().brighter().brighter());
            }
        });
        return button;
    }

    private void toggleLogger(boolean selected) {
        RSPeer.getView().getLogPane().setMinified(!selected);
        BotPreferences.getInstance().setExpandLogger(selected);

        int h = RSPeer.getView().getHeight();
        if (h < 600 && !selected) {
            return;
        }
        SwingResources.setStrictSize(RSPeer.getView(), RSPeer.getView().getWidth(),
                RSPeer.getView().getHeight() + (selected ? 100 : -100));
    }

    private JPopupMenu createSettingsMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem apperanceMenu = new JMenu("Appearance");
        menu.add(apperanceMenu);

        JMenuItem walkingMenu = new JMenu("Walking");
        menu.add(walkingMenu);

        JRadioButton daxWeb = new JRadioButton("Use Dax Web");
        JRadioButton acuityWeb = new JRadioButton("Use Acuity Web");

        ButtonGroup group = new ButtonGroup();
        group.add(acuityWeb);
        group.add(daxWeb);
        walkingMenu.add(daxWeb);
        walkingMenu.add(acuityWeb);

        daxWeb.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                BotPreferences.getInstance().setWebWalker(0);
                Log.fine("Successfully updated WebWalker to use Dax Web. Movement will now be handled by DaxWeb. For more information, visit: https://github.com/itsdax/RSPeer-Webwalker");
            }
        });
        acuityWeb.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                BotPreferences.getInstance().setWebWalker(1);
                Log.fine("Successfully updated WebWalker to use Acuity Web. Movement will now be handled by Acuity. Acuity Web is the default RSPeer web walker.");
            }
        });

        //Festive mode
        JCheckBoxMenuItem festive = new JCheckBoxMenuItem("Festive Mode");
        festive.addActionListener(e -> RsPeerExecutor.execute(() -> {
            BotPreferences.getInstance().setFestiveMode(festive.isSelected());
        }));
        festive.setSelected(BotPreferences.getInstance().isFestiveMode());
        apperanceMenu.add(festive);

        //Display IP
        JCheckBoxMenuItem displayIp = new JCheckBoxMenuItem("Display Ip On Titlebar");
        displayIp.addActionListener(e -> RsPeerExecutor.execute(() -> {
            BotPreferences.getInstance().setShowIpOnMenuBar(displayIp.isSelected());
            BotTitlePaneHelper.refreshFrameTitle();
        }));
        displayIp.setSelected(BotPreferences.getInstance().isShowIpOnMenuBar());
        apperanceMenu.add(displayIp);

        //Display Account
        JCheckBoxMenuItem displayAccount = new JCheckBoxMenuItem("Display Account On Titlebar");
        displayAccount.addActionListener(e -> RsPeerExecutor.execute(() -> {
            BotPreferences.getInstance().setShowAccountOnMenuBar(displayAccount.isSelected());
            BotTitlePaneHelper.refreshFrameTitle();
        }));
        displayAccount.setSelected(BotPreferences.getInstance().isShowAccountOnMenuBar());
        apperanceMenu.add(displayAccount);

        //Display Script
        JCheckBoxMenuItem displayScript = new JCheckBoxMenuItem("Display Script On Titlebar");
        displayScript.addActionListener(e -> RsPeerExecutor.execute(() -> {
            BotPreferences.getInstance().setShowScriptOnMenuBar(displayScript.isSelected());
            BotTitlePaneHelper.refreshFrameTitle();
        }));
        displayScript.setSelected(BotPreferences.getInstance().isShowScriptOnMenuBar());
        apperanceMenu.add(displayScript);

        //Allow Script Message
        JCheckBoxMenuItem allowScriptMessage = new JCheckBoxMenuItem("Display Script Status");
        allowScriptMessage.addActionListener(e -> RsPeerExecutor.execute(() -> {
            BotPreferences.getInstance().setAllowScriptMessageOnMenuBar(allowScriptMessage.isSelected());
            Log.info("Successfully toggled script status. Script status allows the script writer to update your toolbar with a custom message from within the script. This is useful for tracking status, kills, drops, etc.");
            BotTitlePaneHelper.refreshFrameTitle();
        }));
        allowScriptMessage.setSelected(BotPreferences.getInstance().isAllowScriptMessageOnMenuBar());
        apperanceMenu.add(allowScriptMessage);


        JMenuItem debugMenu = new JMenu("Debug");
        menu.add(debugMenu);

        JCheckBoxMenuItem logger = new JCheckBoxMenuItem("Expand Logger");
        logger.addActionListener(e -> toggleLogger(logger.isSelected()));
        logger.setSelected(BotPreferences.getInstance().isExpandLogger());
        SwingUtilities.invokeLater(() -> toggleLogger(logger.isSelected()));
        debugMenu.add(logger);

        JCheckBoxMenuItem showDebugger = new JCheckBoxMenuItem("Debug Paint");
        showDebugger.addActionListener(e -> {
            if (showDebugger.isSelected()) {
                debugger.start();
            } else {
                debugger.end();
            }
        });
        debugMenu.add(showDebugger);

        JCheckBoxMenuItem menuDebug = new JCheckBoxMenuItem("Menu Action Debug");
        menuDebug.addActionListener(e -> Game.getEventMediator().setDebugMenuActions(menuDebug.isSelected()));
        debugMenu.add(menuDebug);

        JCheckBoxMenuItem movementDebug = new JCheckBoxMenuItem("Movement Debug");
        movementDebug.addActionListener(e -> Movement.getDebug().toggle());
        debugMenu.add(movementDebug);

        JMenuItem iexplorer = new JMenuItem("Interface Explorer");
        iexplorer.addActionListener(e -> EventQueue.invokeLater(() -> new InterfaceExplorer().setVisible(true)));
        debugMenu.add(iexplorer);

        JMenuItem vexplorer = new JMenuItem("Varp Debugger");
        vexplorer.addActionListener(e -> EventQueue.invokeLater(VarpChangeFrame::new));
        debugMenu.add(vexplorer);

        JMenuItem quickLaunchTool = new JMenuItem("QuickLaunch Generator");
        LaunchCreatorController creatorController = new LaunchCreatorController();
        quickLaunchTool.addActionListener(e -> creatorController.initView());
        menu.add(quickLaunchTool);

        JMenuItem breakProfile = new JMenuItem("Break Settings");
        breakProfile.addActionListener(act -> BreakSettingsPanel.createView(this));
        menu.add(breakProfile);

        JMenuItem mapUtility = new JMenuItem("Map Utility");
        mapUtility.addActionListener(e -> {
           RsPeerExecutor.execute(() -> {
               try {
                   Desktop.getDesktop().browse(URI.create("https://explv.github.io/"));
               } catch (IOException e1) {
                   JOptionPane.showMessageDialog(BotToolBar.this,
                           "Failed to open browser. Go to: https://explv.github.io/");
                   e1.printStackTrace();
               }
           });
        });
        menu.add(mapUtility);

        JMenuItem me = new JMenuItem("View Me");
        me.addActionListener(e -> {
            JSONObject user = RsPeerApi.getCurrentUser();
            if (user == null) {
                JOptionPane.showMessageDialog(BotToolBar.this, "Failed to load user.");
                return;
            }
            CurrentUser display = new CurrentUser();
            display.setLocationRelativeTo(BotToolBar.this);
            BotTitlePane.decorate(display);
            display.getUsername().setText("Username: " + user.getString("username"));
            display.getEmail().setText("Email: " + user.getString("email"));
            display.getBalance().setText("Token Balance: " + user.getInt("balance"));
            display.getInstances().setText("Allowed Instances: " + user.getInt("instances"));
            JSONArray groupNames = user.getJSONArray("groupNames");

            display.getIp().setText("Ip: " + HttpUtil.getIpAddress());
            display.getGroups().setText("Groups: " + groupNames.join(", "));
            display.setVisible(true);
        });
        menu.add(me);

        JCheckBoxMenuItem enableFileLogging = new JCheckBoxMenuItem("Enable File Logging");
        enableFileLogging.addActionListener(e -> {
            boolean selected = enableFileLogging.isSelected();
            BotPreferences.getInstance().setEnableFileLogging(selected);
            RSPeer.getView().getLogPane().toggleFileLogging(selected);
            if(selected) {
                Log.fine("File logging has been enabled. All logs will be saved to " + LogFileBuffer.LOG_DIRECTORY);
            }
        });
        enableFileLogging.setSelected(BotPreferences.getInstance().isEnableFileLogging());
        menu.add(enableFileLogging);


        JCheckBoxMenuItem closeClientUponBan = new JCheckBoxMenuItem("Close Bot Upon Ban");
        closeClientUponBan.addActionListener(e -> {
            boolean selected = closeClientUponBan.isSelected();
            BotPreferences.getInstance().setCloseOnBan(selected);
            if(selected) {
                Log.fine("Client will now close automatically if a running script receives a ban. This will apply to all new clients.");
            }
            else {
                Log.fine("Client will now only stop the current running script if a ban is received. This will apply to all new clients.");
            }
        });
        closeClientUponBan.setSelected(BotPreferences.getInstance().isCloseOnBan());
        menu.add(closeClientUponBan);

        JMenuItem signOut = new JMenuItem("Exit And Close");
        signOut.addActionListener(e -> {
            int value = JOptionPane.showConfirmDialog(menu, "Are you sure you want to exit and close?", "Please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(value == JOptionPane.YES_OPTION) {
                Log.fine("Signing out and shutting down client...");
                RsPeerExecutor.schedule(() -> {
                    try {
                        PingService.getInstance().onClientClose();
                        AuthorizationService.getInstance().clearSession();
                        Runtime.getRuntime().exit(0);
                    } catch (IOException ex) {
                        Log.severe(ex);
                    }
                }, 2, TimeUnit.SECONDS);
            }
        });
        menu.add(signOut);

     /*   JMenuItem settings = new JMenuItem("Settings");
        settings.addActionListener(e -> EventQueue.invokeLater(() -> new BotSettings().setVisible(true)));
        menu.add(settings);*/

        return menu;
    }

    public JButton getSettings() {
        return settings;
    }

    public JButton getStop() {
        return stop;
    }

    public JButton getRun() {
        return run;
    }

    public JButton getRefresh() {
        return refresh;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        /*
         * g.setColor(border); g.drawLine(0, 1, getWidth(), 1); g.drawLine(0, 2,
         * getWidth(), 2); g.drawLine(0, 3, getWidth(), 3);
         */
        g.setColor(bg);
        g.fillRect(0, 0, getWidth(), getHeight()); // 0, 4
    }
}
