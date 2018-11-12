package org.androidsocket;

import android.net.TrafficStats;
import android.support.annotation.Nullable;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.logging.LogManager;

public class Server extends WebSocketServer {
    public Server(int port, int timeout) {
        super(new InetSocketAddress(port));
        this.poll = new Polling(this, timeout);
        this.pollThread = null;
        this.stopped = true;
    }

    public void setTimeout(int value) {
        this.setConnectionLostTimeout(value / 500);
        this.poll.setTimeout(value);
    }

    public int getTimeout() {
        if(this.poll != null) {
            return this.poll.getTimeout();
        } else {
            return 0;
        }
    }

    public Boolean isRunning() {
        return !stopped;
    }

    @Override
    public void stop() {
        try {
            super.stop();
            this.stopped = true;
            this.poll.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        LogManager.getLogger().info("Client %s connected", webSocket.toString());
        LogManager.getLogger().info("Client %s connected", WSUtils.getStreamFromWS(webSocket));
        webSocket.send("Hallo Client");
        int uid = android.os.Process.myUid();
        long txBytesInitial = TrafficStats.getUidTxBytes(uid);
        long rxBytesInitial = TrafficStats.getUidRxBytes(uid);
        org.androidsocket.Models.Connection.addConnection(webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        LogManager.getLogger().info("Client %s disconnected", webSocket.toString());
        LogManager.getLogger().info("Client %s disconnected", WSUtils.getStreamFromWS(webSocket));
        this.poll.removeConnection(webSocket);
        org.androidsocket.Models.Connection.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        LogManager.getLogger().info("Client %s sent a message", WSUtils.getStreamFromWS(webSocket));
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        LogManager.getLogger().error("An error happened");
        LogManager.getLogger().error(e);
    }

    @Override
    public void onStart() {
        LogManager.getLogger().warning("Server started");
        this.stopped = false;
        pollThread = new Thread(this.poll);
        pollThread.start();
    }

    @Override
    public void onWebsocketPong(WebSocket webSocket, Framedata f) {
        this.poll.pongReceived(webSocket);
    }

    @Override
    public void broadcast(String text) {
        super.broadcast(text);
    }

    private Polling poll;
    private Thread pollThread;
    private Boolean stopped;
}
