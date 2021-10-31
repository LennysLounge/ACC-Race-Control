/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Leonard
 */
public class HTTPRequest {

    /**
     * Method for this request.
     */
    private Method method;
    /**
     * Target for this request.
     */
    private String target;
    /**
     * The HTTP version of this request.
     */
    private String version;
    /**
     * Request headers.
     */
    private final Map<String, String> headers = new HashMap<>();
    /**
     * Request body.
     */
    private String body;

    /**
     * Creates a new instance.
     */
    public HTTPRequest() {
    }

    public void read(InputStream inputStream)
            throws IOException, IllegalArgumentException {
        var in = new BufferedReader(new InputStreamReader(inputStream));

        String[] startLine = in.readLine().split(" ");
        if (startLine.length != 3) {
            throw new IllegalArgumentException("Malformed HTTP Start line: " + Arrays.toString(startLine));
        }

        method = Method.valueOf(startLine[0]);
        target = startLine[1];
        version = startLine[2];

        // read headers.
        while (in.ready()) {
            String line = in.readLine();
            if (line.isEmpty()) {
                break;
            }
            String[] header = line.split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0], header[1]);
            }
        }

        // read body.
        while (in.ready()) {
            body += in.readLine();
        }
    }

    public Method getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }

    public boolean hasHeader(String headerKey) {
        return headers.containsKey(headerKey);
    }

    public String getHeader(String headerKey) {
        return headers.getOrDefault(headerKey, "");
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        String string = "HTTP Request: Method: " + method.name()
                + "\ntarget: " + target
                + "\nversion: " + version;
        for (String header : headers.keySet()) {
            string += String.format("\n%s:%s", header, headers.get(header));
        }
        string += "\nbody: " + body;
        return string;
    }

    /**
     * HTTP Methods.
     */
    public enum Method {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        CONNECT,
        OPTIONS,
        TRACE,
        PATCH;
    }
}
