package edu.capstone.scheduler.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import edu.capstone.scheduler.R;

public class CalendarActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser;

    MaterialCalendarView materialCalendarView;
    private long lastTimeBackPressed;
    private ArrayList<CalendarDay> dates = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mUser = mAuth.getCurrentUser();

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calenarView);

        //달력 시작과 끝 지정
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();


        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator());
        //날짜 클릭시 일정 출력
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener(){
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected){
                setContentView(R.layout.activity_show_list);

            }
        });







        /*특정날짜 달력에 점표시해주는곳*/
        /*월은 0이 1월 년,일은 그대로*/
        //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환


        ref = database.getReference("Schedule").child(mAuth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dates.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    String temp = snap.getKey();
                    Log.e("temp", temp);
                    int year = Integer.parseInt(temp.substring(0,4));
                    int month = Integer.parseInt(temp.substring(4,6));
                    int dayy = Integer.parseInt(temp.substring(6,8));
                    Log.e("날짜", Integer.toString(year) + Integer.toString(month) + Integer.toString(dayy));
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year,month-1,dayy);
                    CalendarDay day = CalendarDay.from(calendar);
                    dates.add(day);
                }
                Log.e("dates ",Integer.toString(dates.size()));
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                new GetDotTask(dates).executeOnExecutor(Executors.newSingleThreadExecutor());
                materialCalendarView.addDecorators(new EventDecorator(Color.RED, dates,CalendarActivity.this));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    @Override
    protected void onResume() {
        super.onResume();
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






    private class GetDotTask extends AsyncTask<Void, Void, List<CalendarDay>>{
        ArrayList<CalendarDay> dates = new ArrayList<>();
        GetDotTask(ArrayList<CalendarDay> dates){this.dates = dates;}

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> dates) {
            super.onPostExecute(dates);
            Log.e("dates의 크기", Integer.toString(dates.size()));
            materialCalendarView.addDecorators(new EventDecorator(Color.RED, dates,CalendarActivity.this));
        }
    }

}