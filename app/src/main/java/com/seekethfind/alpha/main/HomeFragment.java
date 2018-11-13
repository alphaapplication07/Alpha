package com.seekethfind.alpha.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seekethfind.alpha.R;
import com.seekethfind.alpha.model.Comment;
import com.seekethfind.alpha.model.Like;
import com.seekethfind.alpha.model.Photo;
import com.seekethfind.alpha.util.MainfeedListAdapter;
import com.seekethfind.alpha.util.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    //vars

    private ArrayList<Photo> mPhotos;
    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private int recursionIterator = 0;
    private ListView mListView;

    private int mResults;


    private MainfeedListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);

        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();


        getFollowing();
        return view;
    }




    /**

     //     * Retrieve all user id's that current user is following

     //     */

    private void getFollowing() {

        Log.d(TAG, "getFollowing: searching for following");

       // clearAll();
        //also add your own id to the list

        mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getActivity().getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Log.d(TAG, "getFollowing: found user: " + singleSnapshot

                            .child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot

                            .child(getString(R.string.field_user_id)).getValue().toString());

                }

                getPhotos();

//                getMyUserAccountSettings();

//                getFriendsAccountSettings();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void getPhotos(){

        Log.d(TAG, "getPhotos: getting list of photos");



        for(int i = 0; i < mFollowing.size(); i++){

            final int count = i;

            Query query = FirebaseDatabase.getInstance().getReference()

                    .child(getActivity().getString(R.string.dbname_user_photos))

                    .child(mFollowing.get(i))

                    .orderByChild(getString(R.string.field_user_id))

                    .equalTo(mFollowing.get(i))

                    ;

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                        Photo photo = new Photo();

                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();

                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){

                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);

                        }

                        photo.setComments(comments);
                        mPhotos.add(photo);

                    }

                    if(count >= mFollowing.size() - 1){

                        //display the photos

                        displayPhotos();

                    }



                }



                @Override

                public void onCancelled(DatabaseError databaseError) {

                    Log.d(TAG, "onCancelled: query cancelled.");

                }

            });



        }

    }



    private void displayPhotos(){

        mPaginatedPhotos = new ArrayList<>();

        if(mPhotos != null){

            try{

                //sort for newest to oldest

                Collections.sort(mPhotos, new Comparator<Photo>() {

                    public int compare(Photo o1, Photo o2) {

                        return o2.getDate_created().compareTo(o1.getDate_created());

                    }

                });

                //we want to load 10 at a time. So if there is more than 10, just load 10 to start

                int iterations = mPhotos.size();

                if(iterations > 10){

                    iterations = 10;

                }



                mResults = 10;

                for(int i = 0; i < iterations; i++){

                    mPaginatedPhotos.add(mPhotos.get(i));

                    mResults++;

                    Log.d(TAG, "displayPhotos: adding a photo to paginated list: " + mPhotos.get(i).getPhoto_id());

                }



                adapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);

                mListView.setAdapter(adapter);



                // Notify update is done

               // mListView.notifyUpdated();



            }catch (IndexOutOfBoundsException e){

                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );

            }catch (NullPointerException e){

                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );

            }

        }

    }


    public void displayMorePhotos(){

        Log.d(TAG, "displayMorePhotos: displaying more photos");



        try{



            if(mPhotos.size() > mResults && mPhotos.size() > 0){



                int iterations;

                if(mPhotos.size() > (mResults + 10)){

                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");

                    iterations = 10;

                }else{

                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");

                    iterations = mPhotos.size() - mResults;

                }



                //add the new photos to the paginated list

                for(int i = mResults; i < mResults + iterations; i++){

                    mPaginatedPhotos.add(mPhotos.get(i));

                }



                mResults = mResults + iterations;

                adapter.notifyDataSetChanged();

            }

        }catch (IndexOutOfBoundsException e){

            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );

        }catch (NullPointerException e){

            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );

        }

    }

}
