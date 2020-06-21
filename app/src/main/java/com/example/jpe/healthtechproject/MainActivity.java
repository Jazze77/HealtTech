package com.example.jpe.healthtechproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.jpe.healthtechproject.BluetoothLE.BluetoothLEHandler;
import com.example.jpe.healthtechproject.BluetoothLE.PolarBluetoothLE;

import com.example.jpe.healthtechproject.BluetoothLE.*;


public class MainActivity extends Activity/*SimpleOrionActivity*/ {


    /** Application's private internal files path. */
    //public static String PRIVATE_INTERNAL_FILES_PATH;

    /** Application's private external files path. */
   // public static String PRIVATE_EXTERNAL_FILES_PATH_MAIN;


    private boolean mVideoStarted =false;

    private MyCustomClass myCustomClass;

    /** Time limit for counting two back presses (in ms). */
    protected static final int DOUBLE_BACK_TO_EXIT_TIME_WINDOW = 2000;

    /** Flag for enabling double back to exit -feature. */
    protected boolean mDoubleBackToExitEnabled = true;

    /** Flag for counting two back presses for exit. */
    protected boolean mDoubleBackToExitPressedOnce = false;

    /** Toast for asking another back press. */
    protected Toast mDoubleBackToExitNotification = null;

    /** Handler for clearing back press counter. */
    protected Handler mDoubleBackToExitHandler = null;

    /** Runnable that actually resets back press counter (flag). */
    private Runnable mDoubleBackToExitCounterReset = new Runnable() {
        @Override
        public void run() {
            mDoubleBackToExitPressedOnce = false;
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //setOrionView(R.id.orion_view);


        // Double back press to exit.
        mDoubleBackToExitPressedOnce = false;
        mDoubleBackToExitNotification = Toast.makeText(this, getResources()
                        .getString(R.string.double_back_exit_notification),
                Toast.LENGTH_SHORT); // Disregard warning, not supposed to show the toast yet!
        mDoubleBackToExitHandler = new Handler();


        myCustomClass = MyCustomClass.getInstance();
        myCustomClass.addEventListener(HeartRateInfo.COMPLETE, new IEventHandler() {
            @Override
            public void callback(HeartRateInfo event) {
                Log.d("Event callback","i am in callback "+event.getStrType()+" :: param = "+event.getParams());
            }
        });
        Log.d("Event callback","i am going to call");
        myCustomClass.myCallback();

    }

    @Override
    protected void onResume() {
        super.onResume();
        String temp;

        if (mVideoStarted) {

            temp=  BluetoothLEHandler.getInstance().GetSummary();

            Toast toast = Toast.makeText(this,temp,Toast.LENGTH_LONG);
            toast.show();

            mVideoStarted = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDoubleBackToExitEnabled) {

            // Handle double back to exit -feature.
            if (!mDoubleBackToExitPressedOnce) {

                /*
                if (this.getListAdapter() != mGroupAdapter) {

                    // Return to groups view.
                    setListAdapter(mGroupAdapter);
                    mMainMenuTitleText.setText(R.string.app_name);

                } else {*/

                    // First press observed.
                    mDoubleBackToExitPressedOnce = true;

                    // Notify user that another press is needed.
                    mDoubleBackToExitNotification.show();

                    // Cancel first press if second press is not observed in time.
                    mDoubleBackToExitHandler.postDelayed(
                            mDoubleBackToExitCounterReset,
                            DOUBLE_BACK_TO_EXIT_TIME_WINDOW);

                //}

            } else {

                // Second press came in time. Cancel notification and handler.
                mDoubleBackToExitNotification.cancel();
                mDoubleBackToExitHandler
                        .removeCallbacks(mDoubleBackToExitCounterReset);

                // Let the app exit now.
                super.onBackPressed();
            }
        } else {

            // Let the app exit now.
            super.onBackPressed();
        }
    }


    // 360 IMAGES
    public void ImageAImagePressed(View view) {

        Intent intent = new Intent(this, ImageFilePlayer.class);
        intent.putExtra("Image","Formula");
        startActivity(intent);
    }

    public void ImageBImagePressed(View view) {

        Intent intent = new Intent(this, ImageFilePlayer.class);
        intent.putExtra("Image","Carousel");
        startActivity(intent);
    }

    public void ImageCImagePressed(View view) {

        Intent intent = new Intent(this, ImageFilePlayer.class);
        intent.putExtra("Image","Balloon");
        startActivity(intent);

/*
        Intent intent = new Intent(this, PolarBluetoothLE.class);
        //intent.putExtra("Image","Balloon");
        startActivity(intent);
*/

    }

    // 360 VR VIDEOS
    public void ImageAPressed(View view) {

        /*
        Intent intent = new Intent(this, VideoPlayerContols.class);
        intent.putExtra("Image","Formula");
        startActivity(intent);
        */

        Intent intent = new Intent(this, VideoControls.class);
        // intent.putExtra("Image","Formula");
        startActivity(intent);

    }

    public void ImageBPressed(View view) {

        Intent intent = new Intent(this, VideoPlayerContols.class);
        intent.putExtra("Image","Carousel");
        startActivity(intent);
    }

    public void ImageCPressed(View view) {

        Intent intent = new Intent(this, VideoPlayerContols.class);
        intent.putExtra("Image","Balloon");
        mVideoStarted = true;
        startActivity(intent);
    }

    public void ActivateSettings(View view) {
        // Only settings is Bluetooth
        Intent intent = new Intent(this, PolarBluetoothLE.class);
        //intent.putExtra("");
        startActivity(intent);

    }
}
