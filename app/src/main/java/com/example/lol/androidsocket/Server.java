package com.example.lol.androidsocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import Logging.LogManager;

public class Server extends WebSocketServer {
    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        LogManager.getLogger().info("Client %s connected", getAddressFromWS(webSocket));
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
        LogManager.getLogger().error("Client %s disconnected", getAddressFromWS(webSocket));
    }

    @Override
    public void onStart() {
        LogManager.getLogger().warning("Server started");

    }

    private String getAddressFromWS(WebSocket ws) {
        if(ws == null) {
            return "-";
        }
        return ws.getRemoteSocketAddress().getAddress().getHostAddress();
    }
}
