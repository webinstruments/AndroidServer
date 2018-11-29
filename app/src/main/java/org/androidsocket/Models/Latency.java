package org.androidsocket.Models;

public class Latency {
    public Latency(Long latency, String serviceType, String signalStrength) {
        this.latency = latency;
        this.serviceName = serviceType;
        this.strength = signalStrength;
    }

    public Long latency;
    public String strength;
    public String serviceName;
}
