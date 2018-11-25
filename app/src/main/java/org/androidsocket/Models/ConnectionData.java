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
        if (!socketAndDelay.containsKey(socket)) {
            socketAndDelay.put(socket, new ActiveConnection(socket));
        }
        notifyObservers();
    }

    public static synchronized void addStreamAndDelay(WebSocket socket, Long delay) {
        if (!socketAndDelay.containsKey(socket)) {
            socketAndDelay.put(socket, new ActiveConnection(delay, socket));
        } else {
            ActiveConnection value = socketAndDelay.get(socket).addLatency(delay);
            socketAndDelay.put(socket, value);
        }
        notifyObservers();
    }

    public static synchronized long addMiss(WebSocket socket) {
        if(!socketAndDelay.containsKey(socket)) {
            addConnection(socket);
        }
        long result = socketAndDelay.get(socket).addMiss();
        notifyObservers();
        return result;
    }

    public static synchronized boolean remove(WebSocket socket) {
        if (socketAndDelay.containsKey(socket)) {
            socketAndDelay.remove(socket);
            notifyObservers();
            return true;
        }
        return false;
    }

    public static synchronized Map<WebSocket, ActiveConnection> getConnections() {
        return socketAndDelay;
    }

    public static synchronized int count() {
        return socketAndDelay.size();
    }

    public static ArrayList<ActiveConnection> getActiveConnections() {
        ArrayList<ActiveConnection> result = new ArrayList<>();
        for (Map.Entry<WebSocket, ActiveConnection> entry : socketAndDelay.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    public static ActiveConnection getConnectionFromIndex(int index) {
        int i = 0;
        for (Map.Entry<WebSocket, ActiveConnection> entry : socketAndDelay.entrySet()) {
            if(i++ == index) {
                return entry.getValue();
            }
        }

        return null;
    }

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    public static void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private static void notifyObservers() {
        for (Observer obs : observers) {
            obs.update();
        }
    }

    static Map<WebSocket, ActiveConnection> socketAndDelay = new ConcurrentHashMap<>();
    static ArrayList<Observer> observers = new ArrayList<>();
}
