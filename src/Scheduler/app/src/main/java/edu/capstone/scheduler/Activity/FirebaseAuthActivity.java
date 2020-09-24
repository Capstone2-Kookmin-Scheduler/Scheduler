package edu.capstone.scheduler.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.capstone.scheduler.R;

public class FirebaseAuthActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuthActivity";
    private static final int RC_SIGN_IN = 123;

    private EditText email_editText;
    private EditText password_editText;
    private Button login_button;
    private Button signup_button;
    private SignInButton login_google_button;
    private Button google_signOut_button;

    private String email;
    private String password;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_auth);

        email_editText = (EditText) findViewById(R.id.email_editText);
        password_editText = (EditText) findViewById(R.id.password_editText);
        login_button = (Button) findViewById(R.id.login_button);
        signup_button = (Button) findViewById(R.id.signup_button);
        login_google_button = (SignInButton) findViewById(R.id.login_google_button);
        login_google_button.setSize(SignInButton.SIZE_STANDARD);
        google_signOut_button = (Button) findViewById(R.id.google_signOut);
        google_signOut_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirebaseAuthActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_editText.getText().toString();
                password = password_editText.getText().toString();
                login_email(email, password);
            }
        });
        login_google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(googleIntent, RC_SIGN_IN);
            }
        });

    }
    private void signout(){
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
    private void login_email(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"email 로그인 성공");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else{
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(FirebaseAuthActivity.this,"존재하지 않는 id 입니다." ,Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(FirebaseAuthActivity.this,"비밀번호가 틀립니다." ,Toast.LENGTH_SHORT).show();
                            } catch (FirebaseNetworkException e) {
                                Toast.makeText(FirebaseAuthActivity.this,"Firebase NetworkException" ,Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(FirebaseAuthActivity.this,"Exception" ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
            updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                Log.d(TAG, "googleSignInResult.isSuccess !!!");
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
//        try{
//            Log.d(TAG, "googleSignInResult.isSuccess !!!");
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            firebaseAuthWithGoogle(account.getIdToken());
//        }
//        catch(ApiException e){
//        }
//    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(),"구글계정으로 로그인 성공",Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();
        // [START auth_fui_pp_tos]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_pp_tos]
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
