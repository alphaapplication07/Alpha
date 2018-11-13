package com.seekethfind.alpha.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seekethfind.alpha.R;
import com.seekethfind.alpha.model.UserAccountSettings;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder>{
    private static final String TAG = "LikeAdapter";

    private Context mContext;
    private List<UserAccountSettings> mListUser;

    public LikeAdapter() {
    }

    public LikeAdapter(Context mContext, List<UserAccountSettings> mListUser) {
        this.mContext = mContext;
        this.mListUser = mListUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.center_like_custom,null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserAccountSettings setting = mListUser.get(position);
         holder.userName.setText(setting.getDisplay_name());
         holder.mLike.setText(" Liked your Photo...");
        Picasso.with(mContext).load(setting.getProfile_photo()).into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return mListUser.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView userImage;
        TextView userName,mLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.username);
            mLike = itemView.findViewById(R.id.like);

        }
    }

}
