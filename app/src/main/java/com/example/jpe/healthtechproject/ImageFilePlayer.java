package com.example.jpe.healthtechproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpe.healthtechproject.BluetoothLE.BluetoothLEHandler;

import java.io.File;
import java.util.TimerTask;

import fi.finwe.orion360.sdk.pro.SimpleOrionActivity;

/**
 * Created by jpe on 31.1.2018.
 */

public class ImageFilePlayer extends SimpleOrionActivity{



    /** Application's private external files path. */
    public static String PRIVATE_EXTERNAL_FILES_PATH;
    public static String PUBLIC_EXTERNAL_IMAGE_PATH;

    /** Orion360 directory name (to be created under device's public external files). */
    public static final String ORION_DIRECTORY_NAME = "Orion360/SDK";

    /** Device's public /Pictures path with Orion360 subdirectory appended to it. */
    public static final String PUBLIC_EXTERNAL_PICTURES_ORION_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + ORION_DIRECTORY_NAME + File.separator;

    TextView myTextView;
    int pulse = 0;


    /** Request code for file read permission. */
    private static final int REQUEST_READ_STORAGE = 111;

    /** Full path to an image file to be played. */
    private String mImagePath;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout.
        setContentView(R.layout.activity_image_player);

        // Set Orion360 view (defined in the layout) that will be used for rendering 360 content.
        setOrionView(R.id.orion_view);

        // Select correct 360 image
        Intent intent = getIntent();
        if(!SelectVideo(intent))
        {
            // Video not found go back to main menu
            //SystemClock.sleep(5000);
            super.onBackPressed();
            return;
        }


        //Timer test
        //myTextView2 = (TextView) findViewById(R.id.PulseTextView);

        myTextView = (TextView) findViewById(R.id.PulseTextView);

        //starting our task which update textview every 1000 ms (not necessary  for images)
        //PulseTask();

    }

    @Override
    protected void onPause() {
        super.onPause();

        // need to set false -> doInBackground() ends
       // showPulse = false;

    }

    @Override
    protected void onStop() {
        super.onStop();
        // need to set false -> doInBackground() ends
        //showPulse = false;
    }

    public void showImage(String path) {

        // Keep a reference to the current image path.
        mImagePath = path;


        // When accessing paths on the external media, we should first check if it is currently
        // mounted or not (though, it is often built-in non-removable memory nowadays).
        /*
        if (path.equalsIgnoreCase(PRIVATE_EXTERNAL_IMAGE_PATH)
                || path.equalsIgnoreCase(PUBLIC_EXTERNAL_IMAGE_PATH)
                || path.equalsIgnoreCase(PRIVATE_EXPANSION_IMAGE_PATH)) {

            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, R.string.player_media_not_mounted,
                        Toast.LENGTH_LONG).show();
                return;
            }

        }
*/
        // In case we want to access images in public external folder on Android 6.0 or above,
        // we must ensure that READ_EXTERNAL_STORAGE permission is granted *before* attempting
        // to play the files in that location.
        if (path.equalsIgnoreCase(PUBLIC_EXTERNAL_IMAGE_PATH)) {

            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest
                    .permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Read permission has not been granted. As user can give the permission when
                // requested, the operation now becomes asynchronous: we must wait for
                // user's decision, and act when we receive a callback.
                ActivityCompat.requestPermissions(this, new String [] {
                        Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_READ_STORAGE);
                return;

            }

        }

        // We can now show the image file.
        setContentUri(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String [] permissions,
                                           @NonNull int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_READ_STORAGE: {

                // User has now answered to our read permission request. Let's see how:
                if (grantResults.length == 0 || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Read permission was denied by user");

                    // Bail out with a notification for user.
                    Toast.makeText(this, R.string.player_read_permission_denied,
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.i(TAG, "Read permission was granted by user");

                    // Public external folder works, start preparing the image file.
                    setContentUri(mImagePath);
                }
                return;
            }
            default:
                break;
        }
    }


    public boolean SelectVideo(Intent intent) {

        File externalFilesDir = getExternalFilesDir(null);
        if (null != externalFilesDir) {
            PRIVATE_EXTERNAL_FILES_PATH = externalFilesDir.getAbsolutePath() + File.separator;
        }

       // Intent intent = getIntent();


        Bundle extras = getIntent().getExtras();
        String image;
        String filePath1 = null;
        String filePath2 = null;

        File file;

        if (extras == null) {
            setContentUri("file:///android_asset/Orion360_test_image_1920x960.jpg");
        }

        else {
            image = intent.getStringExtra("Image");


            if (image.equals(getString(R.string.image_a_title))) {


                //filePath2 = PRIVATE_EXTERNAL_FILES_PATH + "formula_test_960x488.jpg"; // TEST
                filePath2 = getString(R.string.image_a_thumbnail);
                //setContentUri("file:///android_asset/formula_960x488.jpg");

            }
            else if (image.equals(getString(R.string.image_b_title))) {

                filePath2 = getString(R.string.image_b_thumbnail);
                //filePath2 = "file:///android_asset/carousel_3840x2160.jpg";
                //setContentUri("file:///android_asset/carousel_3840x2160.jpg");

            }
            else if (image.equals(getString(R.string.image_c_title))) {

                filePath2 = getString(R.string.image_c_thumbnail);
                //filePath2 = "file:///android_asset/balloon_3840x1920.jpg";
                //setContentUri("file:///android_asset/balloon_3840x1920.jpg");
            }
            else {
                //setContentUri("file:///android_asset/Orion360_test_image_1920x960.jpg");
            }
        }

        setContentUri(filePath2);


        return true;
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
                                pulse++;
                                //myTextView.setText("Pulssi: " + String.valueOf(pulse));
                                myTextView.setText(BluetoothLEHandler.getInstance().GetHRRate(true));
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        t.start();

    }

    /*
    public class RefreshTask extends AsyncTask {

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            pulse2++;
            String text = "Pulssi: " + pulse2;
            myTextView.setText(text);
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            while(showPulse) {
                try {
                    //sleep for 1s in background...
                    Thread.sleep(1000);
                    //and update textview in ui thread
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                };

            }
            return null;
        }
    }
    */
}

