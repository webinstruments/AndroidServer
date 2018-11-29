package org.androidsocket.Interfaces;

import org.androidsocket.Models.ConnectionData;
import org.androidsocket.Models.SignalInfo;

public abstract class SignalObserver {
    public SignalObserver() {
        this.info = ConnectionData.getSignalInfo();
        if(this.info != null) {
            this.info.register(this);
            this.signalStrength = this.info.getSignalStrength();
            this.signalType = this.info.getSignalType();
        } else {
            this.signalStrength = "-";
            this.signalType = "-";
        }
    }

    public abstract void onSignalUpdate(String strength, String  type);

    protected SignalInfo info;
    protected String signalStrength;
    protected String signalType;
}
