package edu.capstone.scheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;


public class AlarmActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private AlarmManager alarmManager;
    private int hour, minute;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        timePicker = findViewById(R.id.tp_timepicker);
        button = findViewById(R.id.button);





        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Before", "befffffffffffffffff");
                regist(v);
                Log.d("After", " affffffffffffffff");
            }
        });


    }// end of onCreate



    public void regist(View view) {
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(AlarmActivity.this, CheckLocation.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, 0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //hour = timePicker.getHour();
            //minute = timePicker.getMinute();
        }


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 38);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Log.d("call alarmManger", " ttttttttttttttttttttttttttt");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    } // end of regist

    public void unregist(View view) {
        Intent intent = new Intent(AlarmActivity.this, CheckLocation.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
    } //unRegist






}// end of class AlarmActivity

//--------------------------------------------------------------------------------------------------
// Copyright
// file = "AlarmActivity"
// Author = LeeJuHyoung
// Date = "2020-10-16" finalDate = "2020-10-20"
