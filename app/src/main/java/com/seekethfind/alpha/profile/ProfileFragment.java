package com.seekethfind.alpha.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seekethfind.alpha.R;
import com.seekethfind.alpha.model.Comment;
import com.seekethfind.alpha.model.FirebaseMethod;
import com.seekethfind.alpha.model.Like;
import com.seekethfind.alpha.model.Photo;
import com.seekethfind.alpha.model.UserAccountSettings;
import com.seekethfind.alpha.model.User_SignUp;
import com.seekethfind.alpha.util.BottomNavigationViewHelper;
import com.seekethfind.alpha.util.GridAdapter;
import com.seekethfind.alpha.util.GridImageAdapter;
import com.seekethfind.alpha.util.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";


    public interface OnGridImageSelectedListener{

        void onGridImageSelected(Photo photo, int activityNumber);

    }

    OnGridImageSelectedListener mOnGridImageSelectedListener;


    private static final int ACTIVITY_NUM = 4;

    private ArrayList<String> Images;

    //firebase

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mDatabase;

    private DatabaseReference mRef;

    private FirebaseMethod mFirebaseMethods;

    //widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationView bottomNavigationView;
    private Context mContext;

    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mDisplayName = (TextView) view.findViewById(R.id.display_name);
        mUsername = (TextView) view.findViewById(R.id.username);
        mWebsite = (TextView) view.findViewById(R.id.website);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavViewBar);
        mProgressBar.setVisibility(View.GONE);

        mContext = getActivity();

        setupToolbar();
        setTopProfile();
        setupBottomNavigationView();

        setupFirebaseAuth();

        getPostsCount();
        getFollowersCount();
        getFollowingCount();
        setProfileImage();
        setProfileCenter();
        initImageLoader();
        setupGridView();

        TextView editProfile = (TextView) view.findViewById(R.id.textEditProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Log.d(TAG, "onClick: navigating to " + mContext.getString(R.string.edit_profile_fragment));

                Intent intent = new Intent(getActivity(), AccountSettingActivity.class);

                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));

                startActivity(intent);

                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }

        });

        return view;
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setProfileImage(){



            Query mQuery = mRef.child(getString(R.string.user_account_setting))
                    .child(mAuth.getCurrentUser().getUid());
            mQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserAccountSettings model = dataSnapshot.getValue(UserAccountSettings.class);
                    UniversalImageLoader.setImage(model.getProfile_photo(),mProfilePhoto,mProgressBar,"");
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

    public void onAttach(Context context) {

        try{

            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();

        }catch (ClassCastException e){

            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );

        }

        super.onAttach(context);

    }


    private void setupGridView(){

        final ArrayList<Photo> photos = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference

                .child(getString(R.string.dbname_user_photos))

                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    Photo photo = new Photo();

                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    try {

                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());



                        ArrayList<Comment> comments = new ArrayList<Comment>();

                        for (DataSnapshot dSnapshot : singleSnapshot

                                .child(getString(R.string.field_comments)).getChildren()) {

                            Comment comment = new Comment();

                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());

                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());

                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());

                            comments.add(comment);

                        }



                        photo.setComments(comments);



                        List<Like> likesList = new ArrayList<Like>();

                        for (DataSnapshot dSnapshot : singleSnapshot

                                .child(getString(R.string.field_likes)).getChildren()) {

                            Like like = new Like();

                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());

                            likesList.add(like);

                        }

                        photo.setLikes(likesList);

                        photos.add(photo);

                    }catch(NullPointerException e){

                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage() );

                    }

                }



                //setup our image grid

                int gridWidth = getResources().getDisplayMetrics().widthPixels;

                int imageWidth = gridWidth/3;

                gridView.setColumnWidth(imageWidth);



                ArrayList<String> imgUrls = new ArrayList<String>();

                for(int i = 0; i < photos.size(); i++){

                    imgUrls.add(photos.get(i).getImage_path());

                }

                GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,

                        "", imgUrls);

                gridView.setAdapter(adapter);



                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);

                    }

                });

            }



            @Override

            public void onCancelled(DatabaseError databaseError) {

                Log.d(TAG, "onCancelled: query cancelled.");

            }

        });

    }


    private void getFollowersCount(){

        mFollowersCount = 0;



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbname_followers))

                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());

                    mFollowersCount++;

                }

                mFollowers.setText(String.valueOf(mFollowersCount));

            }



            @Override

            public void onCancelled(DatabaseError databaseError) {



            }

        });

    }



    private void getFollowingCount(){

        mFollowingCount = 0;



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbname_following))

                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());

                    mFollowingCount++;

                }

                mFollowing.setText(String.valueOf(mFollowingCount));

            }



            @Override

            public void onCancelled(DatabaseError databaseError) {



            }

        });

    }



    private void getPostsCount(){

        mPostsCount = 0;



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbname_user_photos))

                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());

                    mPostsCount++;

                }

                mPosts.setText(String.valueOf(mPostsCount));

            }



            @Override

            public void onCancelled(DatabaseError databaseError) {


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

        private void setupToolbar(){

            ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(mContext, "Clicked By User", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, AccountSettingActivity.class);
                startActivity(intent);

            }
        });
    }


    private void setupBottomNavigationView(){

        BottomNavigationViewHelper.enableNavigation(mContext,getActivity(),bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }
//
//    private void setProfileWidgets(UserSettings userSettings) {
//
//        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
//
//        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());
//
//
//
//
//
//        //User user = userSettings.getUser();
//
//        UserAccountSettings settings = userSettings.getSettings();
//
//
//
//        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
//
//
//
////        Glide.with(getActivity())
//
////                .load(settings.getProfile_photo())
//
////                .into(mProfilePhoto);
//
//
//
//        mDisplayName.setText(settings.getDisplay_name());
//
//        mUsername.setText(settings.getUsername());
//
//        mWebsite.setText(settings.getWebsite());
//
//        mDescription.setText(settings.getDescription());
//
//        mProgressBar.setVisibility(View.GONE);
//
//    }

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

