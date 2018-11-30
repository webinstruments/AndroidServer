package org.androidsocket.Models;

import org.androidsocket.Interfaces.Observer;
import org.androidsocket.Interfaces.SignalObserver;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActiveConnection extends SignalObserver {
    public ActiveConnection(Long delay, WebSocket socket) {
        this(socket);
        this.latencies.add(new Latency(delay, super.signalType, super.signalStrength));
    }

    @Override
    public void onSignalUpdate(String strength, String type) {
        super.signalStrength = strength;
        super.signalType = type;
    }

    public ActiveConnection(WebSocket socket) {
        super();
        this.latencies = new ArrayDeque<>();
        this.dateTime = Calendar.getInstance().getTime();
        this.localAddress = socket.getLocalSocketAddress().getAddress().toString();
        this.localPort = socket.getLocalSocketAddress().getPort();
        this.remoteAddress = socket.getRemoteSocketAddress().getAddress().toString();
        this.remotePort = socket.getRemoteSocketAddress().getPort();
        this.latencySum = 0L;
        this.misses = new ArrayList<>();
        this.minLatency = 0L;
        this.maxLatency = 0L;
    }

    public ActiveConnection addLatency(Long latency) {
        this.latencies.addFirst(new Latency(latency,super.signalType, super.signalStrength));
        this.latencySum += latency;
        if(latency > this.maxLatency) {
            this.maxLatency = latency;
        } else if(this.minLatency == 0 || this.minLatency > latency) {
            this.minLatency = latency;
        }
        return this;
    }

    public long getPingCount() {
        return this.latencies.size() + this.misses.size();
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

    public String getFullRemoteAddress() {
        return this.getRemoteAddress().replace("/", "") + ":" + this.getRemotePort();
    }

    public long addMiss() {
        misses.add(new Miss(super.signalType, super.signalStrength));
        return this.misses.size();
    }

    public long getMissesCount() {
        return this.misses.size();
    }

    public long getMinLatency() {
        return this.minLatency;
    }

    public long getMaxLatency() {
        return this.maxLatency;
    }

    public ArrayList<Latency> getLatencies() {
        return new ArrayList<Latency>(this.latencies);
    }

    private Date dateTime;
    private Long latencySum;
    private Long minLatency;
    private Long maxLatency;
    private int localPort;
    private int remotePort;
    private String localAddress;
    private String remoteAddress;
    private ArrayList<Miss> misses;
    private ArrayDeque<Latency> latencies;
}
