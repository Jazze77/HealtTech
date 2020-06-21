package com.example.jpe.healthtechproject.BluetoothLE;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jpe on 3.3.2018.
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

public class BluetoothLEHandler extends Application {
    // private static final BluetoothLEHandler ourInstance = new BluetoothLEHandler();

    // HR example code
    public enum AD_TYPE
    {
        GAP_ADTYPE_UNKNOWN(0),
        GAP_ADTYPE_FLAGS(1)                         ,
        GAP_ADTYPE_16BIT_MORE(2)                    , //!< Service: More 16-bit UUIDs available
        GAP_ADTYPE_16BIT_COMPLETE(3)                , //!< Service: Complete list of 16-bit UUIDs
        GAP_ADTYPE_32BIT_MORE(4)                    , //!< Service: More 32-bit UUIDs available
        GAP_ADTYPE_32BIT_COMPLETE(5)                , //!< Service: Complete list of 32-bit UUIDs
        GAP_ADTYPE_128BIT_MORE(6)                   , //!< Service: More 128-bit UUIDs available
        GAP_ADTYPE_128BIT_COMPLETE(7)               , //!< Service: Complete list of 128-bit UUIDs
        GAP_ADTYPE_LOCAL_NAME_SHORT(8)              , //!< Shortened local name
        GAP_ADTYPE_LOCAL_NAME_COMPLETE(9)           , //!< Complete local name
        GAP_ADTYPE_POWER_LEVEL(10)                  , //!< TX Power Level: 0xXX: -127 to +127 dBm
        GAP_ADTYPE_OOB_CLASS_OF_DEVICE(11)          , //!< Simple Pairing OOB Tag: Class of device (3 octets)
        GAP_ADTYPE_OOB_SIMPLE_PAIRING_HASHC(12)     , //!< Simple Pairing OOB Tag: Simple Pairing Hash C (16 octets)
        GAP_ADTYPE_OOB_SIMPLE_PAIRING_RANDR(13)     , //!< Simple Pairing OOB Tag: Simple Pairing Randomizer R (16 octets)
        GAP_ADTYPE_SM_TK(14)                        , //!< Security Manager TK Value
        GAP_ADTYPE_SM_OOB_FLAG(15)                  , //!< Secutiry Manager OOB Flags
        GAP_ADTYPE_SLAVE_CONN_INTERVAL_RANGE(16)    , //!< Min and Max values of the connection interval (2 octets Min, 2 octets Max) (0xFFFF indicates no conn interval min or max)
        GAP_ADTYPE_SIGNED_DATA(17)                  , //!< Signed Data field
        GAP_ADTYPE_SERVICES_LIST_16BIT(18)          , //!< Service Solicitation: list of 16-bit Service UUIDs
        GAP_ADTYPE_SERVICES_LIST_128BIT(19)         , //!< Service Solicitation: list of 128-bit Service UUIDs
        GAP_ADTYPE_SERVICE_DATA(20)                 , //!< Service Data
        GAP_ADTYPE_MANUFACTURER_SPECIFIC(0xFF);       //!< Manufacturer Specific Data: first 2 octets contain the Company Identifier Code followed by the additional manufacturer specific data

        private int numVal;

        AD_TYPE(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }

    };

    public static AD_TYPE getCode(byte type){
        try {
            return type == -1 ? AD_TYPE.GAP_ADTYPE_MANUFACTURER_SPECIFIC : AD_TYPE.values()[type];
        }catch (ArrayIndexOutOfBoundsException ex){
            return AD_TYPE.GAP_ADTYPE_UNKNOWN;
        }
    }

    public static HashMap<AD_TYPE,byte[]> advertisementBytes2Map(byte[] record){
        int offset=0;
        HashMap<AD_TYPE,byte[]> adTypeHashMap = new HashMap<>();
        try {
            while ((offset + 2) < record.length) {
                AD_TYPE type = getCode(record[offset + 1]);
                int fieldLen = record[offset];
                if (fieldLen <= 0) {
                    // skip if incorrect adv is detected
                    break;
                }
                if (adTypeHashMap.containsKey(type) && type == AD_TYPE.GAP_ADTYPE_MANUFACTURER_SPECIFIC) {
                    byte data[] = new byte[adTypeHashMap.get(type).length + fieldLen - 1];
                    System.arraycopy(record, offset + 2, data, 0, fieldLen - 1);
                    System.arraycopy(adTypeHashMap.get(type), 0, data, fieldLen - 1, adTypeHashMap.get(type).length);
                    adTypeHashMap.put(type, data);
                } else {
                    byte data[] = new byte[fieldLen - 1];
                    System.arraycopy(record, offset + 2, data, 0, fieldLen - 1);
                    adTypeHashMap.put(type, data);
                }
                offset += fieldLen + 1;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            // corrupted adv data find
        }
        return adTypeHashMap;
    }

    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private BluetoothDevice mCurrectBTDevice;

    private BluetoothAdapter bluetoothAdapter;
    private Context context;

    public static final int BT_SCAN_ALL = 1;
    public static final int BT_TRY_TO_CONNECT = 2;
    public static final int BT_SEND_GATT = 3;
    public static final int BT_CONNECTED = 4;
    public static final int BT_CONNECTED_NO_PULSE = 5;
    public static final int BT_NONE = 6;

    private int currentState = BT_NONE;

    private int hrRate;
    private int hrCounter = 0;
    private int scanCounter = 0;
    private int maxTimer = 0;

    private String mConnectedDeviceID = "";

    private VideoHeartRateSummary vhrSUm;

    private ArrayList<String> hrRates;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private static BluetoothLEHandler ourInstance;

    public static BluetoothLEHandler getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // When Application implementation is registered in the manifest,
        // it will be instantiated when the application process is created.
        // Be aware that Content Provider's onCreate() is called before this!

        // This is the one and only instance of the Application implementation.
        // It is accessed as a singleton.
        ourInstance = this;

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();


        mCurrectBTDevice = null;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();

        DebugTask task = new DebugTask();
        task.execute(this);

/*
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("AutoStart", true);
        editor.putString("DeviceId",null);
        editor.apply();
*/
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        boolean autoConnect = prefs.getBoolean("AutoStart",false);
        mConnectedDeviceID = prefs.getString("DeviceId",null);
        if (autoConnect == true) {
           AutoConnectMyPolar();
        }

        //InitBluetoothLEScan(7000);
        //AutoConnectMyPolar();

        String test12 = "";
        vhrSUm = new VideoHeartRateSummary(test12);

    }


    public final UUID HR_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public final UUID HR_SERVICE = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    public static final UUID DESCRIPTOR_CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            processDeviceDiscovered(result.getDevice(),result.getRssi(),result.getScanRecord().getBytes());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            UpdateHRRate(-11);
        }
    };

    private  BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            processDeviceDiscovered(device,rssi,scanRecord);
        }
    };

    private void processDeviceDiscovered(final BluetoothDevice device, int rssi, byte[] scanRecord){

        //if(currentState == BT_SCAN_ALL)
        //{

        String address = device.getAddress();

        if (!mBTDevicesHashMap.containsKey(address)) {
            BTLE_Device btleDevice = new BTLE_Device(device);
            btleDevice.setRSSI(rssi);

            mBTDevicesHashMap.put(address, btleDevice);
            mBTDevicesArrayList.add(btleDevice);
        }
        else {
            mBTDevicesHashMap.get(address).setRSSI(rssi);

        }

        //}
        if (currentState == BT_TRY_TO_CONNECT  ){
            Map<AD_TYPE, byte[]> content = advertisementBytes2Map(scanRecord);
            if (content.containsKey(AD_TYPE.GAP_ADTYPE_LOCAL_NAME_COMPLETE)) {
                String name = new String(content.get(AD_TYPE.GAP_ADTYPE_LOCAL_NAME_COMPLETE));
                if (name.startsWith("Polar ")) {
                    String names[] = name.split(" ");
                    if (names.length > 2) {
                        String deviceId = names[names.length - 1];
                        if (deviceId.equals(mConnectedDeviceID)) { // TODO NOTE REPLACE with your device (id Polar A370 24E76A2D)
                            mCurrectBTDevice = device;
                            mBTDevicesHashMap.get(address).setUiInfo("Connected");

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                            } else {
                                bluetoothAdapter.stopLeScan(leScanCallback);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                currentState = BT_SEND_GATT;
                                device.connectGatt(context, false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
                            } else {
                                currentState = BT_SEND_GATT;
                                device.connectGatt(context, false, bluetoothGattCallback);
                            }
                        }
                    }
                }
            }
        }
    }

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                gatt.discoverServices();
            } else if(newState == BluetoothGatt.STATE_DISCONNECTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
                } else {
                    bluetoothAdapter.startLeScan(leScanCallback);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            super.onServicesDiscovered(gatt, status);
            for (BluetoothGattService gattService : gatt.getServices()) {
                if( gattService.getUuid().equals(HR_SERVICE) ){
                    for (BluetoothGattCharacteristic characteristic : gattService.getCharacteristics()) {

                        if( characteristic.getUuid().equals(HR_MEASUREMENT) ){
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_CCC);
                            gatt.setCharacteristicNotification(characteristic, true);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            super.onCharacteristicChanged(gatt, characteristic);
            if (characteristic.getUuid().equals(HR_MEASUREMENT)) {
                byte[] data = characteristic.getValue();
                int hrFormat = data[0] & 0x01;
                boolean sensorContact = true;
                final boolean contactSupported = !((data[0] & 0x06) == 0);
                if( contactSupported ) {
                    sensorContact = ((data[0] & 0x06) >> 1) == 3;
                }
                int energyExpended = (data[0] & 0x08) >> 3;
                int rrPresent = (data[0] & 0x10) >> 4;
                final int hrValue = (hrFormat == 1 ? data[1] + (data[2] << 8) : data[1]) & (hrFormat == 1 ? 0x0000FFFF : 0x000000FF);


                if (hrValue != 0) {

                    currentState = BT_CONNECTED;
                    UpdateHRRate(hrValue);
                }
                if (hrValue == 0) {
                    currentState = BT_CONNECTED_NO_PULSE;
                    UpdateHRRate(hrValue);
                }

                if( !contactSupported && hrValue == 0 ){
                    // note does this apply to all sensors, also 3rd party
                    sensorContact = false;
                }
                final boolean sensorContactFinal = sensorContact;
                int offset = hrFormat + 2;
                int energy = 0;
                if (energyExpended == 1) {
                    energy = (data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8);
                    offset += 2;
                }
                final ArrayList<Integer> rrs = new ArrayList<>();
                if (rrPresent == 1) {
                    int len = data.length;
                    while (offset < len) {
                        int rrValue = (int) ((data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8));
                        offset += 2;
                        rrs.add(rrValue);
                    }
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    public void UpdateHRRate(int newRate) {
        hrCounter = 0;
        hrRate = newRate;
    }
    public String GetHRRate(boolean updateSummary ){
        String temp = "";
        if (currentState == BT_CONNECTED){
            temp = "Pulse: " + String.valueOf(hrRate);
        }
        else{
            temp = "  no data";
        }

        if (updateSummary) {
            UpdateVideoHRSum();
        }
        return temp;
    }

    public void UpdateVideoHRSum(){

        vhrSUm.setNewHrRate(hrRate);
    }

    public String GetSummary(){

        return vhrSUm.GetSum();
    }

    public boolean Isconnected(){

        if ( (currentState == BT_CONNECTED)  || (currentState ==  BT_CONNECTED_NO_PULSE)) {
            return true;
        }
        return false;
    }

    public ArrayList<BTLE_Device> GetBluetoothDevices(){
        return mBTDevicesArrayList;
    }
    public String GetCurrentBTDeviceInfo(){

        String temp = "";

        if (mCurrectBTDevice != null)
        {
            temp = "BluetoohLE Device Information \nName: " + mCurrectBTDevice.getName() +
                    "\nAddress : " + mCurrectBTDevice.getAddress() ;
        }

        else {
            temp = "BluetoohLE Device Information \nName: No device " +
                    "\nAddress : - " ;
        }

        return temp;
    }
    public BluetoothDevice GetCurrentBTDevice() {
        return mCurrectBTDevice;
    }

    private void AutoConnectMyPolar(){


        //Autoconnect to my device (id Polar A370 24E76A2D)
        //myConnectedDeviceID = "24E76A2D";

        currentState = BT_TRY_TO_CONNECT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
        } else {
            bluetoothAdapter.startLeScan(leScanCallback);
        }

    }

    private void InitBluetoothLEScan(int scanningTime){

        currentState = BT_SCAN_ALL;

        new CountDownTimer(scanningTime, 1000) {
            public void onTick(long millisUntilFinished) {
                scanCounter++;
            }

            public void onFinish() {
                currentState = BT_NONE;
                scanCounter = 0;
            }
        }.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
        } else {
            bluetoothAdapter.startLeScan(leScanCallback);
        }
    }
    public void StartBluetoothLEScan(int scanningTime) {

        maxTimer = scanningTime/1000;
        currentState = BT_SCAN_ALL;

        if (currentState == BT_SCAN_ALL) {

            mBTDevicesArrayList.clear();
            mBTDevicesHashMap.clear();
            //mCurrectBTDevice = null;


            // TODO tämä pitää suunnitella paremmin onFinish pitäs notify
            new CountDownTimer(scanningTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    scanCounter++;
                }

                public void onFinish() {
                    currentState = BT_NONE;
                    scanCounter = 0;
                }
            }.start();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
            } else {
                bluetoothAdapter.startLeScan(leScanCallback);
            }
        }
    }

    public void StopBluetoothLEScan() {

        currentState = BT_NONE;
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        } else {
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
        */

    }
    public void ConnectBTDevice(int position){


        int rssi = 0;
        byte[] scanRecord ={0x01, 0x02, 0x03, 0x04};
        if  (mBTDevicesArrayList.get(position).getName().startsWith("Polar")) {
            currentState = BT_TRY_TO_CONNECT;
            processDeviceDiscovered(mBTDevicesArrayList.get(position).GetBTLE_Device(), rssi, scanRecord);
            //mBTDevicesArrayList.get(position).
        }
        else{
            //Toast t = Toast.makeText("No Polar Device")
        }

    }

    public void SetConnectedDeviceId(String deviceid){
        this.mConnectedDeviceID = deviceid;
    }

    public String GetConnectedDeviceId(){
        return mConnectedDeviceID;
    }


    public String getDebugInfo(){

        String temp = "";

        if (currentState == BT_SCAN_ALL ){


            temp =("State: BT_SCAN_ALL looking for bluetooth devices  " +
                    "\nDebug scanCounter: " + String.valueOf(scanCounter)+ " / " + String.valueOf(maxTimer));


        }
        else if (currentState == BT_TRY_TO_CONNECT ){
            temp = ("State: BT_TRY_TO_CONNECT"
                    + "\nDebug Counter: " + String.valueOf(hrCounter));
        }


        else if (currentState == BT_SEND_GATT ){
            temp = ("State: BT_SEND_GATT"
                    + "\nDebug Counter: " + String.valueOf(hrCounter));
        }


        else if (currentState == BT_CONNECTED ){

            temp = ("State: BT_CONNECTED  Pulssi: " + String.valueOf(hrRate)
                    + "\nDebug Counter: " + String.valueOf(hrCounter));

        }

        else if (currentState == BT_CONNECTED_NO_PULSE ){

            temp = ("State: BT_CONNECTED_NO_PULSE"
                    + "\nDebug Counter: " + String.valueOf(hrCounter));
        }

        else if (currentState == BT_NONE ) {
            temp = ("State: BT_NONE"
                    + "\nDebug Counter: " + String.valueOf(hrCounter));
        }
        else{
            temp = ("State: unknown. currentState value: " +String.valueOf(currentState)
                    + "\nDebug Counter: " + String.valueOf(hrCounter));
        }


        return temp;
    }

    public class DebugTask extends AsyncTask {

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            hrCounter++;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            while(true) {
                try {
                    //sleep for 1s in background...
                    Thread.sleep(1000);
                    //and update textview in ui thread
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                };

            }
        }
    }


}
