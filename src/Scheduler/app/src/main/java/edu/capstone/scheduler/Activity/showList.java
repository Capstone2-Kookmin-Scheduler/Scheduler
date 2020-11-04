package edu.capstone.scheduler.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.capstone.scheduler.Object.Schedule;
import edu.capstone.scheduler.R;
import edu.capstone.scheduler.util.MainAdapter;

public class showList extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser;

    private MainAdapter mainAdapter;
    RecyclerView recyclerView;
    List<Schedule> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        mUser = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        mainAdapter = new MainAdapter(list);
        recyclerView.setAdapter(mainAdapter);

        //getUid() 로 수정
        ref = database.getReference("Schedule").child(mUser.getUid());
        /**
         * Todo child이벤트리스너로 변경
         */

        java.util.Date currentTime = new java.util.Date();
        SimpleDateFormat format_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat format_month = new SimpleDateFormat("MM");
        SimpleDateFormat format_day = new SimpleDateFormat("dd");
        String str_year = format_year.format(currentTime); int year = Integer.parseInt(str_year);
        String str_month = format_month.format(currentTime); int month = Integer.parseInt(str_month);
        String str_day = format_day.format(currentTime); int day = Integer.parseInt(str_day);

        Log.e("날짜",str_year+str_month+String.format("%02d",day));
        ref.child(str_year+str_month+String.format("%02d",day)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for(DataSnapshot item : snapshot.getChildren()) {
                    Schedule schedule = item.getValue(Schedule.class);
                    list.add(schedule);
                    mainAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    } // end of onCreate


} // end of class showList

