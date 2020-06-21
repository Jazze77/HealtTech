package com.example.jpe.healthtechproject.BluetoothLE;
/**
 * Created by jpe on 1.3.2018.
 */

// HR example code
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpe.healthtechproject.R;
import com.example.jpe.healthtechproject.BluetoothLE.BluetoothLEHandler;

public class PolarBluetoothLE extends Activity  implements  AdapterView.OnItemClickListener {

    private Button btn_Save;
    private Button btn_Scan;
   // private CheckBox checkBox_Save;
    private TextView textViewDebug;
    private TextView textViewAutoConnectDevice;


    private ListAdapter_BTLE_Devices adapter;
    private ListView listView;
    private boolean isListViewClickable = true;

    private boolean mScanning = false;
    private int mScanningposition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout.
        setContentView(R.layout.activity_polar_bluetooth_le);

        btn_Scan = (Button) findViewById(R.id.btn_scan);
        btn_Scan.setBackgroundResource(android.R.drawable.btn_default);
        btn_Save = (Button)findViewById(R.id.btn_save);
       // checkBox_Save = (CheckBox)findViewById(R.id.checkbox_save);

        //currentDeviceInfo
        adapter = new ListAdapter_BTLE_Devices(this,
                R.layout.btle_device_list_item,
                BluetoothLEHandler.getInstance().GetBluetoothDevices());

        listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        ((ScrollView) findViewById(R.id.scrollViewBTLE)).addView(listView);

        textViewAutoConnectDevice = (TextView) findViewById(R.id.text_autoconnect_deviceInfo);
        textViewDebug = (TextView) findViewById(R.id.text_debug);

        SharedPreferences prefs = getSharedPreferences(BluetoothLEHandler.MY_PREFS_NAME, MODE_PRIVATE);

        boolean autoConnect = prefs.getBoolean("AutoStart",false);

        if (autoConnect == true) {
            textViewAutoConnectDevice.setText("" +
                    "\nDevice name : "+ prefs.getString("DeviceName",null ));

        }
        else {
            textViewAutoConnectDevice.setText("\n No device");
        }


        //textViewDevice.setText(BluetoothLEHandler.getInstance().GetCurrentBTDeviceInfo());
        PulseTask();
    }

    public void StartBluetoothScan(View view) {


        if (mScanning) {

            StopBluetoothScan();
        }
        else {

            btn_Scan.setText("STOP");
            mScanningposition = 0;
            mScanning = true;

            BluetoothLEHandler.getInstance().StartBluetoothLEScan(10000);
            listView.setAdapter(adapter); // set empty list


            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //UpdateUiList
                    listView.setAdapter(adapter);
                }

                public void onFinish() {
                    StopBluetoothScan();
                }
            }.start();
        }
    }

    public void StopBluetoothScan() {

        mScanning = false;

        btn_Scan.setText("SCAN");
        BluetoothLEHandler.getInstance().StopBluetoothLEScan();
       // textViewDevice.setText(BluetoothLEHandler.getInstance().GetCurrentBTDeviceInfo());
        listView.setBackgroundColor(Color.WHITE);


        for (int i = 0; i < adapter.getCount();i++){
            adapter.getItem(i).setUiInfo("");
        }
        listView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(!isListViewClickable){
            return;
        }


        /*
        for (int i = 0; i < parent.getCount();i++){
            parent.getChildAt(i).setBackgroundColor(Color.WHITE);
        }
        */

        String name = BluetoothLEHandler.getInstance().GetBluetoothDevices().get(position).getName();
        String deviceId = "";

        if (BluetoothLEHandler.getInstance().GetBluetoothDevices().get(position).getName().startsWith("Polar")){

            if (name.length() >= 8){

                deviceId = name.substring(name.length()-8);

                BluetoothLEHandler.getInstance().SetConnectedDeviceId(deviceId);

                isListViewClickable = false;

                //listView.setBackgroundColor(Color.WHITE);
                //parent.getChildAt(position).setBackgroundColor(Color.RED);
                //view.setClickable(false);

                mScanningposition = position;

                adapter.getItem(position).setUiInfo("Connecting ...");
                listView.setAdapter(adapter);
               // BluetoothLEHandler.getInstance().GetBluetoothDevices()
                BluetoothLEHandler.getInstance().ConnectBTDevice(position);
                StartCountDownTimer();
            }
            else {

                Toast t = Toast.makeText(this, "Polar device id error", Toast.LENGTH_SHORT);
                t.show();
            }

        }
        else{
            //Toast t = Toast.makeText(this,"No Polar",Toast.LENGTH_SHORT);
            //t.show();

            deviceId = "12345678";
            isListViewClickable = false;
            BluetoothLEHandler.getInstance().SetConnectedDeviceId(deviceId);
            //parent.getChildAt(position).setBackgroundColor(Color.RED);
            adapter.getItem(position).setUiInfo("Pairing ...");
            mScanningposition = position;
            listView.setAdapter(adapter);
            //view.setClickable(false);
            BluetoothLEHandler.getInstance().ConnectBTDevice(position);
            StartCountDownTimer();

            Toast t = Toast.makeText(this,"Test fake Polar device id = 12345678",Toast.LENGTH_SHORT);
            t.show();

        }

    }

    private void StartCountDownTimer(){

        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {

                if (BluetoothLEHandler.getInstance().Isconnected()){
                    adapter.getItem(mScanningposition).setUiInfo("Connected");
                }

                //count++;
                //btn_Scan.setText("Scanning..." + String.valueOf(count) + "/10 sec" );
            }
            public void onFinish(){

                CheckBTConnectionResult();
            }
        }.start();


    }

    private void CheckBTConnectionResult(){
        isListViewClickable = true;
       // adapter.getItem(position).setBackgroundColor(Color.WHITE);

        for (int i = 0; i < adapter.getCount();i++){
            adapter.getItem(i).setUiInfo("");
        }
        listView.setAdapter(adapter);

        if ( BluetoothLEHandler.getInstance().Isconnected() ){
            adapter.getItem(mScanningposition).setUiInfo("Connected");
            //Toast t = Toast.makeText(this,"Connect ok",Toast.LENGTH_SHORT);

            //t.show();
        }
        else {

            Toast t = Toast.makeText(this, "Connect failed", Toast.LENGTH_SHORT);
            t.show();
            for (int i = 0; i < adapter.getCount();i++){
                adapter.getItem(i).setUiInfo("");
            }

        }
    }


    private void PulseTask(){

        Thread t=new Thread(){


            @Override
            public void run(){

                while(!isInterrupted()){

                    try {
                        Thread.sleep(1000);  //1000ms = 1 sec

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                textViewDebug.setText(BluetoothLEHandler.getInstance().getDebugInfo());

                            }
                        }); // End of runOnUiThread

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } // end of while
            }
        }; // End of Thread

        t.start();

    }


    public void SaveBluetoothSettings(View view) {



        if ( BluetoothLEHandler.getInstance().Isconnected()) {

            String deviceId = BluetoothLEHandler.getInstance().GetConnectedDeviceId();
            if (deviceId != null) {

                //SharedPreferences sharedPref = getSharedPreferences("mypref", 0);
                SharedPreferences.Editor editor = getSharedPreferences(BluetoothLEHandler.MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("AutoStart", true);
                editor.putString("DeviceId", deviceId);
                editor.putString("DeviceName",BluetoothLEHandler.getInstance().GetCurrentBTDevice().getName());
                //editor.putString("DeviceAddress",BluetoothLEHandler.getInstance().GetCurrentBTDevice().getAddress());
                editor.apply();

                Toast t = Toast.makeText(this, "Save ok", Toast.LENGTH_SHORT);
                t.show();
            } else {
                Toast t = Toast.makeText(this, "Save autoconnect failed", Toast.LENGTH_SHORT);
                t.show();
            }
        }else {
            Toast t = Toast.makeText(this, "Save autoconnect failed2", Toast.LENGTH_SHORT);
            t.show();

        }

        SharedPreferences prefs = getSharedPreferences(BluetoothLEHandler.MY_PREFS_NAME, MODE_PRIVATE);

        textViewAutoConnectDevice.setText("" +
                "\nDevice name : "+ prefs.getString("DeviceName",null ));

    }

    public void ResetBluetoothSettings(View view) {

        SharedPreferences.Editor editor = getSharedPreferences(BluetoothLEHandler.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("AutoStart", false);
        editor.putString("DeviceId", null);
        editor.putString("DeviceName",null);
        editor.apply();

        Toast t = Toast.makeText(this, "Reset ok", Toast.LENGTH_SHORT);
        t.show();

        SharedPreferences prefs = getSharedPreferences(BluetoothLEHandler.MY_PREFS_NAME, MODE_PRIVATE);

        textViewAutoConnectDevice.setText("" +
                "\nDevice name : "+ prefs.getString("DeviceName",null ));

    }
}

