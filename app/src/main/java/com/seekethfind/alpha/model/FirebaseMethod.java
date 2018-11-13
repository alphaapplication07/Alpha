package com.seekethfind.alpha.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import com.seekethfind.alpha.R;
import com.seekethfind.alpha.main.MainActivity;
import com.seekethfind.alpha.profile.AccountSettingActivity;
import com.seekethfind.alpha.util.FilePaths;
import com.seekethfind.alpha.util.ImageManager;
import com.seekethfind.alpha.util.StringManipulation;

public class FirebaseMethod {
    private static final String TAG = "FirebaseMethod";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    //widgets
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private String user_ID;

    private double mPhotoUploadProgress = 0;

    public FirebaseMethod() {
    }

    public FirebaseMethod(Context mContext) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        this.mContext = mContext;

        if(mAuth.getCurrentUser() != null){
            user_ID = mAuth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPhoto(String photoType, final String caption,final int count, final String imgUrl,

                               Bitmap bm){

        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        FilePaths filePaths = new FilePaths();

        //case1) new photo

        if(photoType.equals(mContext.getString(R.string.new_photo))){

            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            final StorageReference storageReference = mStorageReference

                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            if(bm == null){

                bm = ImageManager.getBitmap(imgUrl);

            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;

            uploadTask = storageReference.putBytes(bytes);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        addPhotoToDatabase(caption, downloadUri.toString());

                    } else {
                        // Handle failures
                    }
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override

                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
//
//
//
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
//
//
//
//                    //add the new photo to 'photos' node and 'user_photos' node
//
//                    addPhotoToDatabase(caption, firebaseUrl.toString());

                    //navigate to the main feed so the user can see their photo

                    Intent intent = new Intent(mContext, MainActivity.class);

                    mContext.startActivity(intent);

                }

            }).addOnFailureListener(new OnFailureListener() {

                @Override

                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: Photo upload failed.");

                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();

                }

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                @Override

                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();



                    if(progress - 15 > mPhotoUploadProgress){

                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();

                        mPhotoUploadProgress = progress;

                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");

                }

            });



        }

        //case new profile photo

        else if(photoType.equals(mContext.getString(R.string.profile_photo))){

            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            final StorageReference storageReference = mStorageReference

                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap

            if(bm == null){

                bm = ImageManager.getBitmap(imgUrl);

            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;

            uploadTask = storageReference.putBytes(bytes);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        setProfilePhoto(downloadUri.toString());

                    } else {
                        // Handle failures
                    }
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
//
//
//
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
//
//
//
//                    //add the new photo to 'photos' node and 'user_photos' node
//
//                    setProfilePhoto(firebaseUrl.toString());
//

                    ((AccountSettingActivity)mContext).setViewPager(

                            ((AccountSettingActivity)mContext).pagerAdapter

                                    .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))

                    );

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: Photo upload failed.");

                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();

                }

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if(progress - 15 > mPhotoUploadProgress){

                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();

                        mPhotoUploadProgress = progress;

                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");

                }

            });

        }

    }

    private void setProfilePhoto(String url){

        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);

        mDatabaseReference.child(mContext.getString(R.string.user_account_setting))

                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())

                .child(mContext.getString(R.string.profile_photo))

                .setValue(url);

    }

    private void addPhotoToDatabase(String caption, String url){
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = mDatabaseReference.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);
        //insert into database

        mDatabaseReference.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(photo);

        mDatabaseReference.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
    }

    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }

    public void registerNewEmail(String email, String pass){
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, "Failed to Authentication", Toast.LENGTH_SHORT).show();
                        } else {
                            sendVerification();
                            user_ID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, " onComplete Auth State Changed  " + user_ID);
                        }

                    }
                });
    }

    private void sendVerification(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext, "Receive Email Varification", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext, "Coudn't Send Email Verification Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void addNewUser(String userName, String emailId, String password, String number){

        User_SignUp signUp = new User_SignUp(user_ID,userName,emailId,password,number);

        mDatabaseReference.child(mContext.getString(R.string.user_db))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.user_db_info))
                .setValue(signUp);

        UserAccountSettings userAccountSettings = new UserAccountSettings(
                "",
                "",
                0,
                0,
                0,
                "",
                userName,
                "",
                user_ID
        );

        mDatabaseReference.child(mContext.getString(R.string.user_account_setting))
                .child(mAuth.getCurrentUser().getUid())
                .setValue(userAccountSettings);

    }

    public void updateProfileData(final String displayname, final String website, final String description){

        if(!displayname.equals("")) {
            mDatabaseReference.child(mContext.getString(R.string.user_account_setting))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.user_account_feild_displayname))
                    .setValue(displayname);
        }
        if(!website.equals("")) {
            mDatabaseReference.child(mContext.getString(R.string.user_account_setting))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.user_account_feild_website))
                    .setValue(website);
        }

        if(!description.equals("")) {
            mDatabaseReference.child(mContext.getString(R.string.user_account_setting))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.user_account_feild_description))
                    .setValue(description);
        }

//        Query mQuery =  mDatabaseReference.child(mContext.getString(R.string.user_account_setting))
//                .child(mAuth.getCurrentUser().getUid());
//        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                UserAccountSettings setting = dataSnapshot.getValue(UserAccountSettings.class);
//                long followers = setting.getFollowers();
//                long following = setting.getFollowing();
//                long posts = setting.getPosts();
//                String profile_photo = setting.getProfile_photo();
//                String username = setting.getUsername();
//                String user_id = setting.getUser_id();
//
//                updateDatabaseProfile(description, displayname, followers, following, posts,profile_photo
//                        ,username, website,user_id);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }


}
