package com.seekethfind.alpha.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seekethfind.alpha.R;
import com.seekethfind.alpha.model.FirebaseMethod;
import com.seekethfind.alpha.model.Photo;
import com.seekethfind.alpha.model.UserAccountSettings;
import com.seekethfind.alpha.model.User_SignUp;
import com.seekethfind.alpha.util.BottomNavigationViewHelper;
import com.seekethfind.alpha.util.GridAdapter;
import com.seekethfind.alpha.util.UniversalImageLoader;
import com.seekethfind.alpha.util.ViewCommentsFragment;
import com.seekethfind.alpha.util.ViewPostFragment;
import com.seekethfind.alpha.util.ViewProfileFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener ,

        ViewPostFragment.OnCommentThreadSelectedListener,

        ViewProfileFragment.OnGridImageSelectedListener{



    private static final String TAG = "ProfileActivity";



    @Override

    public void onCommentThreadSelectedListener(Photo photo) {

        Log.d(TAG, "onCommentThreadSelectedListener:  selected a comment thread");



        ViewCommentsFragment fragment = new ViewCommentsFragment();

        Bundle args = new Bundle();

        args.putParcelable(getString(R.string.camera), photo);

        fragment.setArguments(args);



        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.container, fragment);

        transaction.addToBackStack(getString(R.string.view_comments_fragment));

        transaction.commit();

    }



    @Override

    public void onGridImageSelected(Photo photo, int activityNumber) {

        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + photo.toString());



        ViewPostFragment fragment = new ViewPostFragment();

        Bundle args = new Bundle();

        args.putParcelable(getString(R.string.camera), photo);

        args.putInt(getString(R.string.activity_number), activityNumber);



        fragment.setArguments(args);



        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.container, fragment);

        transaction.addToBackStack(getString(R.string.view_post_fragment));

        transaction.commit();


    }

//    private int[] Images = {R.drawable.image,R.drawable.image,R.drawable.image
//
//            ,R.drawable.image,R.drawable.image,R.drawable.image
//
//            ,R.drawable.image,R.drawable.image,R.drawable.image};

    private ArrayList<String> Images;
    private static final int ACTIVITY_NUM = 4;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseMethod mFirebaseMethods;
    private Query mQuery;

    //widgets
    private TextView mPosts, mFollowers, mFollowing, mEditProfile, mDisplayName, mUsername, mWebsite, mDescription;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationView bottomNavigationView;
    private Context mContext;

    private ProgressBar mProgressbar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

//        mProgressbar = findViewById(R.id.profileProgressBar);
//        mProgressbar.setVisibility(View.GONE);

        init();

//        setupToolbar();
//        setupActivityWidgets();
//
//        initImageLoader();
//        setProfileImage();
//
//        setTopProfile();
//        setProfileCenter();
//
//        setupImageGrid();

//        setupBottomNavigationView();

    }

    private void init(){

        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.calling_activity))){

            Log.d(TAG, "init: searching for user object attached as intent extra");

            if(intent.hasExtra(getString(R.string.intent_user))){
                User_SignUp user = intent.getParcelableExtra(getString(R.string.intent_user));

                if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                    Log.d(TAG, "init: inflating view profile");

                    ViewProfileFragment fragment = new ViewProfileFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user),
                            intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    transaction.commit();

                }else{

                    Log.d(TAG, "init: inflating Profile");
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(getString(R.string.profile_fragment));
                    transaction.commit();

                }
            }else{
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }

        }else{

            Log.d(TAG, "init: inflating Profile");
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();

        }



//
//        ProfileFragment fragment = new ProfileFragment();
//        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container,fragment);
//        transaction.addToBackStack(getString(R.string.profile_fragment));
//        transaction.commit();
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getApplicationContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setProfileImage(){
//        String imgURL = "https://firebasestorage.googleapis.com/v0/b/alpha-a3390.appspot.com/o/images%2Fimage.png?alt=media&token=3c23f2af-727e-4eab-8164-2dfdb75ac7e6";
//        UniversalImageLoader.setImage(imgURL,profilePhoto,mProgressbar,"");

        Query mQuery = mRef.child(getString(R.string.user_account_setting))
                .child(mAuth.getCurrentUser().getUid());
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccountSettings model = dataSnapshot.getValue(UserAccountSettings.class);
                UniversalImageLoader.setImage(model.getProfile_photo(),profilePhoto,mProgressbar,"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupImageGrid(){
        final GridView grid = findViewById(R.id.gridView);

        Images = new ArrayList<>();
//        for(int i=0; i<9; i++){
//            Images.add("https://firebasestorage.googleapis.com/v0/b/alpha-a3390.appspot.com/o/images%2Fimage.png?alt=media&token=3c23f2af-727e-4eab-8164-2dfdb75ac7e6");
//
//        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){

                   Photo photo = singleSnapshot.getValue(Photo.class);
                   Images.add(photo.getImage_path());
                   GridAdapter adapter = new GridAdapter(getApplicationContext(),Images);

                   grid.setAdapter(adapter);
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(ProfileActivity.this, "You Clicked at " +position, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setTopProfile(){

        Query mQuery = mRef.child(getString(R.string.user_db))
                .child(mAuth.getCurrentUser().getUid())
                .child(getString(R.string.user_db_info));
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User_SignUp model = dataSnapshot.getValue(User_SignUp.class);
                String name = model.getUser_Name();
                mUsername.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileCenter(){
        Query mQuery = mRef.child(getString(R.string.user_account_setting))
                .child(mAuth.getCurrentUser().getUid());
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccountSettings model = dataSnapshot.getValue(UserAccountSettings.class);
                mDisplayName.setText(model.getDisplay_name());
                mDescription.setText(model.getDescription());
                mWebsite.setText(model.getWebsite());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setupActivityWidgets(){
        mProgressbar = findViewById(R.id.profileProgressBar);
        mProgressbar.setVisibility(View.GONE);
        profilePhoto = findViewById(R.id.profile_photo);
        mDisplayName = (TextView) findViewById(R.id.display_name);
        mUsername = (TextView) findViewById(R.id.username);
        mWebsite = (TextView) findViewById(R.id.website);
        mDescription = (TextView) findViewById(R.id.description);
        mPosts = (TextView)findViewById(R.id.tvPosts);
        mFollowers = (TextView) findViewById(R.id.tvFollowers);
        mFollowing = (TextView) findViewById(R.id.tvFollowing);
        mEditProfile = findViewById(R.id.textEditProfile);
        gridView = (GridView) findViewById(R.id.gridView);
        toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        mContext = getApplicationContext();
        mFirebaseMethods = new FirebaseMethod(mContext);

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AccountSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);

        ImageView profileMenu = findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(ProfileActivity.this, "Clicked By User", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, AccountSettingActivity.class);
                startActivity(intent);

            }
        });
    }

    private void setupBottomNavigationView(){

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(ProfileActivity.this, this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }



}
