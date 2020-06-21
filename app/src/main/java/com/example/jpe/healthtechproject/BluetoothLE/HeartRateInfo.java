package com.example.jpe.healthtechproject.BluetoothLE;

/**
 * Created by Jake on 14.3.2018.
 */

public class HeartRateInfo /*extends EventDispatcher*/ {

    public static final String COMPLETE = "complete";

    protected String strType = "";
    protected Object params;

    public HeartRateInfo(String type,Object params){
        initProperties(type,params);
    }

    protected void initProperties(String type,Object params){
        strType = type;
        this.params = params;
    }

    public String getStrType(){
        return strType;
    }

    public Object getParams(){

        return this.params;
    }

}


