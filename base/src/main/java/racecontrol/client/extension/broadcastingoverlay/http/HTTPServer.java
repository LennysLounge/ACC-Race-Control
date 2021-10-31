/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import racecontrol.client.extension.broadcastingoverlay.websocket.WebSocketServer;

/**
 *
 * @author Leonard
 */
public class HTTPServer
        implements Runnable {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(HTTPServer.class.getName());
    /**
     * The port to listen on.
     */
    private final int PORT;
    /**
     * The server socket.
     */
    private ServerSocket serverConnect;
    /**
     * Indicates that this server is running.
     */
    private volatile boolean running = true;
    /**
     * A web socket server.
     */
    private final WebSocketServer webSocketServer;

    public HTTPServer(int port, WebSocketServer webSocketServer) {
        this.PORT = port;
        this.webSocketServer = webSocketServer;
    }

    @Override
    public void run() {
        try {
            LOG.info("Web server started. Listening on port : " + PORT);

            serverConnect = new ServerSocket(PORT);
            serverConnect.setSoTimeout(60000);

            while (running) {
                try {
                    HTTPConnection httpConnection = new HTTPConnection(serverConnect.accept(), webSocketServer);

                    // create dedicated thread to manage the client connection
                    Thread thread = new Thread(httpConnection, "HTTP connection");
                    thread.start();

                } catch (SocketTimeoutException e) {
                    // socket timed out, try again.
                } catch (SocketException e) {
                    if (running) {
                        LOG.log(Level.SEVERE, "Socket error.", e);
                    }
                }
            }
        } catch (IOException e) {
            LOG.log(Level.INFO, "Server Connection error : " + e);
        }
        LOG.info("Web server done.");
    }

    public void stopServer() {
        running = false;
        LOG.info("closing web server connection");

        // close server socker to interrupt the loop.
        if (serverConnect != null) {
            try {
                serverConnect.close();
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Error closing web server.", e);
                // wait for timeout to stop server
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

}
