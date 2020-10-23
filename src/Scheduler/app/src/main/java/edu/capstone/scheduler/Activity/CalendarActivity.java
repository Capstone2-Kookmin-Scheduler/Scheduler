package edu.capstone.scheduler.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;

import edu.capstone.scheduler.R;

public class CalendarActivity extends AppCompatActivity {

    MaterialCalendarView materialCalendarView;
    private long lastTimeBackPressed;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calenarView);

        //달력 시작과 끝 지정
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        //일요일 토요일 색 변경
        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator());

        //날짜 클릭시 일정 출력
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener(){
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected){
                setContentView(R.layout.activity_list);

            }
        });

        // 하단 버튼 클릭 리스너
        Button todaybtn = findViewById(R.id.todaybtn);
        Button calendatbtn = findViewById(R.id.calendarbtn);
        Button idbtn = findViewById(R.id.idbtn);

        todaybtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_SHORT).show();
            }
        });

        calendatbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_SHORT).show();
            }
        });

        idbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_SHORT).show();
            }
        });

        // 플로팅 버튼
        FloatingActionButton plusbtn =  findViewById(R.id.plusbtn);

        plusbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_SHORT).show();
            }
        });
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