package edu.capstone.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import edu.capstone.scheduler.AlarmActivity;

public class CheckLocation extends BroadcastReceiver {
    GpsTracker gpsTracker;
    private Double lat, lng, target_lat, target_lng;
    private int late_count, late_time;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Alarm", "Alarm");
        gpsTracker = new GpsTracker(context);
        lat = gpsTracker.getLat();
        lng = gpsTracker.getLng();
        Log.d("위도 경도 ", lat.toString() + lng.toString());

        

    } //end of onReceive


    public boolean checkLocation (Double lat, Double lng, Double target_lat, Double target_lng) {
        if (Math.abs((lat + lng) - (target_lat + target_lng)) > 100){
            return true;
        }
        else return false;
    }


} // end of CheckLocation



//--------------------------------------------------------------------------------------------------
// Copyright
// file = "CheckLocation"
// Author = LeeJuHyoung
// Date = "2020-10-16" finalDate = "2020-10-20"