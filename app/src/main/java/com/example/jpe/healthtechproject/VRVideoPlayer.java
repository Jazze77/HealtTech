package com.example.jpe.healthtechproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import fi.finwe.orion360.sdk.pro.SimpleOrionActivity;

/**
 * Created by jpe on 30.1.2018.
 */

public class VRVideoPlayer extends SimpleOrionActivity{


    TextView myTextView;
    int pulse = 0;


    /** Gesture detector for touch events. */
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vr_video_player);

        setOrionView(R.id.orion_view);

        Intent intent = getIntent();

        Bundle extras = getIntent().getExtras();
        String image;

        if (extras == null) {
            setContentUri("file:///android_asset/balloon_4k.mp4");
        }

        else {
            image = intent.getStringExtra("Image");


            if (image.equals("Formula")) {
                setContentUri("file:///android_asset//formula_fullhd.mp4");
            } else if (image.equals("Orion360")) {
                setContentUri("https://s3.amazonaws.com/orion360-us/Orion360_test_video_2d_equi_360x180deg_1920x960pix_30fps_30sec_x264.mp4");

            } else {
                setContentUri("file:///android_asset/balloon_4k.mp4");
            }
        }
        //setContentUri("https://s3.amazonaws.com/orion360-us/Orion360_test_video_2d_equi_360x180deg_1920x960pix_30fps_30sec_x264.mp4");
        //setContentUri("file:///android_asset/formula_fullhd.mp4");
        //setContentUri("file:///android_asset/balloon_4k.mp4");

        myTextView = (TextView) findViewById(R.id.vr_textview);

        //starting our task which update textview every 1000 ms
        PulseTask();


        // Configure video view for VR mode. This will split the screen horizontally,
        // render the image separately for left and right eye, and apply lens distortion
        // compensation and field-of-view (FOV) locking to configured values.
        setVRMode(true);

        // The user should always have an easy-to-find method for returning from VR mode to
        // normal mode. Here we use touch events, as it is natural to try tapping the screen
        // if you don't know what else to do. Start by propagating touch events from the
        // Orion360 view to a gesture detector.
        getOrionView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }

        });

        // Then, handle tap and long press events based on VR mode state. Typically you
        // want to associate long tap for entering/exiting VR mode and inform the user
        // that this hidden feature exists (at least when the user is stuck in VR mode).
        mGestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {

                        // Notify user how to enter/exit VR mode with long press.
                        String message;
                        if (!isVRModeEnabled()) {
                            message = getString(R.string.player_long_tap_hint_enter_vr_mode);
                        } else {
                            message = getString(R.string.player_long_tap_hint_exit_vr_mode);
                        }
                        Toast.makeText(VRVideoPlayer.this, message,
                                Toast.LENGTH_SHORT).show();

                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                        // Enter or exit VR mode.
                        if (!isVRModeEnabled()) {
                            setVRMode(true);
                        } else {
                            setVRMode(false);
                        }

                    }

                });

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
                                myTextView.setText("Pulssi: " + String.valueOf(pulse));
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
}


