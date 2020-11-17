package edu.capstone.scheduler.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;

import edu.capstone.scheduler.Fragment.*;
import edu.capstone.scheduler.R;

public class MainActivity extends BaseActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser;

    private CalendarFragment calendarFragment = new CalendarFragment();
    private ScheduleFragment scheduleFragment = new ScheduleFragment();

    private String mUid;
    private long lastTimeBackPressed;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = mAuth.getCurrentUser();
        mUid = mUser.getUid();
        java.util.Date currentTime = new java.util.Date();
        String str_year = new SimpleDateFormat("yyyy").format(currentTime);
        String str_month =  new SimpleDateFormat("MM").format(currentTime);
        String str_day = new SimpleDateFormat("dd").format(currentTime); int day = Integer.parseInt(str_day);
        String today = str_year+str_month+String.format("%02d",day);
        Bundle bundle = new Bundle();
        bundle.putString("mUid",mUid);
        bundle.putString("date",today);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        calendarFragment.setArguments(bundle);
        scheduleFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.calendarFragment,calendarFragment);
        fragmentTransaction.replace(R.id.scheduleFragment,scheduleFragment);
        fragmentTransaction.commit();

    }
    public void replaceSchedule(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.scheduleFragment, fragment).commit();
    }

    //종료 버튼
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }

        Toast.makeText(this,"한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();

        lastTimeBackPressed = System.currentTimeMillis();
    }

}
