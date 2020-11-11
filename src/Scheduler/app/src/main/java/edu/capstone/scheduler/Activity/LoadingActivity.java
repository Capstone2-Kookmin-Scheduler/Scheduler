package edu.capstone.scheduler.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import edu.capstone.scheduler.R;

public class LoadingActivity extends Activity {
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        permissionCheck();
    }

    private void permissionCheck(){
        if(!hasPermissions(this, PERMISSIONS)){
            getPermission();

        }
        // 권한이 허용되어있다면 다음 화면 진행
        else {
            startLoading();
        }
    }
    private void startLoading(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (Build.VERSION.SDK_INT >= 23) {

            // requestPermission의 배열의 index가 아래 grantResults index와 매칭
            // 퍼미션이 승인되면
            if(grantResults.length > 0  && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.d("LoadingActivity.","Permission: "+permissions[0]+ "was "+grantResults[0]);
                startLoading();
                // TODO : 퍼미션이 승인되는 경우에 대한 코드
            }
            // 퍼미션이 승인 거부되면
            else {
                if(recheckPermission(this,PERMISSIONS)){
                    Log.d("LoadingActivity.","Permission denied");
                    Toast.makeText(getApplicationContext(),"위치권한 필수",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    try {
                        Toast.makeText(getApplicationContext(),"위치권한 필수",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        finish();
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(intent);
                        finish();
                    }

                }
                // TODO : 퍼미션이 거부되는 경우에 대한 코드
            }
        }
    }
    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void getPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                },
                1000);
    }
    public static boolean recheckPermission (Activity activity ,String[] permissions){
        boolean isrpms = false;
        for (String s : permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, s)){
                isrpms = true;
                break;
            }
        }
        return isrpms;
    }
}
