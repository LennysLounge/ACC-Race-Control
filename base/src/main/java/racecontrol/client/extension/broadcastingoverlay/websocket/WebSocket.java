/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Leonard
 */
public class WebSocket {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
        ServerSocket server = new ServerSocket(80);
        try {
            System.out.println("Server has started on 127.0.0.1:80.\r\nWaiting for a connection...");
            Socket client = server.accept();
            System.out.println("A client connected.");
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            Scanner s = new Scanner(in, "UTF-8");
            try {
                String data = s.useDelimiter("\\r\\n\\r\\n").next();
                Matcher get = Pattern.compile("^GET").matcher(data);
                if (get.find()) {
                    Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                    match.find();
                    byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                            + "Connection: Upgrade\r\n"
                            + "Upgrade: websocket\r\n"
                            + "Sec-WebSocket-Accept: "
                            + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                            + "\r\n\r\n").getBytes("UTF-8");
                    out.write(response, 0, response.length);

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
                    System.out.println("decoded: " + printBytes(decoded));
                    System.out.println(new String(decoded));

                    System.out.println("sending data");
                    message = "Hello Browser!".getBytes();
                    byte[] payload = new byte[message.length + 2];
                    payload[0] = (byte)0x81;
                    payload[1] = (byte)(message.length & 0x7f);
                    System.arraycopy(message, 0, payload, 2, message.length);
                    System.out.println("sending payload: " + printBytes(payload));
                    out.write(payload);
                    out.flush();

                    System.out.println("done");
                }
            } finally {
                s.close();
            }
        } finally {
            server.close();
        }
    }

    public static String printBytes(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (byte b : bytes) {
            sb.append(String.format("%d ", (int) b & 0xff));
        }
        sb.append("]");
        return sb.toString();
    }
}
