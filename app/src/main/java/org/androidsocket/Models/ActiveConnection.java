package org.androidsocket.Models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActiveConnection {
    public ActiveConnection(Long delay) {
        this();
        latencies = new ArrayList<>();
        latencies.add(delay);
    }

    public ActiveConnection() {
        dateTime = Calendar.getInstance().getTime();
    }

    public ActiveConnection addLatency(Long latency) {
        latencies.add(latency);
        return this;
    }

    public Date dateTime;
    public ArrayList<Long> latencies;
}
