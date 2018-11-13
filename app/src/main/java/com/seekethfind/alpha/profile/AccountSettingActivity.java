package com.seekethfind.alpha.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.seekethfind.alpha.R;
import com.seekethfind.alpha.main.MainActivity;
import com.seekethfind.alpha.model.FirebaseMethod;
import com.seekethfind.alpha.util.BottomNavigationViewHelper;
import com.seekethfind.alpha.util.SectionStatePagerAdapter;

import java.util.ArrayList;

public class AccountSettingActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingActivity";
    private static final int ACTIVITY_NUM = 4;

    private Context mContext;
    ImageView backArrow;

    public SectionStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetting);

        mContext = AccountSettingActivity.this;

        mViewPager = findViewById(R.id.viewpager_container);
        mRelativeLayout = findViewById(R.id.relLayout1);

        setupFragments();
        setupSettingList();
        setupBottomNavigationView();
        getIncomingIntent();

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getIncomingIntent(){

        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))){
            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment

            Log.d(TAG, "getIncomingIntent: New incoming imgUrl");

            if(intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))){

                if(intent.hasExtra(getString(R.string.selected_image))){
                    //set the new profile picture
                    FirebaseMethod firebaseMethods = new FirebaseMethod(AccountSettingActivity.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,

                            intent.getStringExtra(getString(R.string.selected_image)), null);

                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    //set the new profile picture
                    FirebaseMethod firebaseMethods = new FirebaseMethod(AccountSettingActivity.this);

                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,

                            null,(Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));

                }

            }

        }

        if(intent.hasExtra(getString(R.string.calling_activity))){

            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));

            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));

        }

    }

    private void setupFragments(){

        pagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment)); //fragment 0

        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment)); //fragment 1

    }

    public void setViewPager(int fragmentNumber){

        mRelativeLayout.setVisibility(View.GONE);

        Log.d(TAG, "setViewPager: navigating to fragment #: " + fragmentNumber);

        mViewPager.setAdapter(pagerAdapter);

        mViewPager.setCurrentItem(fragmentNumber);

    }

    private void setupSettingList(){
        ListView listView = findViewById(R.id.lvAccountSettings);

        ArrayList<String> option = new ArrayList<>();
        option.add(getString(R.string.edit_profile_fragment));
        option.add(getString(R.string.sign_out_fragment));

        ArrayAdapter adapter = new ArrayAdapter(mContext,
                android.R.layout.simple_list_item_1,option);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onItemClick: navigating to fragment#: " + position);

                setViewPager(position);

            }

        });
    }

    private void setupBottomNavigationView(){

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(AccountSettingActivity.this, this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }
}
