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
import java.util.Arrays;
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

    public String getMessage() {
        if (!hasMessage()) {
            return "";
        }
        String rtn = "";
        try {
            var in = socket.getInputStream();
            byte[] bytes = in.readNBytes(in.available());
            int opcode = bytes[0] & 0x7f;
            int length = bytes[1] & 0x7f;
            byte[] key = Arrays.copyOfRange(bytes, 2, 6);
            byte[] message = Arrays.copyOfRange(bytes, 6, bytes.length);

            byte[] decoded = new byte[length];
            for (int i = 0; i < message.length; i++) {
                decoded[i] = (byte) (message[i] ^ key[i & 0x3]);
            }
            rtn = new String(decoded);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error accessing input from socket.", e);
        }
        return rtn;
    }

    public boolean hasMessage() {
        boolean rtn = false;
        try {
            rtn = socket.getInputStream().available() > 0;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error accessing input from socket.", e);
        }
        return rtn;
    }

    public void sendMessage(String message) {
        try {
            byte[] payload = new byte[message.length() + 2];
            payload[0] = (byte) 0x81;
            payload[1] = (byte) (message.length() & 0x7f);
            System.arraycopy(message.getBytes(), 0, payload, 2, message.length());

            socket.getOutputStream().write(payload);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error accessing output from socket.", e);
        }
    }

}
