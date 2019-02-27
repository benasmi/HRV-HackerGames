package com.mabe.productions.hrv_madison.bluetooth;

import android.bluetooth.BluetoothDevice;

public class DeviceViewInfo {

    private boolean isNearby;
    private boolean isBonded;
    private boolean isInProgress;
    private BluetoothDevice device;


    public DeviceViewInfo(boolean isNearby, boolean isBonded, boolean isInProgress, BluetoothDevice device) {
        this.isNearby = isNearby;
        this.isBonded = isBonded;
        this.isInProgress = isInProgress;
        this.device = device;
    }


    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }

    public boolean isBonded() {
        return isBonded;
    }

    public void setBonded(boolean bonded) {
        isBonded = bonded;
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        isInProgress = inProgress;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
