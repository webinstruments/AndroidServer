package org.androidsocket;

import android.net.TrafficStats;

import org.androidsocket.Models.ConnectionData;
import org.androidsocket.Utils.WSUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.logging.LogManager;

public class Server extends WebSocketServer {

    public Server(int port, int timeout) {
        super(new InetSocketAddress(port));
        this.stopped = true;
        this.timeout = timeout;
        //Todo remove hard coded
        this.setConnectionLostTimeout(300);
    }

    public void setTimeout(int value) {
        if(this.poll != null) {
            this.poll.setTimeout(value);
        }
        this.timeout = value;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public boolean isRunning() {
        return !this.stopped;
    }

    public boolean isPollStarted() {
        if(this.poll != null) {
            return !this.poll.shouldStop();
        }
        return false;
    }

    @Override
    public void stop() {
        try {
            super.stop();
            this.stopped = true;
            this.stopPolling();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        super.start();
        this.stopped = false;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        LogManager.getLogger().info("Client %s connected", WSUtils.getStreamFromWS(webSocket));
        webSocket.send("Hallo Client");
        int uid = android.os.Process.myUid();
        long txBytesInitial = TrafficStats.getUidTxBytes(uid);
        long rxBytesInitial = TrafficStats.getUidRxBytes(uid);
        ConnectionData.addConnection(webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        LogManager.getLogger().info("Client %s disconnected", webSocket.toString());
        LogManager.getLogger().info("Client %s disconnected", WSUtils.getStreamFromWS(webSocket));
        this.poll.removeConnection(webSocket);
        ConnectionData.remove(webSocket);
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
    }

    public void startPolling() {
        if(this.pollThread == null) {
            this.poll = new Polling(this, this.timeout);
            this.pollThread = new Thread(this.poll);
            this.pollThread.start();
        }
    }

    public void stopPolling() {
        if(this.pollThread != null) {
            this.poll.stop();
            this.pollThread = null;
        }
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
    private boolean stopped;
    private int timeout;
}
