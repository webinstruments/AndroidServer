package org.androidsocket.Models;

public class Miss {
    public Miss(String serviceType, String signalStrength) {
        this.strength = signalStrength;
        this.serviceName = serviceType;
    }

    public String getSignalStrength() {
        return this.strength;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    private String strength;
    private String serviceName;
}
