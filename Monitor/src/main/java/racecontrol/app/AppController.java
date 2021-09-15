/**
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import processing.core.PGraphics;
import processing.core.PImage;
import racecontrol.RaceControlApplet;
import racecontrol.app.Menu.MenuItem;
import racecontrol.app.test.TestPanel;
import racecontrol.app.broadcasting.BroadcastingController;
import racecontrol.app.logging.LoggingPanel;
import racecontrol.app.racecontrol.RaceControlController;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.ConnectionClosedEvent;
import racecontrol.client.events.ConnectionOpenedEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.lpgui.gui.LPComponent;

/**
 *
 * @author Leonard
 */
public class AppController
        implements EventListener {

    /**
     * Singelton instance.
     */
    private static AppController instance;

    /**
     * The GUI component.
     */
    private AppPanel appPanel;
    /**
     * Settings panel.
     */
    private SettingsPanel settingsPanel;
    /**
     * Logging panel.
     */
    private LoggingPanel loggingPanel;
    /**
     * Broadcasting controller.
     */
    private BroadcastingController broadcastingController;

    private TestPanel testPanel;

    private RaceControlController raceControlController;

    private boolean initialised;

    public static AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }

    private AppController() {
    }

    public void initialise() {
        if (initialised) {
            return;
        }
        initialised = true;

        EventBus.register(this);

        appPanel = new AppPanel();
        settingsPanel = new SettingsPanel();
        loggingPanel = new LoggingPanel();
        broadcastingController = new BroadcastingController();
        raceControlController = RaceControlController.getInstance();
        raceControlController.initialise();
        testPanel = new TestPanel();

        appPanel.menu.addMenuItem(new MenuItem("Menu",
                loadResourceAsPImage("/images/RC_Menu_Symbol.png"),
                (MenuItem prevItem) -> {
                    appPanel.menu.setSelectedMenuItem(prevItem);
                    appPanel.menu.toggleCollapse();
                    appPanel.updateComponents();
                    appPanel.invalidate();
                }));

        appPanel.menu.addMenuItem(new MenuItem("Live Timing",
                loadResourceAsPImage("/images/RC_Menu_LiveTiming.png"),
                () -> {
                    appPanel.setActivePage(broadcastingController.getPanel());
                    appPanel.updateComponents();
                    appPanel.invalidate();
                }));

        appPanel.menu.addMenuItem(new MenuItem("Race Control",
                loadResourceAsPImage("/images/RC_Menu_Control.png"),
                () -> {
                    appPanel.setActivePage(raceControlController.getPanel());
                    appPanel.updateComponents();
                    appPanel.invalidate();
                }));
        /*
        appPanel.menu.addMenuItem(new MenuItem("Broadcast",
                loadResourceAsPImage("/images/RC_Menu_Broadcast.png"),
                () -> {
                    appPanel.setActivePage(loggingPanel);
                    appPanel.updateComponents();
                    appPanel.invalidate();
                }));
        appPanel.menu.addMenuItem(new MenuItem("Autopilot",
                loadResourceAsPImage("/images/RC_Menu_AutoBroadcast.png"),
                () -> {
                    appPanel.setActivePage(loggingPanel);
                    appPanel.updateComponents();
                    appPanel.invalidate();
                }));
         */
        appPanel.menu.addMenuItem(new MenuItem("Log",
                loadResourceAsPImage("/images/RC_Menu_LOG.png"),
                () -> {
                    appPanel.setActivePage(loggingPanel);
                    appPanel.updateComponents();
                    appPanel.invalidate();
                }));
        /*
        appPanel.menu.addMenuItem(new MenuItem("Trackmap",
                loadResourceAsPImage("/images/RC_Menu_TrackMap.png"),
                () -> {
                    appPanel.setActivePage(testPanel);
                    appPanel.updateComponents();
                    appPanel.invalidate();
                }));
         */
        appPanel.menu.addMenuItem(new MenuItem("Debug",
                loadResourceAsPImage("/images/RC_Menu_Debugging.png"),
                () -> {
                    appPanel.setActivePage(testPanel);
                    appPanel.updateComponents();
                    appPanel.invalidate();
                }));

        Menu.MenuItem settingsMenuItem = new MenuItem("Settings",
                loadResourceAsPImage("/images/RC_Menu_Settings.png"),
                () -> {
                    appPanel.setActivePage(settingsPanel);
                    appPanel.updateComponents();
                    appPanel.invalidate();
                });
        appPanel.menu.addMenuItem(settingsMenuItem);
        appPanel.menu.setVisible(true);

        appPanel.setActivePage(settingsPanel);
        appPanel.menu.setSelectedMenuItem(settingsMenuItem);
    }

    public LPComponent getGUIComponent() {
        return appPanel;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            appPanel.getHeader().invalidate();
        } else if (e instanceof ConnectionOpenedEvent) {
            appPanel.setActivePage(broadcastingController.getPanel());
            appPanel.menu.setSelectedMenuIndex(1);
            appPanel.updateComponents();
            appPanel.invalidate();
        } else if (e instanceof ConnectionClosedEvent) {
            ConnectionClosedEvent event = (ConnectionClosedEvent) e;
            if (event.getExitState() == AccBroadcastingClient.ExitState.NORMAL) {
                appPanel.setActivePage(settingsPanel);
                appPanel.updateComponents();
            } else if (event.getExitState() == AccBroadcastingClient.ExitState.TIMEOUT) {
                addStatusPanel(new ConnectionTimeoutStatusPanel());
            }
            appPanel.invalidate();
        }
    }

    public PanelWindowApplet launchNewWindow(
            PanelController controller,
            boolean resizeable,
            Runnable closeAction) {
        PanelWindowApplet applet = new PanelWindowApplet(controller.getPanel(), resizeable);
        applet.setCloseAction(closeAction);
        return applet;
    }

    public void addStatusPanel(LPComponent statusPanel) {
        appPanel.addStatusPanel(statusPanel);
    }

    public void removeStatusPanel(LPComponent statusPanel) {
        appPanel.removeStatusPanel(statusPanel);
    }

    private PImage loadResourceAsPImage(String resource) {
        try {
            BufferedImage bi = ImageIO.read(RaceControlApplet.class.getResourceAsStream(resource));
            PGraphics g = LPComponent.getStaticApplet().createGraphics(bi.getWidth(), bi.getHeight());
            g.beginDraw();
            Graphics2D g2d = (Graphics2D) g.getNative();
            g2d.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
            g.endDraw();
            PImage b = g.copy();
            return b;
        } catch (IOException ex) {
            return null;
        }
    }
}
