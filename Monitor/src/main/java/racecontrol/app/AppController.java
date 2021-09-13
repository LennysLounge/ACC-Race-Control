/**
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.app;

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
        raceControlController = new RaceControlController();
        testPanel = new TestPanel();

        appPanel.getMenu().addMenuItem(new Menu.MenuItem("Menu", () -> {
            appPanel.getMenu().toggleCollapse();
            appPanel.updateComponents();
            appPanel.invalidate();
        }));

        appPanel.getMenu().addMenuItem(new Menu.MenuItem("Live Timing", () -> {
            appPanel.setActivePage(broadcastingController.getPanel());
            appPanel.updateComponents();
            appPanel.invalidate();
        }));

        appPanel.getMenu().addMenuItem(new Menu.MenuItem("Race Control", () -> {
            appPanel.setActivePage(raceControlController.getPanel());
            appPanel.updateComponents();
            appPanel.invalidate();
        }));

        appPanel.getMenu().addMenuItem(new Menu.MenuItem("Log", () -> {
            appPanel.setActivePage(loggingPanel);
            appPanel.updateComponents();
            appPanel.invalidate();
        }));

        appPanel.getMenu().addMenuItem(new Menu.MenuItem("Debug", () -> {
            appPanel.setActivePage(testPanel);
            appPanel.updateComponents();
            appPanel.invalidate();
        }));

        Menu.MenuItem settingsMenuItem = new Menu.MenuItem("Settings", () -> {
            appPanel.setActivePage(settingsPanel);
            appPanel.updateComponents();
            appPanel.invalidate();
        });
        appPanel.getMenu().addMenuItem(settingsMenuItem);
        appPanel.getMenu().setVisible(true);

        appPanel.setActivePage(settingsPanel);
        appPanel.getMenu().setSelectedMenuItem(settingsMenuItem);
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
            appPanel.getMenu().setSelectedMenuIndex(1);
            appPanel.updateComponents();
            appPanel.invalidate();
        } else if (e instanceof ConnectionClosedEvent) {
            ConnectionClosedEvent event = (ConnectionClosedEvent) e;
            if (event.getExitState() == AccBroadcastingClient.ExitState.NORMAL) {
                appPanel.setActivePage(settingsPanel);
                appPanel.updateComponents();
                appPanel.invalidate();
            } else if (event.getExitState() == AccBroadcastingClient.ExitState.TIMEOUT) {
                addStatusPanel(new ConnectionTimeoutStatusPanel());
            }
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
}
