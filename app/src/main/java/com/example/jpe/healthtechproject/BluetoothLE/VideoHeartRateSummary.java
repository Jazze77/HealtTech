package com.example.jpe.healthtechproject.BluetoothLE;

import java.util.ArrayList;

/**
 * Created by jpe on 5.3.2018.
 */

public class VideoHeartRateSummary {
    private String VideoName;
    private ArrayList<Integer> hrRate;
    private int avgHrRate;

    public VideoHeartRateSummary(String VideoName){

        this.VideoName = VideoName;
        hrRate = new ArrayList<>();
    };

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }

    public ArrayList<Integer> getHrRate() {
        return hrRate;
    }

    public void setNewHrRate(int newrate) {
        hrRate.add(newrate);
    }

    /*
    public void setHrRate(ArrayList<Integer> hrRate) {
        this.hrRate = hrRate;
    }
*/
    public int getAvgHrRate() {
        int sum = 0;

        for (int i = 0;i < hrRate.size();i++) {
            if ( hrRate.get(i).intValue() == 0){

            }
            else{

            }
            sum += hrRate.get(i).intValue();
        }
        avgHrRate = sum/hrRate.size();

        return avgHrRate;
    }


    public String GetSum(){
        String temp;
        temp = "Size :" + hrRate.size() +
                "avg : " + getAvgHrRate();

        return temp;
    }

    /*
    public void setAvgHrRate(int avgHrRate) {
        this.avgHrRate = avgHrRate;
    }
    */
}

