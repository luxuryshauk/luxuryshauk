package com.luxuryshauk.Share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luxuryshauk.R;
import com.luxuryshauk.Utils.FirebaseMethods;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 7/24/2017.
 */

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private StorageReference mStorageReference;
    private String userID;

    //widgets
    private EditText mCaption;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;
    private EditText mPrice;
    private TextView mPrice_com,mPrice_earn;
    private TextView uploading_t;
    private ProgressBar uploading_p;
    public double price;
    public Context mContext;
    public int type;
    ArrayList<String> img_urls;
    String[] img_urls_arr;
    Bundle bundle;


    public NextActivity() {
        //type = getIntent().getExtras().getInt("type");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = NextActivity.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        bundle = getIntent().getExtras();
        type = bundle.getInt("type");

//        final int type = bundle.getInt("type");
//        ArrayList<String> img_urls = bundle.getStringArrayList("images");
        if(type==2) {
            img_urls = bundle.getStringArrayList("images");
            img_urls_arr = img_urls.toArray(new String[img_urls.size()]);
        }
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        mCaption = (EditText) findViewById(R.id.caption) ;
        uploading_p = findViewById(R.id.uploading_p);
        uploading_t = findViewById(R.id.uploading);

        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCommision();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();


        setupFirebaseAuth();

        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });


        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mCaption.getText().toString().isEmpty() && !mPrice.getText().toString().isEmpty()) {
                    uploading_p.setVisibility(View.VISIBLE);
                    uploading_t.setVisibility(View.VISIBLE);
                    if(type==1)
                    {
                        mPrice = (EditText)findViewById(R.id.price);
                        final float price = Float.parseFloat(mPrice.getText().toString());
                        Log.d(TAG, "onClick: navigating to the final share screen.");
                        //upload the image to firebase
                        Toast.makeText(NextActivity.this, "Uploading..", Toast.LENGTH_SHORT).show();
                        String caption = mCaption.getText().toString();
                            if (intent.hasExtra(getString(R.string.selected_image))) {
                                imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                                mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl, null, (float) price, type);
                            } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                                bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                                mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null, bitmap, (float) price, type);
                            }

                    }
                    else if(type==2)
                    {

                        mPrice = (EditText)findViewById(R.id.price);
                        final float price = Float.parseFloat(mPrice.getText().toString());
                        Log.d(TAG, "onClick: navigating to the final share screen.");
                        //upload the image to firebase
                        Toast.makeText(NextActivity.this, "Uploading..", Toast.LENGTH_SHORT).show();
                        String caption = mCaption.getText().toString();

                        if(intent.hasExtra("images")){
                            mFirebaseMethods.uploadNewPhotos(getString(R.string.new_photo), caption, imageCount, img_urls_arr,null,(float)price,type);
                        }


                    }
                }else
                {
                    Toast.makeText(mContext, "Caption and Price should not be empty", Toast.LENGTH_SHORT).show();
                }

//                finish();
            }

        });
        setImage();

    }
    public void finishA()
    {
        finish();
    }

    public float getCommision()
    {

        mPrice = (EditText)findViewById(R.id.price);
        mPrice_com = (TextView) findViewById(R.id.price_com);
        mPrice_earn = (TextView) findViewById(R.id.price_earn);
        double price=0,price_com=0,price_earn=0;
        if(TextUtils.isEmpty(mPrice.getText()))
        {
            price = 0;
        }
        else
        {

            price = Double.parseDouble(mPrice.getText().toString());
            price_com = price * .05;
            price_earn = price - price_com;
        }

        int price_com_round = (int)Math.ceil(price_com);
        int price_ear_round = (int)Math.floor(price_earn);

        mPrice_com.setText("LuxuryShauk Charge : (-) ₹ "+Integer.toString(price_com_round));
        mPrice_earn.setText("You Earn :              (+) ₹ "+Integer.toString(price_ear_round));
        return (float)price;
    }

    private void someMethod(){
        /*
            Step 1)
            Create a data model for Photos

            Step 2)
            Add properties to the Photo Objects (caption, date, imageUrl, photo_id, tags, user_id)

            Step 3)
            Count the number of photos that the user already has.

            Step 4)
            a) Upload the photo to Firebase Storage
            b) insert into 'photos' node
            c) insert into 'user_photos' node

         */

    }


    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage(){
        intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);
//        if (intent.hasExtra(getString(R.string.selected_bitmap))) {
//            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
//            Log.d(TAG, "setImage: got new bitmap");
//            image.setImageBitmap(bitmap);
//        }
        image = (ImageView) findViewById(R.id.imageShare);


        if(type==1) {


            if (intent.hasExtra(getString(R.string.selected_image))) {
                imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                Log.d(TAG, "setImage: got new image url: " + imgUrl);
            File imgFile = new File(imgUrl);

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());


                image.setImageBitmap(myBitmap);

            }
//                Picasso.get().load(imgUrl).into(image);
//                UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
            } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                Log.d(TAG, "setImage: got new bitmap");
                image.setImageBitmap(bitmap);
            }
        }else if(type==2) {
//            image = (ImageView) findViewById(R.id.imageShare);

//            if (img_urls.size()!=0) {
            //imgUrl = intent.getStringExtra("images");
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            img_urls = bundle.getStringArrayList("images");
//                img_urls_arr = img_urls.toArray(new String[img_urls.size()]);
//                Picasso.get().load(img_urls_arr[0]).into(image);
//            UniversalImageLoader.setImage(img_urls.get(0), image, null, mAppend);
            File imgFile = new File(img_urls.get(0));

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                image.setImageBitmap(myBitmap);

            }
//            Picasso.get().load("file:/"+img_urls.get(0)).into(image);
//            } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
//                bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
//                Log.d(TAG, "setImage: got new bitmap");
//                image.setImageBitmap(bitmap);
//            }
//        }
        }
    }

     /*
     ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
