package com.seekethfind.alpha;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.seekethfind.alpha.model.FirebaseMethod;

import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private FirebaseMethod mFirebaseMethods;
    private ProgressDialog mProgressDialog;

    //var
    private String append = "";
    private Context mContext;

    private EditText mUserName,mEmail,mPass,mRe_Pass,mEnterNumber;
    private String name,email,pass,retypePass,enterNumber;
    private Button mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        if(Build.VERSION.SDK_INT >= 19){
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }else{
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }

        initWidget();
        startSignup();
        setupFirebaseMethod();
    }

    private void initWidget(){
        mUserName = findViewById(R.id.userName);
        mEmail = findViewById(R.id.emailId);
        mPass = findViewById(R.id.password);
        mRe_Pass = findViewById(R.id.retypePassword);
        mEnterNumber = findViewById(R.id.enter_number);

        mSignUp = findViewById(R.id.signup);

        mContext = SignupActivity.this;
        mProgressDialog = new ProgressDialog(mContext);

        mFirebaseMethods = new FirebaseMethod(mContext);

    }

    private void startSignup(){
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = mUserName.getText().toString();
                email = mEmail.getText().toString();
                pass = mPass.getText().toString();
                retypePass = mRe_Pass.getText().toString();
                enterNumber = mEnterNumber.getText().toString();

                if(pass.equals(retypePass)) {

                    if (checkInput(name,email,retypePass, enterNumber)) {
                        mFirebaseMethods.registerNewEmail(email, pass);

                        mProgressDialog.setMessage("Sending Email verification Code");
                        mProgressDialog.show();

                    }
                }else{
                    Toast.makeText(mContext, "Password doesnt match check and try again!...", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean checkInput(String userName, String mEmail, String mPass, String mNumber){

        if(userName.equals("") || mEmail.equals("") || mPass.equals("") || mNumber.equals("")){
            Toast.makeText(mContext, "All Field Must be Filled Out!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkUserNameExists(final String username){

        Log.d(TAG,"checkIfUsernameExists : checking if " + username + "already exists");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query mQuery = reference.child(getString(R.string.user_db))

                .child("User_Name")
                .equalTo(username);

        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.exists()){
                        //Log.d(TAG, "checkIfUsernameExists: FOUND & MATCH: " + ds.getValue(User_SignUp.class).getUser_Name());

                        append = mRef.push().getKey().substring(3, 7);

                        Log.d(TAG, "checkIfUsernameExists: username already exists append to random string to name: " + append);

                    }
                }

                String mUserName = "";
                mUserName = username + append;
                mFirebaseMethods.addNewUser(mUserName,email,retypePass,enterNumber);

                mAuth.signOut();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupFirebaseMethod(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG,"onAuthStateChanged: Signed_IN" + user.getUid());

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkUserNameExists(name);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    finish();

                }else{
                    Log.d(TAG,"onAuthStateChangedListener : Signed_Out");
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
        if(mAuth != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
