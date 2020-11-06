package edu.capstone.scheduler.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.capstone.scheduler.Activity.CalendarActivity;
import edu.capstone.scheduler.Activity.MainActivity;
import edu.capstone.scheduler.R;
import edu.capstone.scheduler.util.EventDecorator;
import edu.capstone.scheduler.util.SaturdayDecorator;
import edu.capstone.scheduler.util.SundayDecorator;
import edu.capstone.scheduler.util.TodayDecorator;

public class CalendarFragment extends Fragment {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    private String mUid;
    MaterialCalendarView materialCalendarView;
    final TodayDecorator todayDecorator = new TodayDecorator();

    private ArrayList<CalendarDay> dates = new ArrayList<>();

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(String mUid) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString("mUid", mUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUid = getArguments().getString("mUid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calenarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();


        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(), todayDecorator);
        //날짜 클릭시 일정 출력
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener(){
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected){
                /**
                 * Todo 날짜 클릭시 ScheduleFragment 변경
                 */
                String str_year = new SimpleDateFormat("yyyy").format(date.getDate());
                String str_month =  new SimpleDateFormat("MM").format(date.getDate());
                String str_day = new SimpleDateFormat("dd").format(date.getDate()); int day = Integer.parseInt(str_day);
                String select_Date = str_year+str_month+String.format("%02d",day);

                ((MainActivity)getActivity()).replaceSchedule(ScheduleFragment.newInstance(mUid,select_Date));

            }
        });

        ref = database.getReference("Schedule").child(mUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dates.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    String temp = snap.getKey();
                    int year = Integer.parseInt(temp.substring(0,4));
                    int month = Integer.parseInt(temp.substring(4,6));
                    int dayy = Integer.parseInt(temp.substring(6,8));

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year,month-1,dayy);
                    CalendarDay day = CalendarDay.from(calendar);
                    dates.add(day);
                }
                materialCalendarView.addDecorators(new EventDecorator(Color.RED, dates, getActivity()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}