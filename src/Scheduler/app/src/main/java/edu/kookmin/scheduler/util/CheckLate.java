package edu.kookmin.scheduler.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckLate extends BroadcastReceiver {
    GpsTracker gpsTracker;
    private Double lat, lng, arrival_lat, arrival_lng;
    private int late_count, late_time;
    private int hour, minute;
    private int count;
    private int total_time;
    private String schedule_name;
    private String arrival_location;
    private String mUid;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        arrival_lat = bundle.getDouble("arrival_lat");
        arrival_lng = bundle.getDouble("arrival_lng");
        hour = bundle.getInt("hour");
        minute = bundle.getInt("minute");
        schedule_name = bundle.getString("schedule_name");
        arrival_location = bundle.getString("arrival_location");
        mUid = bundle.getString("mUid");
        gpsTracker = new GpsTracker(context);
        lat = gpsTracker.getLat();
        lng = gpsTracker.getLng();
        Log.d("위도 경도 ", lat.toString() + " " + lng.toString());
        Log.d("mUid",mUid);

        ref = database.getReference("User/").child(mUid).child("lateCount");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("onDataChanage", "실행");
                count = snapshot.getValue(int.class);
                isLate(lat, lng, arrival_lat, arrival_lng);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error",error.toString());
            }
        });






    } // end of onReceive

    public void isLate(Double lat, Double lng, Double arrival_lat, Double arrival_lng) {
        // 오차 300m 이내 계산
        if(Math.abs((lat+lng) - (arrival_lat+arrival_lng)) <= 0.0020000 ){
            Log.e("isLate", "false");
        }
        else{
            Log.e("isLate", "true");
            ref.setValue(++count);
        }
    }


} // end of CheckLate

