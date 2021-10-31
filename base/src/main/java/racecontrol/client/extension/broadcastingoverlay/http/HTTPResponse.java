/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leonard
 */
public class HTTPResponse {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(HTTPResponse.class.getName());

    /**
     * Protocol version of the response.
     */
    private String version;
    /**
     * Status of this response.
     */
    private int status;
    /**
     * Status text for this response.
     */
    private String statusText;
    /**
     * Headers for this response.
     */
    private final Map<String, String> headers = new HashMap<>();
    /**
     * Response body.
     */
    private byte[] body = new byte[0];

    /**
     * Creates a new instance.
     *
     * @param version Protocol version.
     * @param status Status code.
     * @param statusText Status text.
     */
    public HTTPResponse(String version, int status, String statusText) {
        this.version = version;
        this.status = status;
        this.statusText = statusText;
    }

    /**
     * Creates a new instance with http version "HTTP/1.1".
     *
     * @param status status code.
     * @param statusText status text.
     */
    public HTTPResponse(int status, String statusText) {
        this("HTTP/1.1", status, statusText);
    }

    /**
     * Creates a new instance.
     */
    public HTTPResponse() {

    }

    /**
     * Set the protocol version.
     *
     * @param version the protocol version.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Set the status code.
     *
     * @param status The status code.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Set the status text.
     *
     * @param statusText the status text.
     */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    /**
     * Add a header to this response.
     *
     * @param headerKey The header key.
     * @param headerValue The header value.
     */
    public void setHeader(String headerKey, String headerValue) {
        headers.put(headerKey, headerValue);
    }

    /**
     * Set the body for this repsonse.
     *
     * @param bodyData The body data.
     */
    public void setBody(byte[] bodyData) {
        this.body = bodyData;
    }

    public String getVersion() {
        return version;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusText() {
        return statusText;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        String string = "HTTP Request: version: " + version
                + "\nstatus: " + status
                + "\nstatusText: " + statusText;
        for (String header : headers.keySet()) {
            string += String.format("\n%s:%s", header, headers.get(header));
        }
        string += "\nbody: " + new String(body);
        return string;
    }

    public byte[] getBytes() {
        var out = new ByteArrayOutputStream();
        var printer = new PrintWriter(out);

        printer.println(String.format("%s %d %s", version, status, statusText));
        for (var entry : headers.entrySet()) {
            printer.println(String.format("%s: %s", entry.getKey(), entry.getValue()));
        }
        printer.println();
        printer.flush();

        try {
            out.write(body);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error writing response body", e);
            return new byte[0];
        }

        return out.toByteArray();
    }

}
