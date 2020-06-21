package com.example.jpe.healthtechproject.BluetoothLE;

import android.util.Log;

/**
 * Created by Jake on 14.3.2018.
 */

public class MyCustomClass extends EventDispatcher {
private static MyCustomClass ourInstance = new MyCustomClass();

public static MyCustomClass getInstance() {
        return ourInstance;
        }

private MyCustomClass() {
        }

public void myCallback(){
        HeartRateInfo event = new HeartRateInfo(HeartRateInfo.COMPLETE,"my first param");
        Log.d("Event callback","i am about to dispatch event complete");
        dispatchEvent(event);
        }
        }
