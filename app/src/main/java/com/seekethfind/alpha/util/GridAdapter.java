package com.seekethfind.alpha.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.seekethfind.alpha.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private static final String TAG = "GridAdapter";

    private Context mContext;
//    private int[] Images;
    private ArrayList<String> Images;

    public GridAdapter() {
    }

    public GridAdapter(Context mContext, ArrayList<String> images) {
        this.mContext = mContext;
        Images = images;
    }

    @Override
    public int getCount() {
        return Images.size();
    }

    @Override
    public Object getItem(int i) {
        return Images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            //INFLATE CUSTOM LAYOUT
            view= LayoutInflater.from(mContext).inflate(R.layout.grid_image_card,viewGroup,false);
            ImageView image = view.findViewById(R.id.grid_image);
            final ProgressBar bufferProgress = view.findViewById(R.id.bufferProgress);
            bufferProgress.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(Images.get(i)).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    bufferProgress.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
        }

        return view;

    }
}
