package edu.kookmin.scheduler.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.kookmin.scheduler.Object.Schedule;
import edu.kookmin.scheduler.R;
import edu.kookmin.scheduler.util.ScheduleListAdapter;

/**
 * 일정 프래그먼트
 * @author - 구윤모, 이주형
 * @start - 2020.11.11
 * @finish - 2020.11.17
 */
public class ScheduleFragment extends Fragment {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    private ScheduleListAdapter scheduleListAdapter;
    RecyclerView recyclerView;
    List<Schedule> list = new ArrayList<>();

    private String mUid;
    private String date;


    public ScheduleFragment() {
        // Required empty public constructor
    }

    // replaceSchedule에서 쓰기위해 uid와 선택한 날짜를 입력으로 받음.
    public static ScheduleFragment newInstance(String mUid,String selectDate) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString("mUid",mUid);
        args.putString("date",selectDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUid = getArguments().getString("mUid");
            date = getArguments().getString("date");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        // RecyclerView로 일정 리스트 표현. ScheduleListAdapter를 커스텀으로 만들어서 사용.

        recyclerView = view.findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        scheduleListAdapter = new ScheduleListAdapter(list);
        recyclerView.setAdapter(scheduleListAdapter);

        // 해당 날짜의 Schedule 객체 DB를 가져와 list에 추가.
        ref = database.getReference("Schedule").child(mUid).child(date);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                list.add(snapshot.getValue(Schedule.class));
                scheduleListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                list.remove(snapshot.getValue(Schedule.class));
                scheduleListAdapter.notifyDataSetChanged();
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
}