/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay.websocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import racecontrol.client.extension.broadcastingoverlay.http.HTTPRequest;
import racecontrol.client.extension.broadcastingoverlay.http.HTTPResponse;

/**
 *
 * @author Leonard
 */
public class WebSocketConnection {

    private static final Logger LOG = Logger.getLogger(WebSocketConnection.class.getName());

    private final Socket socket;

    public WebSocketConnection(Socket socket) {
        this.socket = socket;
    }

    public void sendHandshakeResponse(HTTPRequest request) {
        try {
            byte[] acceptKey = (request.getHeader("Sec-WebSocket-Key")
                    + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                    .getBytes("UTF-8");
            Base64.Encoder encoder = Base64.getEncoder();
            MessageDigest sha1Hash = MessageDigest.getInstance("SHA-1");

            HTTPResponse response = new HTTPResponse(101, "Switching Protocols");
            response.setHeader("Connection", "Upgrade");
            response.setHeader("Upgrade", "websocket");
            response.setHeader("Sec-WebSocket-Accept",
                    encoder.encodeToString(sha1Hash.digest(acceptKey)));

            socket.getOutputStream().write(response.getBytes());
            socket.getOutputStream().flush();

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOG.log(Level.SEVERE, "Error upgrading to web socket", e);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error upgrading to web socket", e);
        }
    }

}
