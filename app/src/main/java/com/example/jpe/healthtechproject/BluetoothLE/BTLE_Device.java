package com.example.jpe.healthtechproject.BluetoothLE;

import android.bluetooth.BluetoothDevice;

/**
 * Created by jpe on 1.3.2018.
 */

public class BTLE_Device {

    private BluetoothDevice bluetoothDevice;
    private int rssi;
    private String UiInfo = " ";

    public BTLE_Device(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    public String getName() {
        return bluetoothDevice.getName();
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
    }

    public int getRSSI() {
        return rssi;
    }

    public BluetoothDevice GetBTLE_Device() { return bluetoothDevice;} // OMA Lisäys


    public String getUiInfo() {
        return UiInfo;
    }

    public void setUiInfo(String uiInfo) {
        UiInfo = uiInfo;
    }

}
