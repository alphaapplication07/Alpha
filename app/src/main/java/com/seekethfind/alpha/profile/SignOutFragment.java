package com.seekethfind.alpha.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.seekethfind.alpha.LoginActivity;
import com.seekethfind.alpha.R;

public class SignOutFragment extends Fragment {
    private static final String TAG = "SignOutFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar mProgressbar;
    private TextView tvSignout,tvSigninOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signout, container, false);
        tvSignout = view.findViewById(R.id.tvConfirmSignout);
        tvSigninOut = view.findViewById(R.id.tvSigningOut);
        mProgressbar = view.findViewById(R.id.progressBar);

        mProgressbar.setVisibility(View.GONE);
        tvSigninOut.setVisibility(View.GONE);

        setupFirebaseAuth();

        Button mSIgnout = view.findViewById(R.id.btnConfirmSignout);

        mSIgnout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressbar.setVisibility(View.VISIBLE);
                tvSigninOut.setVisibility(View.VISIBLE);

                mAuth.signOut();
                getActivity().finish();
            }
        });


        return view;

    }

    /*

    ------------------------------------ Firebase ---------------------------------------------

     */





    /**

     * Setup the firebase auth object

     */

    private void setupFirebaseAuth(){

        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");



        mAuth = FirebaseAuth.getInstance();



        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override

            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();



                if (user != null) {

                    // User is signed in

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {

                    // User is signed out

                    Log.d(TAG, "onAuthStateChanged:signed_out");



                    Log.d(TAG, "onAuthStateChanged: navigating back to login screen.");

                    Intent intent = new Intent(getActivity(), LoginActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                }

                // ...

            }

        };

    }



    @Override

    public void onStart() {

        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

    }



    @Override

    public void onStop() {

        super.onStop();

        if (mAuthListener != null) {

            mAuth.removeAuthStateListener(mAuthListener);

        }

    }

}

