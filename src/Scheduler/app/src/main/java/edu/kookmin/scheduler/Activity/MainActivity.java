package edu.kookmin.scheduler.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;

import edu.kookmin.scheduler.Fragment.*;
import edu.kookmin.scheduler.R;



/*
 Fragment 두개로 달력과 일정을 나타냈음
 각 날짜 클릭시 해당하는 날짜의 일정을 scheduleFragment에서 변경.
 */
/**
 * 앱 메인화면 ( 달력과 일정목록 )
 * @author - 구윤모
 * @start - 2020.11.08
 * @finish - 2020.11.11
 */
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

        // 각 프래그먼트에서 쓸 변수를 bundle로 보냄.
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

    // 날짜 클릭시 scheduleFragment가 변경되게하는 ui 변경 메소드
    public void replaceSchedule(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.scheduleFragment, fragment).commit();
    }

    //종료 버튼
    @Override
    public void onBackPressed(){
        // 뒤로가기 버튼을 1.5초 안에 한번더 누른다면 종료.
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }

        Toast.makeText(this,"한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();

        lastTimeBackPressed = System.currentTimeMillis();
    }

}
