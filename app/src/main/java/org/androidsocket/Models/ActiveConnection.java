package org.androidsocket.Models;

import org.androidsocket.Interfaces.Observer;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActiveConnection {
    public ActiveConnection(Long delay, WebSocket socket) {
        this(socket);
        this.latencies.add(delay);
    }

    public ActiveConnection(WebSocket socket) {
        this.latencies = new ArrayDeque<>();
        this.dateTime = Calendar.getInstance().getTime();
        this.localAddress = socket.getLocalSocketAddress().getAddress().toString();
        this.localPort = socket.getLocalSocketAddress().getPort();
        this.remoteAddress = socket.getRemoteSocketAddress().getAddress().toString();
        this.remotePort = socket.getRemoteSocketAddress().getPort();
        this.latencySum = 0L;
        this.misses = 0L;
        this.minLatency = 0L;
        this.maxLatency = 0L;
    }

    public ActiveConnection addLatency(Long latency) {
        this.latencies.addFirst(latency);
        this.latencySum += latency;
        if(latency > this.maxLatency) {
            this.maxLatency = latency;
        } else if(this.minLatency == 0 || this.minLatency > latency) {
            this.minLatency = latency;
        }
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

    public long getMinLatency() {
        return this.minLatency;
    }

    public long getMaxLetency() {
        return this.maxLatency;
    }

    public ArrayList<Long> getLatencies() {
        return new ArrayList<Long>(this.latencies);
    }

    private Date dateTime;
    private Long latencySum;
    private Long minLatency;
    private Long maxLatency;
    private int localPort;
    private int remotePort;
    private String localAddress;
    private String remoteAddress;
    private long misses;
    private ArrayDeque<Long> latencies;
}
