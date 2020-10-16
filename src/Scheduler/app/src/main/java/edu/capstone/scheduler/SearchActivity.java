package edu.capstone.scheduler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;


import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//버스데이터 불러오기 import
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {
    private TextView currentLocation;
    private Button button;
    private GpsTracker gpsTracker;
    private Double lat; private Double lng;
    private int totalTime;
    private EditText bus_station_name;
    private String data;
    private JSONObject jsonObject;
    private String[] arr;

    Disposable backgroundtask;

    StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos"); /*URL*/
    BufferedReader rd;
    //StringBuffer buffer;


    // new Thread
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        gpsTracker = new GpsTracker(SearchActivity.this);
        lat = gpsTracker.getLat();
        lng = gpsTracker.getLng();
        Log.d("위도 경도 ", lat + "  " + lng);

        //입력값 받아오기
        button = (Button)findViewById(R.id.button);
        currentLocation = (TextView)findViewById(R.id.result);



        ODsayService odsayService = ODsayService.init(this.getApplicationContext(),"suLGma46yOIqhKYbRFlIXAWLeDWumTQqfmY0RJ+ZnvE");
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                if(api == API.SEARCH_PUB_TRANS_PATH){
                    try {
                        JSONArray jsonArray = odsayData.getJson().getJSONObject("result").getJSONArray("path");
                        for(int i=0;i<jsonArray.length();i++){

                            int totalTime = jsonArray.getJSONObject(i).getJSONObject("info").getInt("totalTime");
                            Log.d("결과값"+i+" : ", Integer.toString(totalTime));
                        }
                        JSONArray obj = jsonArray.getJSONObject(1).getJSONArray("subPath");
                        //data = obj.getJSONArray("subPath").getJSONObject(1).getString("startName");
                        for(int i = 1; i<=obj.length(); i+=2){
                            Log.d("경로", obj.getJSONObject(i).getString("startName") + " -> " + obj.getJSONObject(i).getString("endName") + "\n");
                        }


//                        int totalTime = odsayData.getJson().getJSONObject("result").getJSONObject("path").getJSONObject("info").getInt("totalTime");
//                        String firstStartStation = odsayData.getJson().getJSONObject("result").getJSONObject("path").getJSONObject("info").getString("firstStartStation");
//                        String lastEndStation = odsayData.getJson().getJSONObject("result").getJSONObject("path").getJSONObject("info").getString("lastEndStation");
//
//
                        
//                        Log.d("totalTime : ", Integer.toString(totalTime));
//                        Log.d("firstStartStation : ", firstStartStation);
//                        Log.d("lastEndStation : ", lastEndStation);

//                        jsonObject = odsayData.getJson().getJSONObject("result").getJSONObject("path").getJSONObject("info");
//                        Log.d("result : ", jsonObject.toString());
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
        odsayService.requestSearchPubTransPath(lng.toString(), lat.toString(), "127.126936754911", "37.5004198786564", "0", "0", "0", onResultCallbackListener);








        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });



    }




    private String get_bus() {
        String temp = null;
        URL url = null;
        StringBuffer buffer = new StringBuffer();

        try {
            url = new URL(urlBuilder.toString());
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("start parse.... \n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if (tag.equals("itemList")) ;
                        else if (tag.equals("arsId")) {
                            buffer.append("arsID : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(("\n"));
                        } else if (tag.equals("dist")) {
                            buffer.append("dist : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(("\n"));
                        } else if (tag.equals("gpsX")) {
                            buffer.append("gpsX : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(("\n"));
                        } else if (tag.equals("gpsY")) {
                            buffer.append("gpsY : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(("\n"));
                        } else if (tag.equals("posX")) {
                            buffer.append("posX : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(("\n"));
                        } else if (tag.equals("posY")) {
                            buffer.append("posY : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(("\n"));
                        } else if (tag.equals("stationId")) {
                            buffer.append("stationID : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(("\n"));
                        } else if (tag.equals("stationNm")) {
                            buffer.append("stationname : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(("\n"));
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();

                        if (tag.equals("itemList")) buffer.append("\n");

                }

                eventType = xpp.next();
            }


        } catch (Exception e) { e.printStackTrace(); }

        buffer.append("end \n");
        return buffer.toString();
    }



    public void getID() {
        bus_station_name = findViewById(R.id.bus_station_name);
    }



} // End of class SearchActivity




//    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Content-type", "application/json");
//            System.out.println("Response code: " + conn.getResponseCode());
//    BufferedReader rd;
//
//            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//    } else {
//        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//    }
//    StringBuilder sb = new StringBuilder();
//    String line;
//            while ((line = rd.readLine()) != null) {
//        sb.append(line);
//    }
//
//            rd.close();
//            conn.disconnect();
//            Log.i("temp", sb.toString());
//    temp = sb.toString();
//
//
//} catch (MalformedURLException e) {
//        e.printStackTrace();
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//
//        return temp;



//bus API
//        try {
//                urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=pUOkfD4DYE6R21qRkJEv0bt1wyAFwIRywpkNRt8Ji6sRZy33Fe%2FuligXhZkeSzWs%2B%2FH9dnO8P2JRhfeIyiwuPA%3D%3D"); /*Service Key*/
//                urlBuilder.append("&" + URLEncoder.encode("tmX","UTF-8") + "=" + lat); /*기준위치 X*/
//                urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + lng); /*기준위치 Y*/
//                urlBuilder.append("&" + URLEncoder.encode("radius","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*단위 m(미터)*/
//                Log.e("유알엘 경고경고경고 : ", urlBuilder.toString());
//                } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                }