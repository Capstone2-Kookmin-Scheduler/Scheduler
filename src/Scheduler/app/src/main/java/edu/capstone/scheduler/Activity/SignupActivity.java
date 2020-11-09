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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.capstone.scheduler.Object.User;
import edu.capstone.scheduler.R;

import static edu.capstone.scheduler.util.textFilter.*;


public class SignupActivity extends BaseActivity {
    private static final String TAG = "SignupActivity";

    private EditText idEdit;
    private EditText pwEdit;
    private Button signupFinishBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



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
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("User/");
        final User user = new User();
        user.setEmail(email);
        user.setLateCount(0);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            mUser = mAuth.getCurrentUser();
                            ref.child(mUser.getUid()).updateChildren(user.toMap());
                            SignupActivity.this.finish();
                        }
                        else{
                            Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
