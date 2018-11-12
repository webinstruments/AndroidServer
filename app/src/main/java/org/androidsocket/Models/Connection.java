package org.androidsocket.Models;

import org.androidsocket.Interfaces.Observer;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Connection {
    public static synchronized void addConnection(WebSocket socket) {
        if(!socketAndDelay.containsKey(getKey(socket))) {
            socketAndDelay.put(getKey(socket), new ActiveConnection());
        }
        notifiyObservers();
    }

    public static synchronized void addStreamAndDelay(WebSocket socket, Long delay) {
        String key = getKey(socket);
        if(!socketAndDelay.containsKey(socket)) {
            socketAndDelay.put(key, new ActiveConnection(delay));
        } else {
            ActiveConnection value = socketAndDelay.get(key).addLatency(delay);
            socketAndDelay.put(key, value);
        }
        notifiyObservers();
    }

    public static synchronized boolean remove(WebSocket socket) {
        if(socketAndDelay.containsKey(getKey(socket))) {
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
        for(Observer obs : observers) {
            obs.update();
        }
    }

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    private static String getKey(WebSocket socket) {
        return socket.getRemoteSocketAddress().toString();
    }

    static Map<String, ActiveConnection> socketAndDelay = new ConcurrentHashMap<>();
    static ArrayList<Observer> observers = new ArrayList<Observer>();
}
