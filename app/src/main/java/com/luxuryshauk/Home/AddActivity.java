package com.luxuryshauk.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.luxuryshauk.models.Photo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";

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
    public long price;
    public Context mContext;
    public String[] imgurls;
    public String[] modifiedArray;
    public String img_url;
    public ImageView viewImage;
    Photo mPhoto;
    Bundle b;

    public AddActivity() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    @Override
    public void onBackPressed() {
//        finish();
//        Intent intent = new Intent(new Intent(AddActivity.this,HomeActivity.class));
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        Intent mStartActivity = new Intent(AddActivity.this, HomeActivity.class);
//        int mPendingIntentId = 123456;
//        PendingIntent mPendingIntent = PendingIntent.getActivity(AddActivity.this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager mgr = (AlarmManager)AddActivity.this.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1, mPendingIntent);
//        System.exit(0);

//        Intent i = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        System.exit(0);
//
//        startActivity(i);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_my_store);
        mFirebaseMethods = new FirebaseMethods(AddActivity.this);
        mCaption = (EditText) findViewById(R.id.caption) ;
        mPrice = (EditText) findViewById(R.id.price);
        viewImage = findViewById(R.id.imageShare);
        b = getIntent().getExtras();

        mPhoto = b.getParcelable("photo");
        FirebaseDatabase.getInstance().getReference().child("user_photos").child(mPhoto.getUser_id()).child(mPhoto.getPhoto_id()).child("price").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                price = dataSnapshot.getValue(long.class);
                mPrice.setText(Long.toString(price));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "onCreate: add to store price = " + price );

        Log.d(TAG, "onClick: photo model in add activity " + mPhoto.toString());
        mCaption.setText(mPhoto.getCaption());
        Log.d(TAG, "onCreate: add to store img type = " + mPhoto.getType());
        FirebaseDatabase.getInstance().getReference().child("user_photos").child(mPhoto.getUser_id()).child(mPhoto.getPhoto_id()).child("type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int type = dataSnapshot.getValue(int.class);
                if(type==2)
                {
                    imgurls = mPhoto.getImage_path().split(", ");
                    for(String s : imgurls)
                        Log.d(TAG, "onCreate: add to store img urls =" + s);
                    modifiedArray = Arrays.copyOfRange(imgurls, 1, imgurls.length);
                    img_url = modifiedArray[0];
                }else{
                    img_url = mPhoto.getImage_path();
                }

                Log.d(TAG, "onCreate: add to store img url =" + img_url);
                Picasso.get().load(img_url).into(viewImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Photo photo = b.getBundle("photo");
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
                startActivity(new Intent(AddActivity.this,HomeActivity.class));
                finish();
            }
        });



        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final long finalprice = Long.valueOf(mPrice.getText().toString());
                final String key = FirebaseDatabase.getInstance().getReference().push().getKey();
                final DatabaseReference mref = FirebaseDatabase.getInstance().getReference();

                FirebaseDatabase.getInstance().getReference().child("photos").child(mPhoto.getPhoto_id()).child("type").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int type = dataSnapshot.getValue(int.class);
                        String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        mPhoto.setUser_id(currentuid);
                        mPhoto.setPrice(finalprice);
                        mPhoto.setPhoto_id(key);
                        mPhoto.setDate_created(getTimestamp());
                        mPhoto.setCaption(mCaption.getText().toString());

//                        mref.child("photos").child(key).setValue(mPhoto);
                        mref.child("user_photos").child(currentuid).child(key).setValue(mPhoto);
//                        mref.child("photos").child(key).child("type").setValue(type);
                        mref.child("user_photos").child(currentuid).child(key).child("type").setValue(type);


                        if(type==2)
                        {
                            Log.d(TAG, "onDataChange: final image url in add to my store : " + mPhoto.getImage_path());
                                imgurls = mPhoto.getImage_path().split(", ");
                                for (String s : imgurls)
                                    Log.d(TAG, "onCreate: add to store img urls =" + s);
                                if(imgurls[0].contains("null"))
                                    modifiedArray = Arrays.copyOfRange(imgurls, 1, imgurls.length);
                                else
                                    modifiedArray = Arrays.copyOfRange(imgurls, 0, imgurls.length);
                                if(modifiedArray[0].contains("["))
                                    modifiedArray[0] = modifiedArray[0].replace("[", "");
                                if(modifiedArray[modifiedArray.length - 1].contains("]"))
                                    modifiedArray[modifiedArray.length - 1] = modifiedArray[modifiedArray.length - 1].replace("]", "");
                                String[] finalurllist = new String[modifiedArray.length];
//                                for(String u: modifiedArray)
//                                    fin
                            int count = 1;

                                for (int i = 0; i < modifiedArray.length; i++) {

                                    if(!modifiedArray[i].equals("null")) {
//                                        mref.child("photos").child(key)
//                                                .child("image_path").child(String.valueOf(count)).setValue(modifiedArray[i]);
                                        mref.child("user_photos").child(currentuid).child(key)
                                                .child("image_path").child(String.valueOf(count)).setValue(modifiedArray[i]);
                                        count++;
                                    }
                                }

                            img_url = modifiedArray[0];
                        }else{
                            img_url = mPhoto.getImage_path();
//                            mref.child("photos").child(key)
//                                    .child("image_path").setValue(img_url);
                            mref.child("user_photos").child(currentuid).child(key)
                                    .child("image_path").setValue(img_url);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                Toast.makeText(AddActivity.this,"Successfully Added to your Store",Toast.LENGTH_LONG).show();
                finish();
            }
        });

        setImage();
    }



    public Uri getLocalBitmapUri(Bitmap bmp)
    {
        Uri bmpUri = null;
        try{
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"share_image" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG,90,out);
            out.close();
            bmpUri = Uri.fromFile(file);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public float getCommision()
    {

        mPrice = (EditText)findViewById(R.id.price);
        mPrice_com = (TextView) findViewById(R.id.price_com);
        mPrice_earn = (TextView) findViewById(R.id.price_earn);
        double price=0,price_com=0,price_earn=0;
        if(TextUtils.isEmpty(mPrice.getText()))
        {
            price = 0f;
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
        final ImageView image = (ImageView) findViewById(R.id.imageShare);
        Bundle b = intent.getExtras();
        Picasso.get().load(img_url).into(image);//new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                Picasso.get().load(getLocalBitmapUri(bitmap)).into(image);
//            }
//
//            @Override
//            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//            }
//        });

//        if(intent.hasExtra("img_url")){
//            imgUrl = intent.getStringExtra("img_url");
//            Log.d(TAG, "setImage: got new image url: " + imgUrl);
//            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
//        }
//        else if(intent.hasExtra("img_url")){
//            bitmap = (Bitmap) intent.getParcelableExtra("img_url");
//            Log.d(TAG, "setImage: got new bitmap");
//            image.setImageBitmap(bitmap);
//        }
//        Bundle b = intent.getExtras();
//        Picasso.get().load(b.getString("img_url")).into(image);
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
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
}
