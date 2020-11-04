package edu.capstone.scheduler.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.capstone.scheduler.Object.Schedule;
import edu.capstone.scheduler.R;
import edu.capstone.scheduler.util.util;

public class TestActivity extends AppCompatActivity {
    private Button signOut_button;
    private Button add_schedule_BTN;
    private TextView textView1;
    private TextView textView2;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mUser = mAuth.getCurrentUser();
        textView1 = (TextView)findViewById(R.id.textview1);
        textView2 = (TextView)findViewById(R.id.textview2);

        add_schedule_BTN = (Button)findViewById(R.id.add_schedule);
        add_schedule_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
            }
        });

        signOut_button = (Button)findViewById(R.id.signOut_button);
        signOut_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.signOut(mAuth, TestActivity.this);
            }
        });



        ref = database.getReference("Schedule").child(mUser.getUid());




        ref.child("20200919").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Schedule schedule = snapshot.getValue(Schedule.class);
                String name = schedule.getName();
                String arrival = schedule.getArrival_location();
                if(name.equals("일정2")){
                    textView1.setText("이름 : "+name + " 도착지점 : "+arrival);
                }
                else
                    textView2.setText("이름 : "+name + " 도착지점 : "+arrival);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}
