/**
 * Copyright (c) 2021 Leonard Schüngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import racecontrol.gui.app.statuspanel.StatusPanelManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import processing.core.PGraphics;
import processing.core.PImage;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.app.Menu.MenuItem;
import racecontrol.gui.app.test.TestPanel;
import racecontrol.gui.app.logging.LoggingPanel;
import racecontrol.gui.app.racecontrol.RaceControlController;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.ConnectionClosedEvent;
import racecontrol.client.events.ConnectionOpenedEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.app.livetiming.LiveTimingController;
import racecontrol.gui.lpui.LPComponent;
import racecontrol.gui.lpui.LPContainer;

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
     * Live timing component controller.
     */
    private LiveTimingController liveTimingController;

    private TestPanel testPanel;

    private RaceControlController raceControlController;
    /**
     * Manages the status panels.
     */
    private StatusPanelManager statusPanelManager;
    /**
     * Placeholder panel for a detached panel.
     */
    private DetachedPlaceholderPanel detachedPlaceholderPanel;
    /**
     * Is initialised.
     */
    private boolean initialised;
    /**
     * Map of components to their window applet.
     */
    private final Map<LPComponent, PanelWindowApplet> windowPanels = new HashMap<>();

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
        detachedPlaceholderPanel = new DetachedPlaceholderPanel();
        settingsPanel = new SettingsPanel();
        loggingPanel = new LoggingPanel();
        liveTimingController = new LiveTimingController();
        raceControlController = RaceControlController.getInstance();
        raceControlController.initialise();
        testPanel = new TestPanel();
        statusPanelManager = StatusPanelManager.getInstance();
        statusPanelManager.initialise(appPanel);

        appPanel.liveTimingMenuItem.setClickAction(button -> menuItemClicked(appPanel.liveTimingMenuItem, button));
        appPanel.raceControlMenuItem.setClickAction(button -> menuItemClicked(appPanel.raceControlMenuItem, button));
        appPanel.debugMenuItem.setClickAction(button -> menuItemClicked(appPanel.debugMenuItem, button));
        appPanel.logMenuItem.setClickAction(button -> menuItemClicked(appPanel.logMenuItem, button));
        appPanel.settingsMenuItem.setClickAction(button -> menuItemClicked(appPanel.settingsMenuItem, button));

        appPanel.setActivePage(settingsPanel);
        appPanel.menu.setSelectedMenuItem(appPanel.settingsMenuItem);
    }

    public LPComponent getGUIComponent() {
        return appPanel;
    }

    private void menuItemClicked(MenuItem item, int button) {
        LPContainer panel = getPanelForMenuItem(item);
        if (panel != null) {
            if (button == LEFT) {
                if (windowPanels.containsKey(panel)) {
                    // panel is detached so we grab the focus instead.
                    windowPanels.get(panel).grabFocus();
                    detachedPlaceholderPanel.setPanelName(panel.getName());
                    panel = detachedPlaceholderPanel;
                }
                appPanel.setActivePage(panel);
                appPanel.updateComponents();
                appPanel.invalidate();
            }

            if (button == CENTER
                    && panel != settingsPanel
                    && appPanel.menu.getSelectedItem() == item) {
                detachPanel(panel);
            }
        }
    }

    private void detachPanel(LPContainer panel) {
        panel.setPosition(0, 0);
        PanelWindowApplet applet = launchNewWindow(panel, true);
        applet.addCloseAction(() -> {
            if (getPanelForMenuItem(appPanel.menu.getSelectedItem()) == panel) {
                appPanel.setActivePage(panel);
                appPanel.updateComponents();
                appPanel.invalidate();
            }
        });
        detachedPlaceholderPanel.setPanelName(panel.getName());
        detachedPlaceholderPanel.invalidate();
        appPanel.setActivePage(detachedPlaceholderPanel);
        appPanel.updateComponents();
        appPanel.invalidate();
    }

    private LPContainer getPanelForMenuItem(MenuItem item) {
        if (item == appPanel.liveTimingMenuItem) {
            return liveTimingController.getPanel();
        } else if (item == appPanel.raceControlMenuItem) {
            return raceControlController.getPanel();
        } else if (item == appPanel.debugMenuItem) {
            return testPanel;
        } else if (item == appPanel.logMenuItem) {
            return loggingPanel;
        } else if (item == appPanel.settingsMenuItem) {
            return settingsPanel;
        }
        return null;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            appPanel.header.invalidate();
        } else if (e instanceof ConnectionOpenedEvent) {
            appPanel.setActivePage(liveTimingController.getPanel());
            appPanel.menu.setSelectedMenuItem(appPanel.liveTimingMenuItem);
            appPanel.updateComponents();
            appPanel.invalidate();
        } else if (e instanceof ConnectionClosedEvent) {
            ConnectionClosedEvent event = (ConnectionClosedEvent) e;
            if (event.getExitState() == AccBroadcastingClient.ExitState.NORMAL) {
                appPanel.setActivePage(settingsPanel);
                appPanel.updateComponents();
            } else if (event.getExitState() == AccBroadcastingClient.ExitState.TIMEOUT) {
                statusPanelManager.addStatusPanel(new ConnectionTimeoutStatusPanel());
            }
            appPanel.invalidate();
        }
    }

    /**
     * Creates a new window for a panel. Does not create a new window if the
     * panel already is assigned to a window.
     *
     * @param panel The panel to create a window for.
     * @param resizeable Is that panel resizable.
     * @return The applet for that panel.
     */
    public PanelWindowApplet launchNewWindow(LPComponent panel, boolean resizeable) {
        // only create a window if that panel doesnt already have one.
        if (!windowPanels.containsKey(panel)) {
            panel.setPosition(0, 0);
            PanelWindowApplet applet = new PanelWindowApplet(panel, resizeable);
            applet.addCloseAction(() -> {
                windowPanels.remove(panel);
            });
            windowPanels.put(panel, applet);
        } else {
            windowPanels.get(panel).grabFocus();
        }
        return windowPanels.get(panel);
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
