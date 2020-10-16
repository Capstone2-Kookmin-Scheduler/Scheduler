package edu.capstone.scheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//버스데이터 불러오기 import
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;

public class buttonSecond extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView lat = (TextView)findViewById(R.id.lat);
        TextView lng = (TextView)findViewById(R.id.lng);

        Intent intent = getIntent(); // 인텐트 수신
        double lat_value = intent.getExtras().getDouble("lat");
        double lng_value = intent.getExtras().getDouble("lng");
        lat.setText(String.valueOf(lat_value));
        lng.setText(String.valueOf(lng_value));



        Button buttonReturn = (Button) findViewById(R.id.find_bus);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });




    }
}

