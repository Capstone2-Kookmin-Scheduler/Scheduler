package edu.kookmin.scheduler.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.kookmin.scheduler.Activity.ShowMapActivity;
import edu.kookmin.scheduler.Object.Schedule;
import edu.kookmin.scheduler.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * BroadcastReceiver로 백그라운드에서 알람이 실행될 시 현재위치 확인 후 목적지까지의 경로 계산
 * @author - 구윤모, 이주형
 * @start - 2020.10.16
 * @finish - 2020.11.17
 */
public class CheckLocation extends BroadcastReceiver {
    GpsTracker gpsTracker;
    private Double lat, lng, arrival_lat, arrival_lng;
    private int hour, minute;
    private String schedule_name;
    private String arrival_location;
    private String date;
    private String schedule_uid;

    private NotificationManager notificationManager;
    private Notification noti;
    private NotificationChannel notificationChannel;
    private static final String NOTI_CHNNEL_ID = "noti_channel";
    private static int NOTI_ID = 1234;
    private Context mContext;

    private String mUid;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        //schedule = (Schedule)intent.getSerializableExtra("schedule");

        arrival_lat = intent.getExtras().getDouble("arrival_lat");
        arrival_lng = intent.getExtras().getDouble("arrival_lng");
        hour = intent.getExtras().getInt("hour");
        minute = intent.getExtras().getInt("minute");
        schedule_name = intent.getStringExtra("schedule_name");
        arrival_location = intent.getStringExtra("arrival_location");
        mUid = intent.getStringExtra("mUid");
        date = intent.getStringExtra("date");
        schedule_uid = intent.getStringExtra("schedule_uid");
        ref = database.getReference("Schedule").child(mUid).child(date).child(schedule_uid).child("total_time");

        gpsTracker = new GpsTracker(context);
        lat = gpsTracker.getLat();
        lng = gpsTracker.getLng();
        Log.d("위도 경도 ", lat.toString() + " " + lng.toString());
        Log.d("시간체크","hour : "+ hour+" minute : "+minute);

        NetworkTask task = new NetworkTask();
        task.execute(lat,lng,arrival_lat,arrival_lng);



    } //end of onReceive





    // notification을통해 현재위치에서 목적지까지의 정보를통해 출발시간을 출력해주는 알림생성
    public void noti(Context context,String schedule_name, int total_time, String arrival_location){
        notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationChannel = new NotificationChannel(NOTI_CHNNEL_ID, schedule_name, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("알림 테스트");
        notificationChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(notificationChannel);

        Intent notiClickIntent = new Intent(context, ShowMapActivity.class);
        notiClickIntent.putExtra("lat",lat);
        notiClickIntent.putExtra("lng",lng);
        notiClickIntent.putExtra("arrival_lat",arrival_lat);
        notiClickIntent.putExtra("arrival_lng",arrival_lng);

        PendingIntent notiPendingIntent = PendingIntent.getActivity(context, NOTI_ID, notiClickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // 알림 형식 설정
        noti = new NotificationCompat.Builder(context, NOTI_CHNNEL_ID)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentTitle("예상 출발시간 - " + util.calculateDepartureTime(hour,minute, total_time))
                .setContentIntent(notiPendingIntent)
                .setSmallIcon(R.mipmap.calendar_icon)
                .setContentText("목적지 - "+ arrival_location)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

    }
    // ID 에 맞게 알림 실행
    public void notifi(NotificationManager ntm, Notification noti, int notiId){
        Log.e("알림 확인","notifi");
        ntm.notify(notiId,noti);
    }

    // 비동기 처리 쓰레드
    // Background에서 GoogleDirectionAPI 를 통해 소요시간을 계산하여 반환해주며, 계산결과를 DB에저장 및 알림실행
    class NetworkTask extends AsyncTask<Double, Void, Integer> {
        @Override
        protected Integer doInBackground(Double... latlng) {
            int result;
            result = find_route(latlng[0],latlng[1],latlng[2],latlng[3]);
            return result;
        }

        @Override
        protected void onPostExecute(Integer time) {
            super.onPostExecute(time);

            ref.setValue(time);
            noti(mContext,schedule_name,time,arrival_location);
            notifi(notificationManager,noti, NOTI_ID);
        }
    }
    // HTTPConnection 을 통해 소요시간 계산 , JSON파일로 파싱해와 값 확인
    private int find_route(Double lat1, Double lng1, Double lat2, Double lng2) {
        HttpURLConnection urlConnection = null;
        StringBuffer stringBuffer = new StringBuffer();

        String address = "https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lng1 +
                "&destination=" + lat2 + "," + lng2 + "&mode=transit&departure_time=now&language=ko&key=" + mContext.getString(R.string.place_api_key);


        try {
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            String strParams = stringBuffer.toString();
            OutputStream os = urlConnection.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            os.flush(); os.close();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) return 0;

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String line; String page = "";

            while((line = reader.readLine()) != null) {
                page += line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(page);
            String time = element.getAsJsonObject().get("routes").getAsJsonArray().get(0).getAsJsonObject().get("legs").getAsJsonArray().get(0).getAsJsonObject().get("duration").getAsJsonObject().get("text").getAsString();
            String[] timeArray = time.split("분");

            return Integer.parseInt(timeArray[0]);


        }catch (IOException e) {
            e.printStackTrace();
        } // end of catch exception


        return 0;
    } // end of find_route method


    public boolean checkLocation (Double lat, Double lng, Double target_lat, Double target_lng) {
        if (Math.abs((lat + lng) - (target_lat + target_lng)) > 100){
            return true;
        }
        else return false;
    }
} // end of CheckLocation
