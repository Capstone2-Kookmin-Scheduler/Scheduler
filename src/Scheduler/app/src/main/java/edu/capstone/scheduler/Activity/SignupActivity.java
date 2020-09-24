package edu.capstone.scheduler.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.capstone.scheduler.R;

import static edu.capstone.scheduler.util.textFilter.*;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText idEdit;
    private EditText pwEdit;
    private Button signupFinishBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        idEdit = (EditText)findViewById(R.id.id_signup);

        pwEdit = (EditText)findViewById(R.id.pw_signup);
        pwEdit.setFilters(new InputFilter[]{filterAlphaNum});

        signupFinishBtn = (Button)findViewById(R.id.signupFinishBtn);
        signupFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(idEdit.getText().toString(),pwEdit.getText().toString());
            }
        });
    }
    private void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            SignupActivity.this.finish();
                        }
                        else{
                            Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
