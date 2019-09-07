package com.luxuryshauk.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import com.luxuryshauk.R;

/**
 * Created by User on 6/4/2017.
 */

public class GridImageAdapter extends ArrayAdapter<String>{

    String TAG = "GridImageAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs) {
        super(context, layoutResource, imgURLs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
        mContext = context;
    }

    private static class ViewHolder{
        SquareImageView image;
        ProgressBar mProgressBar;
        ImageView selected;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
        Viewholder build pattern (Similar to recyclerview)
         */
        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);

            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);
            holder.selected = (ImageView) convertView.findViewById(R.id.selected);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }



        String imgURL = getItem(position);
        Log.d(TAG, "getView: image url in gridview : " + imgURL);
        if(imgURL.contains(", "))
        {


            String[] splitted = imgURL.split(", ");
            if(splitted[0].contains("null"))
                imgURL = splitted[1];
            else
                imgURL = splitted[0];
        }

        //Picasso.get().load(mAppend + imgURL).into(holder.image);

        Glide.with(mContext)
                .load(mAppend + imgURL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);


//        ImageLoader imageLoader = ImageLoader.getInstance();
//
//        imageLoader.displayImage(mAppend + imgURL, holder.image, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                if(holder.mProgressBar != null){
//                    holder.mProgressBar.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                if(holder.mProgressBar != null){
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if(holder.mProgressBar != null){
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//                if(holder.mProgressBar != null){
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
//            }
//        });

        return convertView;
    }
}



















