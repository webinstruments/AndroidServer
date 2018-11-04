package org.androidsocket;

import android.net.TrafficStats;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.logging.LogManager;

public class Server extends WebSocketServer {
    public Server(int port) {
        super(new InetSocketAddress(port));
        this.poll = new Polling(this, 1000);
        this.pollThread = null;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        LogManager.getLogger().info("Client %s connected", WSUtils.getStreamFromWS(webSocket));
        webSocket.send("Hallo Client");
        int uid = android.os.Process.myUid();
        long txBytesInitial = TrafficStats.getUidTxBytes(uid);
        long rxBytesInitial = TrafficStats.getUidRxBytes(uid);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        LogManager.getLogger().info("Client %s disconnected", WSUtils.getStreamFromWS(webSocket));
        this.poll.removeConnection(webSocket);
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
}
