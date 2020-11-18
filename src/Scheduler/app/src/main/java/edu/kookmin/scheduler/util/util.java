package edu.kookmin.scheduler.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.kookmin.scheduler.Activity.LoginActivity;

public class util {
    private static final String TAG = "util";

    // 로그아웃
    public static void signOut(FirebaseAuth mAuth, Activity activity){
        mAuth.getInstance().signOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    // 예상 출발시간 계산
    public static String calculateDepartureTime(int hour, int minute, int total_time) {
        int h = 0; int m = 0;
        int sum = hour * 60 + minute;
        sum -= total_time;
        h = sum / 60; m = sum % 60;
        String mStr = String.format("%02d",m);
        return (h + " : " + mStr);
    }

}
