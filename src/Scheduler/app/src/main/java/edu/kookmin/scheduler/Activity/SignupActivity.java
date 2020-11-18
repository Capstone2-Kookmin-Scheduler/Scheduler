package edu.kookmin.scheduler.Activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.kookmin.scheduler.Object.User;
import edu.kookmin.scheduler.R;

import static edu.kookmin.scheduler.util.textFilter.*;

/**
 * 회원가입화면
 * @author - 구윤모
 * @date - 2020.10.15
 */
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

    // 입력받은 이메일과 비밀번호로 Firebase Auth를 사용하여 가입을 시도
    private void createUser(String email, String password){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("User/"); // 가입이 된다면, Firebase Realtime DB에 저장하려고 경로를 지정.

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
                            /*
                             사용자는 고유의 UID를 가지고 있고, Firebase에서 자동으로 생성해줌.
                             User 객체를 map 형태로 바꾼 뒤 DB에 저장
                             */
                            SignupActivity.this.finish();
                        }
                        else{
                            Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
