package edu.kookmin.scheduler.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    @Override
    public void onReceive(Context context, Intent intent) {
        arrival_lat = intent.getExtras().getDouble("arrival_lat");
        arrival_lng = intent.getExtras().getDouble("arrival_lng");
        hour = intent.getExtras().getInt("hour");
        minute = intent.getExtras().getInt("minute");
        schedule_name = intent.getStringExtra("schedule_name");
        arrival_location = intent.getStringExtra("arrival_location");

        gpsTracker = new GpsTracker(context);
        lat = gpsTracker.getLat();
        lng = gpsTracker.getLng();
        Log.d("위도 경도 ", lat.toString() + " " + lng.toString());
        Log.d("시간체크","hour : "+ hour+" minute : "+minute);

        ref = database.getReference("User/").child(mUser.getUid()).child("lateCount");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.getValue(int.class);
                if (isLate(lat, lng, arrival_lat, arrival_lng)) count++;
                ref.setValue(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    } // end of onReceive

    public boolean isLate(Double lat, Double lng, Double arrival_lat, Double arrival_lng) {
        // 오차 300m 이내 계산
        if(Math.abs((lat+lng) - (arrival_lat+arrival_lng)) <= 0.0020000 ) return false;
        else return true;
    }


} // end of CheckLate

