package org.androidsocket.Models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import org.androidsocket.Utils.Constants;
import org.androidsocket.Interfaces.SignalObserver;
import org.androidsocket.R;
import org.androidsocket.Utils.WSUtils;
import org.logging.LogManager;

import java.util.ArrayList;
import java.util.List;

public class SignalInfo extends PhoneStateListener {

    public SignalInfo(Context context) {
        this.context = context;
        this.observers = new ArrayList<>();
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        this.signalStrength = signalStrength.getGsmSignalStrength();
        this.update();
    }

    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);
        setServiceName(networkType);
        this.update();
    }

    public void setServiceName(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                this.serviceName = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                this.serviceName = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                this.serviceName = "4G";
                break;
            default:
                this.serviceName = "Unknown";
        }
    }

    public String getSignalStrength() {
        if(this.signalStrength <= Constants.CONNECTION_BAD_THRESHOLD) {
            return context.getString(R.string.bad);
        } else if (this.signalStrength <= Constants.CONNECTION_MEDIUM_THRESHOLD) {
            return context.getString(R.string.medium);
        } else if (this.signalStrength <= Constants.CONNECTION_GOOD_THRESHOLD) {
            return context.getString(R.string.good);
        } else {
            return context.getString(R.string.best);
        }
    }

    public void register(SignalObserver observer) {
        observers.add(observer);
    }

    public void unregister(SignalObserver observer) {
        observers.remove(observer);
    }

    public String getSignalType() {
        return this.serviceName;
    }

    private void update() {
        for (SignalObserver observer: this.observers) {
            observer.onSignalUpdate(this.getSignalStrength(), this.getSignalType());
        }
    }

    private int signalStrength;
    private String serviceName;
    private Context context;
    private List<SignalObserver> observers;
}
