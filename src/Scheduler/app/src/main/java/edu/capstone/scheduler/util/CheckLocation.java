package edu.capstone.scheduler.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;

import edu.capstone.scheduler.Activity.MainActivity;
import edu.capstone.scheduler.Activity.ShowMapActivity;
import edu.capstone.scheduler.Object.Schedule;
import edu.capstone.scheduler.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CheckLocation extends BroadcastReceiver {
    GpsTracker gpsTracker;
    private Double lat, lng, arrival_lat, arrival_lng;
    private int late_count, late_time;
    private int hour, minute;
    private int total_time;
    private String schedule_name;
    private String arrival_location;

    private NotificationManager notificationManager;
    private Notification noti;
    private NotificationChannel notificationChannel;
    private static final String NOTI_CHNNEL_ID = "noti_channel";
    private static int NOTI_ID = 1234;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        arrival_lat = intent.getExtras().getDouble("arrival_lat");
        arrival_lng = intent.getExtras().getDouble("arrival_lng");
        hour = intent.getExtras().getInt("hour");
        minute = intent.getExtras().getInt("minute");
        schedule_name = intent.getStringExtra("schedule_name");
        arrival_location = intent.getStringExtra("arrival_location");

        ODsayService odsayService = ODsayService.init(context,"suLGma46yOIqhKYbRFlIXAWLeDWumTQqfmY0RJ+ZnvE");
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        gpsTracker = new GpsTracker(context);
        lat = gpsTracker.getLat();
        lng = gpsTracker.getLng();
        Log.d("위도 경도 ", lat.toString() + " " + lng.toString());
        Log.d("시간체크","hour : "+ hour+" minute : "+minute);
        calculateTotalTime(lat, lng, arrival_lat, arrival_lng, hour, minute, odsayService);



    } //end of onReceive


    public boolean checkLocation (Double lat, Double lng, Double target_lat, Double target_lng) {
        if (Math.abs((lat + lng) - (target_lat + target_lng)) > 100){
            return true;
        }
        else return false;
    }
    public int getTotal_time(){
        return this.total_time;
    }
    public void calculateTotalTime (Double lat, Double lng, Double arrival_lat, Double arrival_lng, int hour, int minute,  ODsayService odsayService) {


        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                if(api == API.SEARCH_PUB_TRANS_PATH){
                    try {
                        JSONArray jsonArray = odsayData.getJson().getJSONObject("result").getJSONArray("path");
                        total_time = jsonArray.getJSONObject(1).getJSONObject("info").getInt("totalTime");
                        Log.i("예상 소요시간 ", Integer.toString(total_time));
                        Log.i("출발 시간  " , calculateDepartureTime(hour, minute, total_time));

                        noti(mContext,schedule_name,total_time,arrival_location);
                        notifi(notificationManager,noti, NOTI_ID);
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
        odsayService.requestSearchPubTransPath(lng.toString(), lat.toString(), arrival_lng.toString(), arrival_lat.toString(), "0", "0", "0", onResultCallbackListener);


    } // end of calculateTotalTime

    public String calculateDepartureTime (int hour, int minute, int total_time) {
        int h = 0; int m = 0;
        int sum = hour * 60 + minute;
        sum -= total_time;
        h = sum / 60; m = sum % 60;

        return (h + " : " + m);
    }

    public void noti(Context context,String schedule_name, int total_time, String arrival_location){
        notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationChannel = new NotificationChannel(NOTI_CHNNEL_ID, schedule_name, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("알림 테스트");
        notificationChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(notificationChannel);

        String time = Integer.toString(total_time) + "분";
        /**
         * 알림 클릭시 앱으로 이동 구현
         */
        Intent notiClickIntent = new Intent(context, ShowMapActivity.class);
        notiClickIntent.putExtra("NOTI_ID", NOTI_ID);
        notiClickIntent.putExtra("lat",lat);
        notiClickIntent.putExtra("lng",lng);
        notiClickIntent.putExtra("arrival_lat",arrival_lat);
        notiClickIntent.putExtra("arrival_lng",arrival_lng);

        PendingIntent notiPendingIntent = PendingIntent.getActivity(context, NOTI_ID, notiClickIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        noti = new NotificationCompat.Builder(context, NOTI_CHNNEL_ID)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentTitle(schedule_name)
                .setContentIntent(notiPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("도착지 : "+ arrival_location + "\n예상소요시간 : " + time)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

    }

    public void notifi(NotificationManager ntm, Notification noti, int notiId){
        Log.e("알림 확인","notifi");
        ntm.notify(notiId,noti);
    }
} // end of CheckLocation



//--------------------------------------------------------------------------------------------------
// Copyright
// file = "CheckLocation"
// Author = LeeJuHyoung
// Date = "2020-10-16" finalDate = "2020-10-20"