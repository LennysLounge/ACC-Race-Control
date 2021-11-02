/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay.websocket;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class WebSocketConnection {

    private static final Logger LOG = Logger.getLogger(WebSocketConnection.class.getName());

    private WebSocketServer server;

    private final Socket socket;

    private final List<String> messages = new ArrayList<>();

    public WebSocketConnection(Socket socket, WebSocketServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void update() {
        try {
            var in = socket.getInputStream();
            if (in.available() > 0) {
                byte[] bytes = in.readNBytes(in.available());
                readMessage(bytes, 0);
            }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error accessing input from socket.", e);
        }
    }

    private void readMessage(byte[] bytes, int start) {
        if ((bytes[start] & 0x80) == 0) {
            throw new IllegalArgumentException("FIN bit not set");
        }
        // read length
        int lengthBytes = 0;
        int length = bytes[start + 1] & 0x7f;
        if (length == 126) {
            lengthBytes = 2;
            byte[] l = new byte[]{0x0, 0x0, bytes[start + 2], bytes[start + 3]};
            length = ByteBuffer.wrap(l).order(ByteOrder.BIG_ENDIAN).getInt();
        }
        // read opcode
        int opcode = bytes[start] & 0x7f;
        switch (opcode) {
            case 0x01:  // text frame
                byte[] key = Arrays.copyOfRange(bytes, start + 2 + lengthBytes, start + 2 + lengthBytes + 4);
                byte[] message = Arrays.copyOfRange(bytes, start + 6 + lengthBytes, start + 6 + lengthBytes + length);

                for (int i = 0; i < message.length; i++) {
                    message[i] = (byte) (message[i] ^ key[i & 0x3]);
                }
                messages.add(new String(message));
                break;

            case 0x08:  // connection close
                closeInternal();
                break;

            default:
                throw new IllegalArgumentException("Unknown opcode");
        }

        int payloadLength = 6 + lengthBytes + length;
        if (start + payloadLength != bytes.length) {
            readMessage(bytes, start + payloadLength);
        }
    }

    public boolean hasMessage() {
        return !messages.isEmpty();
    }

    public String getNextMessage() {
        return messages.remove(0);
    }

    public void sendMessage(String message) {
        if(socket.isClosed()){
            return;
        }
        try {
            byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
            
            int lengthByte = msgBytes.length;
            int lengthBytes = 0;
            if (lengthByte >= 126) {
                lengthByte = 126;
                lengthBytes = 2;
            }

            byte[] payload = new byte[2 + lengthBytes + msgBytes.length];
            payload[0] = (byte) 0x81;
            payload[1] = (byte) (lengthByte & 0x7f);
            if (lengthBytes == 2) {
                System.arraycopy(ByteBuffer.allocate(4).putInt(msgBytes.length).array(), 2,
                        payload, 2, 2);
            }
            System.arraycopy(msgBytes, 0, payload, 2 + lengthBytes, msgBytes.length);

            socket.getOutputStream().write(payload);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error accessing output from socket.", e);
            closeInternal();
        }
    }

    private void closeInternal() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(WebSocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        server.closeConnection(this);
    }

    private void sendCloseMessage() {
        try {
            byte[] payload = new byte[2];
            payload[0] = (byte) 0x88;
            payload[1] = (byte) 0x0;

            socket.getOutputStream().write(payload);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error accessing output from socket.", e);
        }
    }

    public void close() {
        sendCloseMessage();
        closeInternal();
    }

}
