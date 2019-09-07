package com.luxuryshauk.Utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.luxuryshauk.R;
import com.squareup.picasso.Picasso;

public class FullScreenImage extends AppCompatActivity {
    String TAG = "Full Screen";

    public ImageView fullImage;
    String[] urls;
    public Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_image);
        fullImage = findViewById(R.id.fullscreen);
        bundle = getIntent().getExtras();
        String img = bundle.getString("img_url");
        if(img.contains(", "))
        {
            urls = img.split(", ");
            Picasso.get().load(urls[1]).into(fullImage);
        }else
        {
            Picasso.get().load(img).into(fullImage);
        }

    }
}
