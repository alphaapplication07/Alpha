package com.seekethfind.alpha.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seekethfind.alpha.R;
import com.seekethfind.alpha.model.Photo;
import com.seekethfind.alpha.model.User_SignUp;
import com.seekethfind.alpha.util.BottomNavigationViewHelper;
import com.seekethfind.alpha.util.MainfeedListAdapter;
import com.seekethfind.alpha.util.SectionPagerAdapter;
import com.seekethfind.alpha.util.UniversalImageLoader;
import com.seekethfind.alpha.util.ViewCommentsFragment;
import com.seekethfind.alpha.util.ViewProfileFragment;

public class MainActivity extends AppCompatActivity implements
        MainfeedListAdapter.OnLoadMoreItemsListener{


    @Override
    public void onLoadMoreItems() {

        Log.d(TAG, "onLoadMoreItems: displaying more photos");

        HomeFragment fragment = (HomeFragment) getSupportFragmentManager()

                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());

        if (fragment != null) {

            fragment.displayMorePhotos();
        }

    }

    private static final String TAG = "MainActivity";

    private static final int ACTIVITY_NUM = 0;
    private Context mContext = MainActivity.this;
    private static final int HOME_FRAGMENT = 1;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private TextView mUsername;
    BottomNavigationView bottomNavigationView;

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

        setupViewPager();
        initImageLoader();

        setupBottomNavigationView();

    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getApplicationContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void onCommentThreadSelected(Photo photo, String callingActivity){

        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");



        ViewCommentsFragment fragment  = new ViewCommentsFragment();

        Bundle args = new Bundle();

        args.putParcelable(getString(R.string.camera), photo);

        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));

        fragment.setArguments(args);



        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.container, fragment);

        transaction.addToBackStack(getString(R.string.view_comments_fragment));

        transaction.commit();

    }

    public void hideLayout(){
        Log.d(TAG,"hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout(){
        Log.d(TAG,"hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }

    private void setupViewPager(){

        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new HomeFragment());

        mViewPager.setAdapter(adapter);

    }

    private void setupBottomNavigationView(){

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(MainActivity.this, this ,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }


}
