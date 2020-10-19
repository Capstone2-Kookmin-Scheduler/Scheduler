package edu.capstone.scheduler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.Time;
import java.util.Arrays;
import java.util.List;
import edu.capstone.scheduler.*;

public class AddSchedule extends AppCompatActivity {
    private EditText schedule_name, arrival_location, departure_location;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private Button search_location, add_schedule, search_departure_location, test;
    private Double departure_lat, departure_lng, arrival_lat, arrival_lng;
    private String departure_placeName, arrival_placeName;
    private int year, month, day, hour, minute;
    private int estimated_time, total_time;


    private static int AUTOCOMPLETE_REQUEST_CODE_DEPARTURE = 1;
    private static int AUTOCOMPLETE_REQUEST_CODE_ARRIVAL = 2;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        schedule_name = (EditText) findViewById(R.id.schedule_name);
        arrival_location = (EditText) findViewById(R.id.arrival_location);
        departure_location = (EditText) findViewById(R.id.departure_location);
        timePicker = (TimePicker) findViewById(R.id.schedule_time);
        datePicker = (DatePicker) findViewById(R.id.schedule_date);
        search_location = (Button) findViewById(R.id.search_location);
        search_departure_location = (Button) findViewById(R.id.search_departure_location);
        add_schedule = (Button) findViewById(R.id.add_schedule);
        test = (Button) findViewById(R.id.test);

        Places.initialize(getApplicationContext(), "AIzaSyCG6NeTZ9cdyvFoz_tNIsBHMJmfCKw1vl0");
        PlacesClient placesClient = Places.createClient(this);

        year = datePicker.getYear(); month = datePicker.getMonth(); day = datePicker.getDayOfMonth();
        hour = timePicker.getHour(); minute = timePicker.getMinute();

        // timePicker.setHour()
        //  datePicker.updateDate()

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

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate_time(departure_lng, departure_lat, arrival_lng, arrival_lat);
            }
        });

        add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date(year, month, day, hour, minute);
                Schedule schedule = new Schedule(schedule_name.getText().toString(), date, departure_lat, departure_lng, arrival_lat, arrival_lng, departure_placeName, arrival_placeName, 0);
                //calculate_time(departure_lng, departure_lat, arrival_lng, arrival_lat);
                schedule.setTotal_time(total_time);
                


                Log.i("날짜", Integer.toString(year)+Integer.toString(month) + Integer.toString(day));
                Log.i("시간", Integer.toString(hour) + Integer.toString(minute));
                Log.i("장소", departure_placeName+arrival_placeName);
                Log.i("소요시간", Integer.toString(total_time));
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

    public void calculate_time(Double departure_lng, Double departure_lat, Double arrival_lng, Double arrival_lat ) {
        ODsayService odsayService = ODsayService.init(AddSchedule.this,"suLGma46yOIqhKYbRFlIXAWLeDWumTQqfmY0RJ+ZnvE");
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                if(api == API.SEARCH_PUB_TRANS_PATH){
                    try {
                        JSONArray jsonArray = odsayData.getJson().getJSONObject("result").getJSONArray("path");
                        total_time = jsonArray.getJSONObject(1).getJSONObject("info").getInt("totalTime");
                        Log.i("예상 소요시간 ", Integer.toString(total_time));
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
        odsayService.requestSearchPubTransPath(departure_lng.toString(), departure_lat.toString(), arrival_lng.toString(), arrival_lat.toString(), "0", "0", "0", onResultCallbackListener);


    }





} // end of class AddSchedule


//--------------------------------------------------------------------------------------------------
// Copyright
// file = "Addschedule"
// Author = LeeJuHyoung
// Date = "2020-10-17" finalDate = "2020-10-27"