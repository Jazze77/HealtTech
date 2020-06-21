package com.example.jpe.healthtechproject.BluetoothLE;

/**
 * Created by Jake on 14.3.2018.
 */

public class Listener {
    private String type;
    private IEventHandler handler;

    public Listener(String type,IEventHandler handler){
        this.type = type;
        this.handler = handler;
    }

    public String getType(){
        return this.type;
    }

    public IEventHandler getHandler(){
        return this.handler;
    }
}
