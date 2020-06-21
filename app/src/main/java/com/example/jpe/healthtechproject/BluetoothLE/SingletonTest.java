package com.example.jpe.healthtechproject.BluetoothLE;

import android.app.Activity;
import android.app.Application;

/**
 * Created by jpe on 2.3.2018.
 */


/*

// Application class is a singleton by nature.
    private static MyApplication mSingleton;

    //
     * Get instance to the (singleton) application class from anywhere.
     *
     * @return the one and only instance of the application class.
     *
public static MyApplication getInstance() {
        return mSingleton;
        }


@Override
public void onCreate() {
        super.onCreate();
        Logger.logD(TAG, "******************************************************************");
        Logger.logF();

        Log.d("Logger", "Logging enabled = "  + fi.finwe.util.BuildConfig.LOGGING);
        Log.d("Logger", "Func logging enabled = "  + fi.finwe.util.BuildConfig.FUNC_LOGGING);
        Log.d("Logger", "File logging enabled = "  + fi.finwe.util.BuildConfig.FILE_LOGGING);

        // When Application implementation is registered in the manifest,
        // it will be instantiated when the application process is created.
        // Be aware that Content Provider's onCreate() is called before this!

        // This is the one and only instance of the Application implementation.
        // It is accessed as a singleton.
        mSingleton = this;



        */

public class SingletonTest extends Application{
   // private static final SingletonTest ourInstance = new SingletonTest();
   private static SingletonTest ourInstance;

    public static SingletonTest getInstance() {
        return ourInstance;
    }

    /*
    private SingletonTest() {
    }*/


    @Override
    public void onCreate() {
        super.onCreate();

        // When Application implementation is registered in the manifest,
        // it will be instantiated when the application process is created.
        // Be aware that Content Provider's onCreate() is called before this!

        // This is the one and only instance of the Application implementation.
        // It is accessed as a singleton.
        ourInstance = this;
    }

}
