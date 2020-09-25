package edu.capstone.scheduler.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import edu.capstone.scheduler.R;
import edu.capstone.scheduler.util.util;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button signOut_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signOut_button = (Button)findViewById(R.id.signOut_button);
        signOut_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.signOut(mAuth, MainActivity.this);
            }
        });
    }

}
