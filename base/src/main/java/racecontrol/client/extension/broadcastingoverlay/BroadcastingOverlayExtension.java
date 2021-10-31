/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay;

import racecontrol.client.extension.broadcastingoverlay.http.HTTPServer;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingExtension;
import racecontrol.client.extension.broadcastingoverlay.websocket.WebSocketServer;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventListener;

/**
 *
 * @author Leonard
 */
public class BroadcastingOverlayExtension
        implements EventListener, AccBroadcastingExtension {

    /**
     * Singelton instance.
     */
    private static BroadcastingOverlayExtension instance;
    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(BroadcastingOverlayExtension.class.getName());
    /**
     * Port to listen to.
     */
    private final int PORT = 8080;
    /**
     * Whether or not the overlay is running.
     */
    private boolean isRunning = false;
    /**
     * The thread that runs the web server.
     */
    private HTTPServer webServer;
    /**
     * The web socket server.
     */
    private WebSocketServer webSocketServer;

    /**
     * Gets the instance of this extension.
     *
     * @return The instance of this extension.
     */
    public static BroadcastingOverlayExtension getInstance() {
        if (instance == null) {
            instance = new BroadcastingOverlayExtension();
        }
        return instance;
    }

    private BroadcastingOverlayExtension() {
    }

    @Override
    public void onEvent(Event e) {
    }

    public void startBroadcastingOverlay() {
        if (!isRunning) {
            LOG.info("Start broadcasting overlay");
            webSocketServer = new WebSocketServer();
            var thread = new Thread(webSocketServer, "Websocket server");
            thread.start();

            webServer = new HTTPServer(PORT, webSocketServer);
            thread = new Thread(webServer, "Web server");
            thread.start();

            isRunning = true;
        }
    }

    public void stopBroadcastingOverlay() {
        if (isRunning) {
            LOG.info("Stop broadcasting overlay");
            webServer.stopServer();

            isRunning = false;
        }
    }

}
