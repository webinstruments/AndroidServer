package org.androidsocket;

import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;

public class WSUtils {
    public static String getStreamFromWS(WebSocket ws) {
        return ws.getLocalSocketAddress().toString() + ", " + ws.getRemoteSocketAddress().toString();
    }
}
