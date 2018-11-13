package com.seekethfind.alpha.like;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.seekethfind.alpha.R;
import com.seekethfind.alpha.model.Comment;
import com.seekethfind.alpha.model.Like;
import com.seekethfind.alpha.model.Photo;
import com.seekethfind.alpha.model.UserAccountSettings;
import com.seekethfind.alpha.util.BottomNavigationViewHelper;
import com.seekethfind.alpha.util.LikeAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LikeActivity extends AppCompatActivity {
    private static final String TAG = "LikeActivity";

    private static final int ACTIVITY_NUM = 3;



    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    LikeAdapter adapter;
    //widgets
    private Photo mPhoto;
    private List<String> mList;
    private List<UserAccountSettings> mListUser;
    private RecyclerView mListview;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mList = new ArrayList<String>();
        mListUser = new ArrayList<>();

        mListview = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        mListview.setLayoutManager(layoutManager);

        makeList();

        adapter = new LikeAdapter(getApplicationContext(),mListUser);
        setupFirebaseAuth();
        setupBottomNavigationView();

    }

    private void makeList(){

        Query query = mRef.child(getString(R.string.dbname_photos))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){




                    List<Like> LikeList = new ArrayList<Like>();

                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_likes)).getChildren()){

                        Like like = new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        LikeList.add(like);


                        Query query = mRef.child(getString(R.string.user_account_setting))
                                .orderByChild(getString(R.string.field_user_id))
                                .equalTo(dSnapshot.getValue(Like.class).getUser_id());

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    UserAccountSettings settings =
                                            ds.getValue(UserAccountSettings.class);
                                    mListUser.add(settings);


                                }
                                mListview.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setupBottomNavigationView(){

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(LikeActivity.this, this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

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

        mDatabase = FirebaseDatabase.getInstance();

        mRef = mDatabase.getReference();



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

                }

                // ...

            }

        };





        mRef.addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {



                //retrieve user information from the database

                //setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));



                //retrieve images for the user in question



            }



            @Override

            public void onCancelled(DatabaseError databaseError) {



            }

        });

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
