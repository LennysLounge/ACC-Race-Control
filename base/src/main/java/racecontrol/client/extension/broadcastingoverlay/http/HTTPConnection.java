/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import racecontrol.client.extension.broadcastingoverlay.WebSocketConnectionCallback;
import static racecontrol.client.extension.broadcastingoverlay.http.HTTPRequest.Method.GET;
import racecontrol.utility.Version;

/**
 *
 * @author Leonard
 */
public class HTTPConnection
        implements Runnable {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(HTTPConnection.class.getName());

    private final Socket socket;

    private boolean closeSocket = true;

    private final File WEB_ROOT = new File(System.getProperty("user.dir") + "/broadcasting overlay/");
    private final String DEFAULT_FILE = "index.html";

    private final WebSocketConnectionCallback webSocketCallback;

    public HTTPConnection(Socket socket, WebSocketConnectionCallback callback) {
        this.socket = socket;
        this.webSocketCallback = callback;
    }

    @Override
    public void run() {
        LOG.info("Connecton opened. (" + new Date() + ")");
        try {
            HTTPRequest request = new HTTPRequest();
            request.read(socket.getInputStream());

            LOG.info(request.toString());

            HTTPResponse response = new HTTPResponse();
            response.setVersion("HTTP/1.1");
            response.setHeader("Server", "ACC Race Control broadcasting overlay:v" + Version.VERSION);
            response.setHeader("Date", new Date().toString());

            processRequest(request, response);

            socket.getOutputStream().write(response.getBytes());
            socket.getOutputStream().flush();

            if (closeSocket) {
                socket.close();
                LOG.info("Connecton closed");
            }
        } catch (IOException ioe) {
            LOG.info("Server error : " + ioe);
        }
    }

    private void processRequest(HTTPRequest request, HTTPResponse response) {
        if (request.getMethod() == GET) {

            if (request.getTarget().endsWith("/")) {
                serveIndex(response);
            } else if (request.getTarget().endsWith("/socket")) {
                webSocketCallback.requestWebSocket(socket, request);
                closeSocket = false;
            } else {
                respondError(404, "File not found", response);
            }
        } else {
            respondError(501, "Not implemented", response);
        }
    }

    private byte[] readFileData(File file)
            throws FileNotFoundException, IOException {
        var fileIn = new FileInputStream(file);
        return fileIn.readAllBytes();
    }

    // return supported MIME Types
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")) {
            return "text/html";
        } else {
            return "text/plain";
        }
    }

    private void respondError(int status, String statusText, HTTPResponse response) {
        byte[] responseBody = statusText.getBytes();

        response.setStatus(status);
        response.setStatusText(statusText);
        response.setHeader("Content-type", "text/plain");
        response.setHeader("Content-length", String.valueOf(responseBody.length));
        response.setBody(responseBody);
    }

    private void serveIndex(HTTPResponse response) {
        File file = new File(WEB_ROOT, DEFAULT_FILE);
        try {
            byte[] fileData = readFileData(file);
            int fileLength = fileData.length;
            String content = getContentType(file.getAbsolutePath());

            response.setHeader("Content-type", content);
            response.setHeader("Content-length", String.valueOf(fileLength));
            response.setBody(fileData);
        } catch (FileNotFoundException e) {
            respondError(404, "File not found", response);
        } catch (IOException e) {
            respondError(500, "Internal Server Error", response);
            LOG.log(Level.WARNING, "Error reading file", e);
        }
    }

}
