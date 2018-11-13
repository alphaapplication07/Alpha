package com.seekethfind.alpha;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.seekethfind.alpha.model.UserAccountSettings;

public class ForgetPassword extends AppCompatActivity {
    private static final String TAG = "ForgetPassword";

    private EditText mEnterUsername;
    private Button mSubmit;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        init();


    }

    private void init(){
        mEnterUsername = findViewById(R.id.enter_username_forget);
        mSubmit = findViewById(R.id.submit);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String forgetPass = mEnterUsername.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(forgetPass)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPassword.this,
                                            "Check Mail on your Email-ID...", Toast.LENGTH_SHORT).show();

                                    Log.d(TAG, "Email sent.");

                                    Intent intent = new Intent(ForgetPassword.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });

            }
        });

    }


}
