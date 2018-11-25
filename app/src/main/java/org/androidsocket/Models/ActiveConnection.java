package org.androidsocket.Models;

import org.androidsocket.Interfaces.Observer;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActiveConnection {
    public ActiveConnection(Long delay, WebSocket socket) {
        this(socket);
        latencies.add(delay);
    }

    public ActiveConnection(WebSocket socket) {
        this.latencies = new ArrayList<>();
        dateTime = Calendar.getInstance().getTime();
        this.localAddress = socket.getLocalSocketAddress().getAddress().toString();
        this.localPort = socket.getLocalSocketAddress().getPort();
        this.remoteAddress = socket.getRemoteSocketAddress().getAddress().toString();
        this.remotePort = socket.getRemoteSocketAddress().getPort();
        this.latencySum = 0L;
        this.misses = 0L;
    }

    public ActiveConnection addLatency(Long latency) {
        latencies.add(latency);
        latencySum += latency;
        return this;
    }

    public long getPingCount() {
        return this.latencies.size() + this.misses;
    }

    public double getAverageDelay() {
        if (latencies.size() == 0)
            return 0;
        else
            return latencySum / (double) latencies.size();
    }

    public String getDateTime(String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(this.dateTime);
    }

    public String getLocalAddress() {
        return this.localAddress;
    }

    public String getRemoteAddress() {
        return this.remoteAddress;
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public int getRemotePort() {
        return this.remotePort;
    }

    public long addMiss() {
        return ++this.misses;
    }

    public long getMisses() {
        return this.misses;
    }

    private Date dateTime;
    private ArrayList<Long> latencies;
    private Long latencySum;
    private int localPort;
    private int remotePort;
    private String localAddress;
    private String remoteAddress;
    private long misses;
}
