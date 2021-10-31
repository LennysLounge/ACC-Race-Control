/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay;

import racecontrol.client.extension.broadcastingoverlay.http.HTTPServer;
import racecontrol.client.extension.broadcastingoverlay.http.HTTPConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import racecontrol.client.AccBroadcastingExtension;
import racecontrol.client.extension.broadcastingoverlay.http.HTTPRequest;
import static racecontrol.client.extension.broadcastingoverlay.websocket.WebSocket.printBytes;
import racecontrol.client.extension.broadcastingoverlay.websocket.WebSocketConnection;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventListener;

/**
 *
 * @author Leonard
 */
public class BroadcastingOverlayExtension
        implements EventListener, AccBroadcastingExtension, WebSocketConnectionCallback {

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
     * List of currently active websocket connections.
     */
    private final List<WebSocketConnection> webSocketConnections = new ArrayList<>();
    /**
     * Whether or not the overlay is running.
     */
    private boolean isRunning = false;
    /**
     * The thread that runs the web server.
     */
    private HTTPServer webServer;

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

    @Override
    public void requestWebSocket(Socket s, HTTPRequest request) {
        LOG.info("Request for upgrade to web socket.");

        var webSocketConnection = new WebSocketConnection(s);
        webSocketConnection.sendHandshakeResponse(request);

        webSocketConnections.add(webSocketConnection);

        while (true) {
            try {
                InputStream in = s.getInputStream();
                OutputStream out = s.getOutputStream();
                while (in.available() == 0) {
                    System.out.println("wait for data");
                    Thread.sleep(1000);
                }

                System.out.println("read " + in.available() + " bytes");
                byte[] bytes = in.readNBytes(in.available());
                System.out.println("data: " + printBytes(bytes));
                int opcode = bytes[0] & 0x7f;
                int length = bytes[1] & 0x7f;
                byte[] key = Arrays.copyOfRange(bytes, 2, 6);
                byte[] message = Arrays.copyOfRange(bytes, 6, bytes.length);
                System.out.println("opcode: " + opcode);
                System.out.println("length: " + length);
                System.out.println("key: " + printBytes(key));
                System.out.println("message: " + printBytes(message));

                byte[] decoded = new byte[length];
                for (int i = 0; i < message.length; i++) {
                    decoded[i] = (byte) (message[i] ^ key[i & 0x3]);
                }
                message = decoded;

                System.out.println("decoded: " + printBytes(decoded));
                System.out.println(new String(decoded));

                System.out.println("sending data");
                byte[] payload = new byte[message.length + 2];
                payload[0] = (byte) 0x81;
                payload[1] = (byte) (message.length & 0x7f);
                System.arraycopy(message, 0, payload, 2, message.length);
                System.out.println("sending payload: " + printBytes(payload));
                out.write(payload);
                out.flush();

                System.out.println("done");
            } catch (IOException ex) {
                Logger.getLogger(BroadcastingOverlayExtension.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(BroadcastingOverlayExtension.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void closeWebSocket(WebSocketConnection connection) {

    }

    public void startBroadcastingOverlay() {
        if (!isRunning) {
            LOG.info("Start broadcasting overlay");
            webServer = new HTTPServer(PORT);
            var thread = new Thread(webServer, "Web server");
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
