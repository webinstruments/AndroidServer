package org.androidsocket.Models;

public class Latency {
    public Latency(Long latency, String serviceName, int signalStrength) {
        this.latency = latency;
        this.serviceName = serviceName;
        this.strength = signalStrength;
    }

    public Long latency;
    public int strength;
    public String serviceName;
}
