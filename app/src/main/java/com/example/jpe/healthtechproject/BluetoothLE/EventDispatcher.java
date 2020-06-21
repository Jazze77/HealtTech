package com.example.jpe.healthtechproject.BluetoothLE;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Jake on 14.3.2018.
 */

public class EventDispatcher {
    protected ArrayList<Listener> listenerList = new ArrayList<>();

    public void addEventListener(String type,IEventHandler handler){
        Listener listener = new Listener(type,handler);
        removeEventListener(type);
        listenerList.add(0,listener);
    }

    public void removeEventListener(String type){
        for(Iterator<Listener> iterator = listenerList.iterator(); iterator.hasNext();) {
            Listener listener = (Listener) iterator.next();
            if(listener.getType() == type){
                listenerList.remove(listener);
            }
        }
    }

    public void dispatchEvent(HeartRateInfo event){
        for(Iterator<Listener> iterator = listenerList.iterator();iterator.hasNext();) {
            Listener listener = (Listener) iterator.next();
            if(event.getStrType() == listener.getType()){
                IEventHandler eventHandler = listener.getHandler();
                eventHandler.callback(event);
            }
        }
    }

    public Boolean hasEventListener(String type){
        return false;
    }

    public void removeAllListeners(){

    }


}
