package org.androidsocket;

import android.net.TrafficStats;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.logging.LogManager;

public class Server extends WebSocketServer {
    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        LogManager.getLogger().info("Client %s connected", getAddressFromWS(webSocket));
        Iterator<String> a = clientHandshake.iterateHttpFields();
        webSocket.send("Hallo Client");
        List<Draft> d = super.getDraft();
        int uid = android.os.Process.myUid();
        long txBytesInitial = TrafficStats.getUidTxBytes(uid);
        long rxBytesInitial = TrafficStats.getUidRxBytes(uid);
        webSocket.sendPing();
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        LogManager.getLogger().info("Client %s disconnected", getAddressFromWS(webSocket));
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        LogManager.getLogger().info("Client %s sent a message", getAddressFromWS(webSocket));
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        LogManager.getLogger().error("An error happened");
        LogManager.getLogger().error(e);
    }

    @Override
    public void onStart() {
        LogManager.getLogger().warning("Server started");
    }

    @Override
    public void onWebsocketPong( WebSocket webSocket, Framedata f ) {
        webSocket.send(String.format("%s", new Timestamp(System.currentTimeMillis())));
        LogManager.getLogger().info("Client %s ping received", getAddressFromWS(webSocket));
    }

    @Override
    public void broadcast(String text) {
        super.broadcast(text);
    }

    private String getAddressFromWS(WebSocket ws) {
        if(ws == null && ws.getRemoteSocketAddress() == null) {
            return "-";
        }
        return ws.getRemoteSocketAddress().getAddress().getHostAddress();
    }
}
