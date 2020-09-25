package edu.capstone.scheduler;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //입력값 받아오기
        Button button = (Button)findViewById(R.id.button);
        final EditText address = (EditText)findViewById(R.id.address);
        final TextView result = (TextView)findViewById(R.id.result);



        //geocoder 생성성
        final Geocoder geocoder = new Geocoder(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                List<Address> list = null;
                String str = address.getText().toString();

                try {
                    list = geocoder.getFromLocationName(str, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test", "IO Error");
                }
                if(list != null) {
                    if (list.size() == 0) {
                        result.setText("no result. please check your address");
                    }
                    else {
                        Address addr = list.get(0);
                        double lat = addr.getLatitude();
                        double lon = addr.getLongitude();

                        String ssn = String.format("geo : %f, %f", lat, lon);

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ssn));
                        startActivity(intent);
                    }
                }


            }
                                  }
        );






        MapView kMap = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map);
        mapViewContainer.addView(kMap);
        //kMap.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);

    }





}


