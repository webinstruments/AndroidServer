package org.androidsocket;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.logging.LogManager;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Polling implements Runnable {
    public Polling(WebSocketServer server, long timeOut) {
        this.server = server;
        this.timeOut = timeOut;
        this.pollingData = new ConcurrentHashMap<>();
        this.stopped = true;
    }

    public synchronized void sendPing(WebSocket socket) {
        if (!this.pollingData.containsKey(socket)) {
            LogManager.getLogger().info("1_Sending ping to client %s", WSUtils.getStreamFromWS(socket));
            this.pollingData.put(socket, System.currentTimeMillis());
            socket.sendPing();
        } else {
            LogManager.getLogger().warning("Key with connection %s already exists", WSUtils.getStreamFromWS(socket));
        }
    }

    public synchronized void pongReceived(WebSocket socket) {
        LogManager.getLogger().info("2_Pong from client %s received", WSUtils.getStreamFromWS(socket));
        if (this.pollingData.containsKey(socket)) {
            long elapsed = System.currentTimeMillis() - this.pollingData.remove(socket);
            LogManager.getLogger().info("3_Elapsed time %s ms", elapsed);
        } else {
            //warning will also be called if keep-alive-pings by the server are sent. -> Removed from library
            LogManager.getLogger().warning("Key with connection %s doesn't exist", WSUtils.getStreamFromWS(socket));
        }
    }

    public synchronized void stop() {
        this.stopped = false;
    }

    private synchronized boolean shouldStop() {
        return this.stopped;
    }

    @Override
    public void run() {
        this.stopped = false;
        DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS");
        while(!shouldStop()) {
            for (WebSocket s : server.getConnections()) {
                this.sendPing(s);
            }
            try {
                Thread.sleep(timeOut);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private WebSocketServer server;
    private Map<WebSocket, Long> pollingData;
    private long timeOut;
    private boolean stopped;
}
