package com.luxuryshauk.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MultiImageViewPager extends PagerAdapter {

    private Context mContext;
    private String[] imagesUrl;

    MultiImageViewPager(Context mContext, String[] imagesUrl)
    {
        this.mContext = mContext;
        this.imagesUrl = imagesUrl;
    }

    @Override
    public int getCount() {
        return imagesUrl.length;
    }



    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        Picasso.get()
                .load(imagesUrl[position]).into(imageView);
//                .fit()
//                .centerCrop()

        container.addView(imageView);
        return imageView;
    }
}
