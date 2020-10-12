package edu.capstone.scheduler.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

import edu.capstone.scheduler.R;

public class CalendarActivity extends AppCompatActivity {

    MaterialCalendarView materialCalendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calenarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
    }
}