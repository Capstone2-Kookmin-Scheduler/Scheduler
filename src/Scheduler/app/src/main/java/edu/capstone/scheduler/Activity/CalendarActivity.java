package edu.capstone.scheduler.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

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

        String[] result = {"2020,11,05","2020,11,07"};

        new Dotdeco(result).executeOnExecutor(Executors.newSingleThreadExecutor());

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
                Intent intent = new Intent(getApplicationContext(), AddSchedule.class);
                startActivity(intent);
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


    private class Dotdeco extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        Dotdeco(String[] Time_Result){
            this.Time_Result = Time_Result;
        }
        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1,dayy);
            }



            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            if (isFinishing()) {
                return;
            }
            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays,CalendarActivity.this));
        }
    }

}