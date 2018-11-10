package org.androidsocket.Models;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;

public class Connection {
    public static synchronized void addConnection(WebSocket socket) {
        connections.add(socket.getRemoteSocketAddress().toString());
    }

    public static synchronized void addStreamAndDelay(WebSocket socket, Long delay) {
        String key = socket.getRemoteSocketAddress().toString();
        if(!socketAndDelay.containsKey(socket)) {
            ArrayList<Long> value = new ArrayList<Long>();
            value.add(delay);
            socketAndDelay.put(key, value);
        } else {
            ArrayList<Long> value = socketAndDelay.get(key);
            value.add(delay);
            socketAndDelay.put(key, value);
        }
    }
    private static HashMap<String, ArrayList<Long>> socketAndDelay;
    private static ArrayList<String> connections;
}
