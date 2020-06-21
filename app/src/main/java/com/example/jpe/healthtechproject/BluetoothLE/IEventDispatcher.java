package com.example.jpe.healthtechproject.BluetoothLE;

/**
 * Created by Jake on 14.3.2018.
 */

public interface IEventDispatcher {
    public void addEventListener(String type,IEventHandler cbInterface);
    public void removeEventListener(String type);
    public void dispatchEvent(HeartRateInfo event);
    public Boolean hasEventListener(String type);
    public void removeAllListeners();
}
