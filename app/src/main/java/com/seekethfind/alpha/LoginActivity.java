package com.seekethfind.alpha;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.seekethfind.alpha.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //var
    private ProgressDialog mProgress;
    private Context mContext;

    private EditText mEmail,mPassword;
    private Button mLogin;

    private TextView mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        if(Build.VERSION.SDK_INT >= 19){
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }else{
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }

        mContext = LoginActivity.this;
        mProgress = new ProgressDialog(mContext);
        mAuth = FirebaseAuth.getInstance();

        initWidget();
        setUpFirebaseMethod();
        clickfunction();

    }

    private void initWidget(){

        mEmail = findViewById(R.id.emailId);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.btnLogin);
        mSignup = findViewById(R.id.signup);

    }

    private boolean isStringNull(String value){
        if(value.equals("")){
            return true;
        }
        return false;
    }

    private void clickfunction(){

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(isStringNull(email) && isStringNull(password)){
                    Toast.makeText(mContext, "Please Fill all Fields", Toast.LENGTH_SHORT).show();
                }else{
                    mProgress.setMessage("Check Login...");
                    mProgress.show();

                    //login setup start
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if(!task.isSuccessful()){
                                        mProgress.dismiss();

                                        Log.w(TAG, "signInWithEmail:failed", task.getException());

                                        Toast.makeText(mContext, "Login-Failed Try Again", Toast.LENGTH_SHORT).show();

                                    }else{

                                        try {
                                            if (user.isEmailVerified()) {
                                                mProgress.dismiss();

                                                Intent mainIntent = new Intent(mContext, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(mainIntent);
                                                finish();

                                            } else {
                                                Toast.makeText(mContext, "Email is not Varified check Email\n Try Again", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }
                                        }catch (NullPointerException e){
                                            Log.d(TAG,"NullPointerException "+ e.getMessage());
                                        }
                                    }
                                }
                            });

                }
            }
        });
 /*
        if the user already login then navigate to MainActivity and call finish()
         */
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

            //mAuth.signOut();

        }

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);

            }
        });

    }

    private void setUpFirebaseMethod(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }else{
                    Log.d(TAG,"onAuthStateChanged:signed_out:");
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
