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
import android.widget.EditText;
import android.widget.ImageView;
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
import com.seekethfind.alpha.model.UserAccountSettings;
import com.seekethfind.alpha.model.User_SignUp;
import com.seekethfind.alpha.share.ShareActivity;
import com.seekethfind.alpha.util.UniversalImageLoader;

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";

    private ImageView mProfilePhoto;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseMethod firebaseMethod;

    private EditText mUserName,mDisplayName,mWebsite,mDescription,mEmail,mPhoneNumber;
    private TextView mChangeProfilePhoto;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        firebaseMethod = new FirebaseMethod(getActivity());
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mUserName = view.findViewById(R.id.username);
        mDisplayName = view.findViewById(R.id.display_name);
        mWebsite = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);
        mEmail = view.findViewById(R.id.email);
        mPhoneNumber = view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = view.findViewById(R.id.changeProfilePhoto);
        initImageLoader();
        setProfilePhoto();

        setValues();

        //back arrw for navigatingback to "ProfileActivity"
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        ImageView checkmark = view.findViewById(R.id.saveChanges);

        checkmark.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Log.d(TAG, "onClick: attempting to save changes.");

                updateProfile();
            }

        });

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        });

        return view;

    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setProfilePhoto(){
        Log.d(TAG," setProfilePhoto: setting Profile image. ");

        Query mQuery = mRef.child(getString(R.string.user_account_setting))

                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);
                UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setValues(){

        Query mQuery = mRef.child(getString(R.string.user_db))
                .child(mAuth.getCurrentUser().getUid())
                .child(getString(R.string.user_db_info));
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User_SignUp model = dataSnapshot.getValue(User_SignUp.class);
                String name = model.getUser_Name();
                String email = model.getUser_Email();
                String phone = model.getUser_mobile();
                mUserName.setText(name);
                mEmail.setText(email);
                mPhoneNumber.setText(phone);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query mQuery1 = mRef.child(getString(R.string.user_account_setting))
                .child(mAuth.getCurrentUser().getUid());
        mQuery1.addValueEventListener(new ValueEventListener() {
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

    private void updateProfile(){
        final String displayName = mDisplayName.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();

        firebaseMethod.updateProfileData(displayName,website, description);
        Toast.makeText(getActivity(), "Value Update Successful", Toast.LENGTH_SHORT).show();

    }
}
