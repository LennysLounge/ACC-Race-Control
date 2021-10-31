/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay.websocket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import racecontrol.client.extension.broadcastingoverlay.http.HTTPRequest;

/**
 *
 * @author Leonard
 */
public class WebSocketServer
        implements Runnable {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(WebSocketServer.class.getName());
    /**
     * List of currently active websocket connections.
     */
    private final List<WebSocketConnection> webSocketConnections = new ArrayList<>();
    /**
     * Indicates that this server is running.
     */
    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            // update connections.
            for (var connection : webSocketConnections) {
                if (connection.hasMessage()) {
                    String message = connection.getMessage();
                    LOG.info("message: " + message);
                    connection.sendMessage(message);
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                // interruped
            }
        }

    }

    public void requestWebSocket(Socket s) {
        var webSocketConnection = new WebSocketConnection(s);
        webSocketConnections.add(webSocketConnection);
    }

    public void stopServer() {

    }

}
