/*
 * Copyright (c) 2021 Leonard Schüngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.broadcastingoverlay;

import java.net.Socket;
import racecontrol.client.extension.broadcastingoverlay.http.HTTPRequest;
import racecontrol.client.extension.broadcastingoverlay.websocket.WebSocketConnection;

/**
 *
 * @author Leonard
 */
public interface WebSocketConnectionCallback {
    
    public void requestWebSocket(Socket s, HTTPRequest request);
    
    public void closeWebSocket(WebSocketConnection connection);
    
}
