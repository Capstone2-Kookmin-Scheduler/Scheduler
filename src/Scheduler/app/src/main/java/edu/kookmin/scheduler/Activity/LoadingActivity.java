package edu.kookmin.scheduler.Activity;

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

import edu.kookmin.scheduler.R;


/**
 * 앱 실행 시 나오는 로딩액티비티
 * @author - 이주형
 * @start - 2020.10.26
 * @finish - 2020.10.27
*/
public class LoadingActivity extends Activity {

    /**
     * 앱에서 요청해야 하는 권한 리스트 작성
     */
    // 권한 체크를 위해 배열에 담음
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        permissionCheck();
    }

    // 권한이 주어졌는지 확인 -> 없으면 getPermisiion(), 있으면 진행
    private void permissionCheck(){
        if(!hasPermissions(this, PERMISSIONS)){
            getPermission();

        }
        // 권한이 허용되어있다면 다음 화면 진행
        else {
            startLoading();
        }
    }

    // handler를 이용하여 약 2초간 액티비티를 띄워주고 다음 액티비티로 전환.
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
            }
        }
    }

    // 권한이 있는가 확인
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

    // 권한 요청
    private void getPermission(){
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1000);
    }

    // 권한 거부했을 때 앱설정으로 이동.(위치 권한 필수)
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
