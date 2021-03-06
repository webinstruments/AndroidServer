package org.androidsocket.Models;

import java.util.Date;

public class Latency {
    public Latency(Long latency, String serviceType, String signalStrength) {
        this.latency = latency;
        this.serviceName = serviceType;
        this.strength = signalStrength;
    }

    public Long getLatency() {
        return this.latency;
    }

    public String getSignalStrength() {
        return this.strength;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    private Long latency;
    private String strength;
    private String serviceName;
}
