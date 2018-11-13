package org.androidsocket.Models;

import org.androidsocket.Interfaces.Observer;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionData {
    public static synchronized void addConnection(WebSocket socket) {
        if (!socketAndDelay.containsKey(getKey(socket))) {
            socketAndDelay.put(getKey(socket), new ActiveConnection(socket));
        }
        notifiyObservers();
    }

    public static synchronized void addStreamAndDelay(WebSocket socket, Long delay) {
        String key = getKey(socket);
        if (!socketAndDelay.containsKey(socket)) {
            socketAndDelay.put(key, new ActiveConnection(delay, socket));
        } else {
            ActiveConnection value = socketAndDelay.get(key).addLatency(delay);
            socketAndDelay.put(key, value);
        }
        notifiyObservers();
    }

    public static synchronized boolean remove(WebSocket socket) {
        if (socketAndDelay.containsKey(getKey(socket))) {
            socketAndDelay.remove(getKey(socket));
            notifiyObservers();
            return true;
        }
        return false;
    }

    public static synchronized Map<String, ActiveConnection> getConnections() {
        return socketAndDelay;
    }

    public static synchronized int count() {
        return socketAndDelay.size();
    }

    public static void notifiyObservers() {
        for (Observer obs : observers) {
            obs.update();
        }
    }

    public static Object[][] getTableData() {
        Object[][] result = new Object[socketAndDelay.size()][3];

        int row = 0, col = 0;
        for (Map.Entry<String, ActiveConnection> entry : socketAndDelay.entrySet()) {
            result[row][col++] = entry.getValue().getRemoteAddress() + ":" + entry.getValue().getRemotePort();
            result[row][col++] = entry.getValue().getAverage();
            result[row++][col++] = entry.getValue().getDateTime("HH:mm:ss");
            col %= 3;
        }

        return result;
    }

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    private static String getKey(WebSocket socket) {
        return socket.getRemoteSocketAddress().toString();
    }

    static Map<String, ActiveConnection> socketAndDelay = new ConcurrentHashMap<>();
    static ArrayList<Observer> observers = new ArrayList<>();
}
