package com.example.jpe.healthtechproject;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpe.healthtechproject.BluetoothLE.BluetoothLEHandler;

import java.io.File;

import fi.finwe.orion360.sdk.pro.SimpleOrionActivity;
import fi.finwe.orion360.sdk.pro.source.OrionVideoTexture;


/**
 * Created by jpe on 30.1.2018.
 */

public class VideoPlayerContols extends SimpleOrionActivity {



    public static String PRIVATE_EXTERNAL_FILES_PATH;

    /** Video summary */
    private View mVideoSummaryView;
    private TextView mySummaryText;

    // Pulse task
    private TextView myPulseTaskView;
    private ImageView myPulseImage;
    private ImageView myPulseImage2;
    private int pulse = 50;


    /** Media controller. */
    private MediaController mMediaController;

    /** Gesture detector for tapping events. */
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        // Select correct VR video
        Intent intent = getIntent();
        if(!SelectVideo(intent))
        {
            // Video not found go back to main menu
            //SystemClock.sleep(5000);
            super.onBackPressed();
            return;
        }


        setContentView(R.layout.activity_video_player_controls);
        setOrionView(R.id.orion_view);

        // Title panel.
        mVideoSummaryView = (View) findViewById(R.id.full_video_summary);
        mVideoSummaryView.setVisibility(View.INVISIBLE);
        mySummaryText  = (TextView) findViewById(R.id.SummaryText);


        myPulseImage = (ImageView)findViewById(R.id.PulseImage);
        myPulseImage2 = (ImageView)findViewById(R.id.PulseImage2);
        myPulseImage2.setVisibility(View.INVISIBLE);

        //starting our task which update textview every 1000 ms
        myPulseTaskView = (TextView) findViewById(R.id.PulseTextView);

        PulseTask();

        // Set content listener.
        setVideoContentListener(new OrionVideoTexture.ListenerBase() {

            @Override
            public void onVideoPrepared(OrionVideoTexture orionVideoTexture) {

                // Make the controls visible when a video has been prepared.
                mMediaController.show();
            }
        });

        // Create a media controller.
        mMediaController = new MediaController(this);

        // Set Orion360 video texture as media player (media controller interacts directly with it).
        mMediaController.setMediaPlayer(
                ((OrionVideoTexture)getOrionTexture()).getMediaPlayerControl());

        // Set Orion360 view as anchor view (media controller positions itself on top of anchor).
        mMediaController.setAnchorView(getOrionView());

        //mMediaController.setMediaPlayer();


        // Propagate all touch events from the Orion view to a gesture detector.
        getOrionView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }



        });

        // Toggle media controls by tapping the screen.
        mGestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {


                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {

                        // Notify user how to enter/exit VR mode with long press.
                        if (isVRModeEnabled()) {

                            String message;
                            message = getString(R.string.player_long_tap_hint_exit_vr_mode);
                            Toast.makeText(VideoPlayerContols.this, message,
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            if (mMediaController.isShowing()) {
                                mMediaController.hide();
                            } else {
                                mMediaController.show();
                            }

                        }

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

    @Override
    protected void onPause() {
        String temp ="j";

        temp=  BluetoothLEHandler.getInstance().GetSummary();

        mVideoSummaryView.setVisibility(View.VISIBLE);
        mySummaryText.setText(temp);

        if (temp == null) {

        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        String temp = "j";

        temp=  BluetoothLEHandler.getInstance().GetSummary();

        mVideoSummaryView.setVisibility(View.VISIBLE);
        mySummaryText.setText(temp);

        if (temp == null) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {


        String temp;

        temp=  BluetoothLEHandler.getInstance().GetSummary();

        mVideoSummaryView.setVisibility(View.VISIBLE);
        mySummaryText.setText(temp);

        super.onStop();
    }

    public boolean SelectVideo(Intent intent){


        File externalFilesDir = getExternalFilesDir(null);
        if (null != externalFilesDir) {
            PRIVATE_EXTERNAL_FILES_PATH = externalFilesDir.getAbsolutePath() + File.separator;
        }

        Bundle extras = intent.getExtras();
        String image;
        String message;
        String filePath1 = "";
        String filePath2 = "";

        if (extras == null) {


            //message = getString("No Video");
            message = "No Video1";
            Toast.makeText(VideoPlayerContols.this, message,
                    Toast.LENGTH_LONG).show();

            return false;
        }

        else {
            image = intent.getStringExtra("Image");
            //image = intent.getStringExtra(getString(R.string.show_video));

            if (image == null ) {
                message = "Parameter error";
                Toast.makeText(VideoPlayerContols.this, message,
                        Toast.LENGTH_LONG).show();
                return false;
            }


            if (image.equals(getString(R.string.image_a_title))) {

                filePath1 = PRIVATE_EXTERNAL_FILES_PATH + getString(R.string.video_a_full_name);
                //filePath2 = "file:///android_asset/formula_fullhd.mp4";


            }
            else if (image.equals(getString(R.string.image_b_title))) {
                filePath1 = PRIVATE_EXTERNAL_FILES_PATH + getString(R.string.video_b_full_name);
                //filePath2 = "file:///android_asset/Carousel.mp4";

            }
            else if (image.equals(getString(R.string.image_c_title))) {
                filePath1 = PRIVATE_EXTERNAL_FILES_PATH + getString(R.string.video_c_full_name);
                //filePath2 = "file:///android_asset/balloon_4k.mp4";

            }
            else {

                message =  "No Video";
                Toast.makeText(VideoPlayerContols.this, message,
                        Toast.LENGTH_LONG).show();

                return false;
            }
        }

        //setContentUri("https://s3.amazonaws.com/orion360-us/Orion360_test_video_2d_equi_360x180deg_1920x960pix_30fps_30sec_x264.mp4");
        setContentUri(filePath1);
        return true;
    }

    private void PulseTask(){

        Thread t=new Thread(){


            @Override
            public void run(){

                while(!isInterrupted()){

                    try {
                        Thread.sleep(500);  //1000ms = 1 sec

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                pulse++;
                                //myPulseTaskView.setText("Pulssi: " + String.valueOf(pulse));
                                myPulseTaskView.setText(BluetoothLEHandler.getInstance().GetHRRate(true));
                                if (pulse % 2 != 0 ) {
                                    myPulseImage.setVisibility(View.INVISIBLE);
                                    myPulseImage2.setVisibility(View.VISIBLE);
                                }
                                else{
                                    myPulseImage.setVisibility(View.VISIBLE);
                                    myPulseImage2.setVisibility(View.INVISIBLE);
                                }
                                //myPulseImage.setMaxWidth(pulse);


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
