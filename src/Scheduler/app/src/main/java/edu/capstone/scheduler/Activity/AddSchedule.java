package edu.capstone.scheduler.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import edu.capstone.scheduler.Object.Date;
import edu.capstone.scheduler.Object.Schedule;
import edu.capstone.scheduler.R;

public class AddSchedule extends AppCompatActivity {

    private EditText schedule_name, arrival_location, departure_location;
    private TimePicker timePicker;
    private DatePicker datePicker;

    private Double departure_lat, departure_lng, arrival_lat, arrival_lng;
    private String departure_placeName, arrival_placeName;
    private int year, month, day, hour, minute;
    private int estimated_time, total_time;

    private Button search_location, add_schedule, search_departure_location, test;

    private Schedule schedule;
    private AlarmManager alarmManager;

    private static int AUTOCOMPLETE_REQUEST_CODE_DEPARTURE = 1;
    private static int AUTOCOMPLETE_REQUEST_CODE_ARRIVAL = 2;

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        schedule_name = (EditText) findViewById(R.id.schedule_name);
        timePicker = (TimePicker) findViewById(R.id.schedule_time);
        datePicker = (DatePicker) findViewById(R.id.schedule_date);

        add_schedule = (Button) findViewById(R.id.add_schedule);
        search_location = (Button) findViewById(R.id.search_location);
        search_departure_location = (Button) findViewById(R.id.search_departure_location);

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                year = i; month = i1; day = i2;
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = i; minute = i1;
            }
        });


        search_departure_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(AddSchedule.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_DEPARTURE);
            }
        });

        search_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(AddSchedule.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_ARRIVAL);
            }
        });

        add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date(year, month, day ,hour, minute);
                schedule = new Schedule(schedule_name.getText().toString(), date, departure_lat, departure_lng, arrival_lat, arrival_lng, departure_placeName, arrival_placeName, 0);
                schedule.setTotal_time(total_time);
                String dateStr = Integer.toString(date.getYear())+String.format("%02d",date.getMonth())+String.format("%02d",date.getDay());

                Log.i("날짜", Integer.toString(year)+Integer.toString(month) + Integer.toString(day));
                Log.i("시간", Integer.toString(hour) + Integer.toString(minute));
                Log.i("장소", departure_placeName+arrival_placeName);
                Log.i("소요시간", Integer.toString(total_time));

                //ref = database.getReference("Schedule/").child(mUser.getUid()).child(dateStr).child(schedule.getName());
                //ref.updateChildren(schedule.toMap());

//                regist(v);

            }
        });

        search_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        search_departure_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1234){
            if(resultCode==RESULT_OK){

            }
        }

    }
    //    public void regist(View view) {
//        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(AddSchedule.this, CheckLocation.class);
//        intent.putExtra("arrival_lat", arrival_lat);
//        intent.putExtra("arrival_lng", arrival_lng);
//        intent.putExtra("hour", hour);
//        intent.putExtra("minute", minute);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddSchedule.this, 0, intent, 0);
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            //hour = timePicker.getHour();
//            //minute = timePicker.getMinute();
//        }
//
//        /**
//         * TODO : 한시간 전 실행 아님, 약속시간 - 소요시간 - 한시간
//         */
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, hour-1); //  한시간 전 실행
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        Log.d("call alarmManger", " ttttttttttttttttttttttttttt");
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//    } // end of regist
//
//    public void unregist(View view) {
//        Intent intent = new Intent(AddSchedule.this, CheckLocation.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        alarmManager.cancel(pendingIntent);
//    } //unRegist


}