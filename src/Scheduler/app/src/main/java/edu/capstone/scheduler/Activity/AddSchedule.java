package edu.capstone.scheduler.Activity;

import androidx.annotation.Nullable;

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

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.capstone.scheduler.util.CheckLocation;
import edu.capstone.scheduler.Object.Date;
import edu.capstone.scheduler.Object.Schedule;
import edu.capstone.scheduler.R;

public class AddSchedule extends BaseActivity {
    private EditText schedule_name, arrival_location, departure_location;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private Button search_location, add_schedule, search_departure_location;
    private Double departure_lat, departure_lng, arrival_lat, arrival_lng;
    private String departure_placeName, arrival_placeName;
    private int year, month, day, hour, minute;
    private Schedule schedule  = new Schedule();
    private String schedule_UID;
    private AlarmManager alarmManager;
    private int count = 0;

    private static int AUTOCOMPLETE_REQUEST_CODE_DEPARTURE = 1;
    private static int AUTOCOMPLETE_REQUEST_CODE_ARRIVAL = 2;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        mUser = mAuth.getCurrentUser();

        schedule_name = (EditText) findViewById(R.id.schedule_name);
        arrival_location = (EditText) findViewById(R.id.arrival_location);
        departure_location = (EditText) findViewById(R.id.departure_location);
        timePicker = (TimePicker) findViewById(R.id.schedule_time);
        datePicker = (DatePicker) findViewById(R.id.schedule_date);
        search_location = (Button) findViewById(R.id.search_location);
        search_departure_location = (Button) findViewById(R.id.search_departure_location);
        add_schedule = (Button) findViewById(R.id.add_schedule);

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            schedule = (Schedule) extras.getSerializable("schedule");
            Log.e("log", schedule.getUid());
            updateUI(schedule);
        }

        Places.initialize(getApplicationContext(), "AIzaSyCG6NeTZ9cdyvFoz_tNIsBHMJmfCKw1vl0");
        PlacesClient placesClient = Places.createClient(this);

        // initialize currentDate
        java.util.Date currentTime = new java.util.Date();
        SimpleDateFormat format_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat format_month = new SimpleDateFormat("MM");
        SimpleDateFormat format_day = new SimpleDateFormat("dd");
        SimpleDateFormat format_hour = new SimpleDateFormat("HH");
        SimpleDateFormat format_minute = new SimpleDateFormat("mm");

        String str_year = format_year.format(currentTime); year = Integer.parseInt(str_year);
        String str_month = format_month.format(currentTime); month = Integer.parseInt(str_month);
        String str_day = format_day.format(currentTime); day = Integer.parseInt(str_day);
        String str_hour = format_hour.format(currentTime); hour = Integer.parseInt(str_hour);
        String str_minute = format_minute.format(currentTime); minute = Integer.parseInt(str_minute);



        //set datePicker and timePicker
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker i, int i1, int i2, int i3) {
                datePicker = i; year = i1; month = i2+1; day = i3;
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = i; minute = i1;
            }
        });

        // set location
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
                Date date = new Date(year, month, day, hour, minute);
                schedule.setDate(date);
                schedule.setName(schedule_name.getText().toString());
                CalculateTime(getApplicationContext(), schedule);
            }
        });


    } // end of onCreate



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_DEPARTURE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("add", "Place: " + place.getName() + ", " + place.getId());

                departure_placeName = place.getName();
                departure_location.setText(departure_placeName);
                departure_lat = place.getLatLng().latitude;
                departure_lng = place.getLatLng().longitude;
                schedule.setDeparture_lat(departure_lat);
                schedule.setDeparture_lng(departure_lng);
                schedule.setDeparture_location(departure_placeName);
                Log.i("위도 경도 ", departure_lat.toString() + departure_lng.toString());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("add", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        else if(requestCode == AUTOCOMPLETE_REQUEST_CODE_ARRIVAL) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("add", "Place: " + place.getName() + ", " + place.getId());

                arrival_placeName = place.getName();
                arrival_location.setText(arrival_placeName);
                arrival_lat = place.getLatLng().latitude;
                arrival_lng = place.getLatLng().longitude;
                schedule.setArrival_lat(arrival_lat);
                schedule.setArrival_lng(arrival_lng);
                schedule.setArrival_location(arrival_placeName);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("add", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
    }




    public void regist(Schedule schedule) {
        count++;
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(AddSchedule.this, CheckLocation.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("schedule",schedule);
        intent.putExtra("arrival_lat", schedule.getArrival_lat());
        intent.putExtra("arrival_lng", schedule.getArrival_lng());
        intent.putExtra("hour", schedule.getDate().getHour());
        intent.putExtra("minute", schedule.getDate().getMinute());
        intent.putExtra("schedule_name", schedule_name.getText().toString());
        intent.putExtra("arrival_location", schedule.getArrival_location());
//        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddSchedule.this, hour*60+minute, intent, 0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //hour = timePicker.getHour();
            //minute = timePicker.getMinute();
        }

        /**
         * TODO : 한시간 전 실행 아님, 약속시간 - 소요시간 - 한시간
         */
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour-1); //  한시간 전 실행
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("call alarmManger", "channel " + count);
    } // end of regist

    public void unregist(View view) {
        Intent intent = new Intent(AddSchedule.this, CheckLocation.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, count, intent, 0);
        alarmManager.cancel(pendingIntent);
    } //unRegist

    private void CalculateTime(Context context, Schedule schedule){
        Schedule mSchedule = schedule;
        ODsayService odsayService = ODsayService.init(context,"suLGma46yOIqhKYbRFlIXAWLeDWumTQqfmY0RJ+ZnvE");
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                if(api == API.SEARCH_PUB_TRANS_PATH){
                    try {
                        JSONArray jsonArray = odsayData.getJson().getJSONObject("result").getJSONArray("path");
                        int time = jsonArray.getJSONObject(1).getJSONObject("info").getInt("totalTime");
                        Log.i("예상 소요시간 ", Integer.toString(time));
                        mSchedule.setTotal_time(time);
                        Date date = mSchedule.getDate();
                        String dateStr = Integer.toString(date.getYear())+String.format("%02d",date.getMonth())+String.format("%02d",date.getDay());
                        ref = database.getReference("Schedule/").child(mUser.getUid()).child(dateStr);
                        if(mSchedule.getUid()==null) {
                            Log.e("add","null");
                            schedule_UID = ref.push().getKey();
                            mSchedule.setUid(schedule_UID);
                            Map<String,Object> childUpdates = new HashMap<>();
                            childUpdates.put(schedule_UID,mSchedule.toMap());
                            ref.updateChildren(childUpdates);
                            regist(mSchedule);
                            finish();
                        }
                        else{
//                            Map<String,Object> childUpdates = new HashMap<>();
//                            childUpdates.put(schedule_UID,mSchedule.toMap());
                            Log.e("add",mSchedule.getUid());
                            ref.child(mSchedule.getUid()).setValue(mSchedule);
                            regist(mSchedule);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onError(int i, String s, API api) {
                if(api==API.BUS_STATION_INFO){}
            }
        };

        // 출경도,출위도,도경도,도위도,정렬방식(0:추천,1:타입별),검색방식(0:도시내검색,1:도시간검색),경로수단(0:모두,1:지하철,2:버스)
        odsayService.requestSearchPubTransPath(mSchedule.getDeparture_lng().toString(), mSchedule.getDeparture_lat().toString(), mSchedule.getArrival_lng().toString(), mSchedule.getArrival_lat().toString(), "0", "0", "0", onResultCallbackListener);
    }

    public void updateUI(Schedule schedule){
        this.schedule_name.setText(schedule.getName());
        this.arrival_location.setText(schedule.getArrival_location());
        this.departure_location.setText(schedule.getDeparture_location()) ;
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker i, int i1, int i2, int i3) {
                datePicker = i; year = i1; month = i2+1; day = i3;
            }
        });
        this.timePicker.setHour(schedule.getDate().getHour());
        this.timePicker.setMinute(schedule.getDate().getMinute());

        this.arrival_lat = schedule.getArrival_lat();
        this.arrival_lng = schedule.getArrival_lng();
        this.departure_lat = schedule.getDeparture_lat();
        this.departure_lng = schedule.getDeparture_lng();
        this.schedule_UID = schedule.getUid();


    }



} // end of class AddSchedule


//--------------------------------------------------------------------------------------------------
// Copyright
// file = "Addschedule"
// Author = LeeJuHyoung
// Date = "2020-10-17" finalDate = "2020-10-27"