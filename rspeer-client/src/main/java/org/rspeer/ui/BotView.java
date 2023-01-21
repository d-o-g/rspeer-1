package org.rspeer.ui;

import com.allatori.annotations.DoNotRename;
import org.rspeer.RSPeer;
import org.rspeer.api_services.PingService;
import org.rspeer.commons.*;
import org.rspeer.ui.commons.SwingResources;
import org.rspeer.ui.component.BotTitlePane;
import org.rspeer.ui.component.BotTitlePaneHelper;
import org.rspeer.ui.component.BotToolBar;
import org.rspeer.ui.component.Splash;
import org.rspeer.ui.component.log.LogBar;
import org.rspeer.ui.component.log.LogPane;
import org.rspeer.ui.component.log.LogTextArea;
import org.rspeer.ui.startup.InstanceLimit;
import org.rspeer.ui.startup.Login;
import org.rspeer.ui.startup.StartupFailure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;

@DoNotRename
public final class BotView extends JFrame {

    private LogPane logPane;
    private BotToolBar toolBar;
    private Splash splash;
    private Login login;
    private InstanceLimit instanceLimit;
    private StartupFailure startupFailure;

    public BotView() {
        super(BotTitlePaneHelper.getFrameTitle(false));
        setTitle("RSPeer");
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(Color.BLACK);
        add(toolBar = new BotToolBar(), BorderLayout.NORTH);
        SwingResources.setStrictSize(this, 771, 587);
        add(logPane = new LogPane(new LogTextArea(), new LogBar()), BorderLayout.SOUTH);
        BotTitlePane.decorate(this);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (e instanceof ThreadDeath) {
                return;
            }
            Log.severe(e);
        });

        addWindowListener(new WindowAdapter() {
            //Un-minimized
            @Override
            public void windowDeiconified(WindowEvent e) {
                boolean expandLogger = BotPreferences.getInstance().isExpandLogger();
                RSPeer.getView().getLogPane().setMinified(!expandLogger);
                super.windowDeiconified(e);
            }

            //Minimized
            @Override
            public void windowIconified(WindowEvent e) {
                RSPeer.getView().getLogPane().setMinified(true);
                super.windowIconified(e);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                Log.fine("Shutting down... Please wait.");
                RsPeerExecutor.execute(() -> PingService.getInstance().onClientClose());
                RsPeerExecutor.execute(Failsafe::shutDown);
                Time.sleep(100);
                Runtime.getRuntime().exit(0);
                super.windowClosing(e);
            }
        });
    }

    @DoNotRename
    public void expand() {
        requestFocusInWindow();
    }

    @DoNotRename
    public void minimize() {
        setState(JFrame.ICONIFIED);
    }

    public synchronized BotToolBar getToolBar() {
        if(toolBar == null) {
            add(toolBar = new BotToolBar(), BorderLayout.NORTH);
        }
        return toolBar;
    }

    public synchronized StartupFailure getStartupFailure() {
        if(startupFailure == null) {
            add(startupFailure = new StartupFailure(), BorderLayout.CENTER);
        }
        return startupFailure;
    }

    public synchronized Splash getSplash() {
        if(splash == null) {
            add(splash = new Splash(), BorderLayout.CENTER);
        }
        return splash;
    }

    public synchronized void removeSplash() {
        if(splash == null) {
            return;
        }
        remove(splash);
        splash = null;
    }

    public synchronized LogPane getLogPane() {
        if(logPane == null) {
            add(logPane = new LogPane(new LogTextArea(), new LogBar()), BorderLayout.SOUTH);
        }
        return logPane;
    }

    public synchronized Login getLogin() {
      if(login == null) {
          add(login = new Login(), BorderLayout.CENTER);
      }
      return login;
    }

    public synchronized InstanceLimit getInstanceLimit() {
        if(instanceLimit == null) {
            add(instanceLimit = new InstanceLimit(), BorderLayout.CENTER);
            Timer timer = new Timer((int) Duration.ofMinutes(2).toMillis(), (evt) -> dispose());
            timer.start();
        }
        return instanceLimit;
    }
}
