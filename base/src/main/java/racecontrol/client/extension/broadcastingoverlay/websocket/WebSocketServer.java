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
    private final List<WebSocketConnection> connections = new ArrayList<>();
    /**
     * Indicates that this server is running.
     */
    private volatile boolean running = true;
    /**
     * The thread the server is running on.
     */
    private Thread serverThread;
    /**
     * List of messages to send to sockets.
     */
    private final List<String> messages = new ArrayList<>();

    @Override
    public void run() {
        LOG.info("Web socket server started");
        serverThread = Thread.currentThread();
        while (running) {
            // update connections.

            synchronized (messages) {
                List<WebSocketConnection> connectionsCopy = new ArrayList<>(connections);
                for (var connection : connectionsCopy) {
                    connection.update();

                    if (!messages.isEmpty()) {
                        for (String msg : messages) {
                            connection.sendMessage(msg);
                        }
                    }
                }
            }

            messages.clear();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                // interruped
            }
        }
        LOG.info("Closing web socket server");
        // close all active connections.
        List<WebSocketConnection> connectionsCopy = new ArrayList<>(connections);
        for (var connection : connectionsCopy) {
            connection.close();
        }
        connections.clear();

        LOG.info("Web socket server done");

    }

    public void requestWebSocket(Socket s) {
        var webSocketConnection = new WebSocketConnection(s, this);
        connections.add(webSocketConnection);
    }

    public void closeConnection(WebSocketConnection connection) {
        LOG.info("close connection");
        connections.remove(connection);
    }

    public void sendMessage(String message) {
        synchronized (messages) {
            messages.add(message);
        }
    }

    public void stopServer() {
        running = false;
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }
}
