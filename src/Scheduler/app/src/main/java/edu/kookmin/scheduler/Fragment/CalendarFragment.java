package edu.kookmin.scheduler.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
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
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.kookmin.scheduler.Activity.AddSchedule;
import edu.kookmin.scheduler.Activity.MainActivity;
import edu.kookmin.scheduler.Object.Schedule;
import edu.kookmin.scheduler.R;
import edu.kookmin.scheduler.util.EventDecorator;
import edu.kookmin.scheduler.util.SaturdayDecorator;
import edu.kookmin.scheduler.util.SundayDecorator;
import edu.kookmin.scheduler.util.TodayDecorator;
import edu.kookmin.scheduler.util.util;

/**
 * 달력화면 ( MaterailCalendarView 라이브러리 사용 )
 * @author - 구윤모
 * @start - 2020.11.10
 * @finish - 2020.11.17
 */
public class CalendarFragment extends Fragment {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private Context mContext;
    private Activity activity;
    private String mUid;
    MaterialCalendarView materialCalendarView;
    final TodayDecorator todayDecorator = new TodayDecorator();
    private EventDecorator eventDecorator;
    private ArrayList<CalendarDay> dates = new ArrayList<>();
    private String tempDate;
    private int lateCount;
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

        // 메인액티비티에서 bundle로 넘겨준 mUid
        if (getArguments() != null) {
            mUid = getArguments().getString("mUid");
        }
        ref = database.getReference("User/").child(mUid).child("lateCount");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lateCount = snapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        CalendarDay date = new CalendarDay();

        // 기본 액션바 대신 Toolbar 사용.
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.app_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);
        actionBar.setTitle(Integer.toString(date.getYear()) + "년 " + Integer.toString(date.getMonth() + 1) + "월");

        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calenarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        //타이틀 안보이게
        materialCalendarView.setTopbarVisible(false);

        //달력 사이즈 조절
        materialCalendarView.setTileHeightDp(50);



        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                if (null != actionBar)
                    actionBar.setTitle(Integer.toString(date.getYear()) + "년 " + Integer.toString(date.getMonth() + 1) + "월");
            }
        });

        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(), todayDecorator);
        //날짜 클릭시 일정 출력
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener(){
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected){
                String str_year = new SimpleDateFormat("yyyy").format(date.getDate());
                String str_month =  new SimpleDateFormat("MM").format(date.getDate());
                String str_day = new SimpleDateFormat("dd").format(date.getDate()); int day = Integer.parseInt(str_day);
                String select_Date = str_year+str_month+String.format("%02d",day);

                //같은 날짜를 클릭했을 때 리스트 새로고침 방지
                if(tempDate==null){
                    ((MainActivity) getActivity()).replaceSchedule(ScheduleFragment.newInstance(mUid, select_Date));
                    tempDate = select_Date;
                }

                else if(!tempDate.equals(select_Date)) {
                    ((MainActivity) getActivity()).replaceSchedule(ScheduleFragment.newInstance(mUid, select_Date));
                    tempDate = select_Date;
                }

            }
        });


        ref = database.getReference("Schedule").child(mUid);
        ref.addChildEventListener(new ChildEventListener() {
            // snapshot 은 각 날짜의 Schedule 객체
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String temp = snapshot.getKey();
                int year = Integer.parseInt(temp.substring(0,4));
                int month = Integer.parseInt(temp.substring(4,6));
                int dayy = Integer.parseInt(temp.substring(6,8));

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
                eventDecorator = new EventDecorator(Color.RED, dates, activity);
                materialCalendarView.removeDecorator(eventDecorator);
                if(!dates.isEmpty()) {
                    materialCalendarView.addDecorators(eventDecorator);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                /**
                 * Todo 점 지우기가 안됩니다
                 */
                materialCalendarView.removeDecorator(eventDecorator);
                int count= (int) snapshot.getChildrenCount();
                if(count==1) {
                    dates.remove(snapshot.getValue(Schedule.class));
                    eventDecorator = new EventDecorator(Color.RED, dates, activity);
                    materialCalendarView.addDecorator(eventDecorator);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout_btn:
                util.signOut(FirebaseAuth.getInstance(), getActivity());
                break;
            case R.id.action_add_btn:
                Intent intent = new Intent(getActivity(), AddSchedule.class);
                startActivity(intent);
                break;
            case R.id.reset_lateCount:
                ref = database.getReference("User/").child(mUid).child("lateCount");
                ref.setValue(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}