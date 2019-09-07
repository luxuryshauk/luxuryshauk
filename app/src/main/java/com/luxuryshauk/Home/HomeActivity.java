package com.luxuryshauk.Home;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.Share.NextActivity;
import com.luxuryshauk.Utils.FullScreenImage;
import com.luxuryshauk.Utils.Mail;
import com.luxuryshauk.Utils.Permissions;
import com.luxuryshauk.Utils.PlaceOrder;
import com.luxuryshauk.Utils.SendEmailAsyncTask;
import com.luxuryshauk.Utils.ViewMultiplePostFragment;
import com.luxuryshauk.models.Chat;
import com.luxuryshauk.models.User;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.luxuryshauk.Login.LoginActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.Utils.BottomNavigationViewHelper;
import com.luxuryshauk.Utils.MainFeedListAdapter;
import com.luxuryshauk.Utils.SectionsPagerAdapter;
import com.luxuryshauk.Utils.UniversalImageLoader;
import com.luxuryshauk.Utils.ViewCommentsFragment;
import com.luxuryshauk.models.Photo;
import com.luxuryshauk.opengl.AddToStoryDialog;
import com.luxuryshauk.opengl.NewStoryActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity implements
        MainFeedListAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            fragment.displayMorePhotos();
        }
    }


    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;
    private static final int RESULT_ADD_NEW_STORY = 7891;
    private final static int CAMERA_RQ = 6969;
    private static final int REQUEST_ADD_NEW_STORY = 8719;
    private static final int CAMERA_PHOTO = 111;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int CAMERA_REQUEST_CODE = 5;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    boolean canopencam;
    public String capturedImageUri;

    private Context mContext = HomeActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    private RelativeLayout mHeader;
    ImageView isseen_i;

    boolean cam;



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: ");
        ImageView camera = findViewById(R.id.camera);
        ImageView message = findViewById(R.id.message);
        ImageView instaasell = findViewById(R.id.instaasell);
        final WebView important = findViewById(R.id.important);
//        sendEmail("Test Track Upload Buyer", "Tracking update order #Test", "yammaar9@gmail.com");
//        sendEmail("Test Track Upload Admin", "Tracking update order #Test", "support@collectmoney.in");
//        sendEmail("Test Track Upload Seller", "Tracking update order #Test", "luxuryshaukapp@gmail.com");

        FirebaseDatabase.getInstance().getReference().child("important").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String importantText = dataSnapshot.getValue(String.class);
                String summary = "<html><FONT size='2' color='#a80000' FACE='courier'><marquee behavior='scroll' direction='left' scrollamount=2>"
                        + importantText + "</marquee></FONT></html>";
                important.loadData(summary, "text/html", "utf-8");
//                important.setText(importantText);
//                important.setSelected(true);
//                TranslateAnimation animation = new TranslateAnimation(0, 100, 0, 0); // new TranslateAnimation (float fromXDelta,float toXDelta, float fromYDelta, float toYDelta)
//                animation.setDuration(8500); // animation duration
//                animation.setRepeatCount(100); // animation repeat count
//                animation.setFillAfter(false);
//                animation.setRepeatMode(2);
//                important.setAnimation(animation);
//                important.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.rtl));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        isseen_i = findViewById(R.id.isseen_i);
        //sendMessage();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        instaasell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });



        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
            }
        });

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
//            setupViewPager();
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }

        try {
            String extras = getIntent().getStringExtra("from_chat");
            Log.d(TAG, "onCreate: back from chat extras = " + extras);
            if (extras.equals("1")) {
                Log.d(TAG, "onCreate: back from chat extras = set on 2");
                mViewPager.setCurrentItem(2);
            }
        }catch (Exception e){}

        Log.d(TAG, "onCreate: starting.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);
        //mHeader = (RelativeLayout)findViewById(R.id.header_layout);
        //mHeader.bringToFront();
        //startActivity(new Intent(HomeActivity.this, buy.class));


        setupFirebaseAuth();
        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();



        //chat notifier

        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    FirebaseDatabase.getInstance().getReference("Chats").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Chat chat = snapshot.getValue(Chat.class);
                                                try {
                                                    if (chat.getReceiver().equals(currentUid) && !chat.isIsseen()) {
                                                        isseen_i.setVisibility(View.VISIBLE);
                                                    } else {
                                                        isseen_i.setVisibility(View.INVISIBLE);
                                                    }
                                                } catch (Exception e) {

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }catch (Exception e){}
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();



    }
    FragmentManager mFragmentManager = getSupportFragmentManager();

    public void multi_show(String image_id, String[] img_url, String type, User u)
    {
        String none="none";
        Bundle bundle = new Bundle();
        bundle.putString("path",none);
        bundle.putStringArray("url",img_url);
        bundle.putString("id",image_id);
        bundle.putString("type",type);
        bundle.putParcelable("user",u);
        ViewMultiplePostFragment view_post = new ViewMultiplePostFragment();
        view_post.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, view_post).addToBackStack("ViewMultplePostFragment").commit();
    }

    public void email(String photo_id, String username)
    {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","Luxuryshaukapp@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Post/Account report");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Post id : " + photo_id + " \nUsername : " + username + "\n\n\nYour Comments : ");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }
    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStackImmediate();
            showLayout();
        }
        else super.onBackPressed();
    }

    public void share(final String bmpUri,final String capton)
    {
        Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,bmpUri + "   " +
                        "Caption : " +capton);
                //shareIntent.putExtra(Intent.EXTRA_SUBJECT,capton);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share Image"));


//        Picasso.get().load(bmpUri).into(new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT,capton);
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                shareIntent.setType("image/*");
//                startActivity(Intent.createChooser(shareIntent, "Share Image"));
//            }
//            @Override
//            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//            }
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//            }
//        });
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


//    public void buy_now(String id, String seller, long price, String curr_user, String seller_uid, String seller_id, String imgurl, String caption)
//    {
//        Bundle bundle = new Bundle();
//        bundle.putString("seller_username",seller);
//        bundle.putString("price",String.valueOf(price));
//        bundle.putString("current_user",curr_user);
//        bundle.putString("seller_uid",seller_id);
//        bundle.putString("img_url",imgurl);
//        bundle.putString("caption",caption);
//        bundle.putString("id",id);
//        Intent buy = new Intent(HomeActivity.this, PlaceOrder.class);
//        buy.putExtras(bundle);
//        startActivity(buy);
//    }

    public void buy_now(String photo_id, String buyer)
    {
        Bundle b = new Bundle();
        b.putString("photoid",photo_id);
        b.putString("buyer",buyer);
        Intent buy = new Intent(HomeActivity.this, PlaceOrder.class);
        buy.putExtras(b);
        startActivity(buy);
    }

    public void add_to_my_store(long price,String imgurl,String caption)
    {
        Bundle bundle = new Bundle();
        bundle.putString("price",String.valueOf(price));
        bundle.putString("img_url",imgurl);
        bundle.putString("caption",caption);
        Intent add = new Intent(HomeActivity.this, AddActivity.class);
        add.putExtras(bundle);
        startActivity(add);

    }
    public void full_screen(String url)
    {
        Bundle bundle = new Bundle();
        bundle.putString("img_url",url);
        Intent add = new Intent(HomeActivity.this, FullScreenImage.class);
        add.putExtras(bundle);
        startActivity(add);
    }


    public Uri getLocalBitmapUri(ImageView imageView)
    {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if(drawable instanceof BitmapDrawable)
        {
            bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }
        else{
            return null;
        }
        Uri bmpUri = null;

        try{
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
            ),"share_image_"+System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90,out);
            out.close();
            bmpUri = Uri.fromFile(file);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return bmpUri;

    }

    public void openNewStoryActivity(){
        Intent intent = new Intent(this, NewStoryActivity.class);
        startActivityForResult(intent, REQUEST_ADD_NEW_STORY);
    }

    public void showAddToStoryDialog(){
        Log.d(TAG, "showAddToStoryDialog: showing add to story dialog.");
        AddToStoryDialog dialog = new AddToStoryDialog();
        dialog.show(getFragmentManager(), getString(R.string.dialog_add_to_story));
    }


    public void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }


    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }






    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */
    String mCameraFileName;
    ContentValues values;
    Uri imageUri;

    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); //index 0
        adapter.addFragment(new HomeFragment()); //index 1
        adapter.addFragment(new MessagesFragment()); //index 2
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //Toast.makeText(HomeActivity.this, "Position = " + i, Toast.LENGTH_SHORT).show();
                if(i==0)
                {

//                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File image = new File(Environment.getExternalStorageDirectory(),"TeamImage.jpg");
//                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
//
//                    startActivityForResult(camera, CAMERA_REQUEST_CODE);
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    if(canopencam) {
//                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
//                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
//                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                        StrictMode.setVmPolicy(builder.build());
//                        Intent intent = new Intent();
//                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                        Date date = new Date();
//                        DateFormat df = new SimpleDateFormat("-mm-ss");
//
//                        String newPicFile = df.format(date) + ".jpg";
//                        String outPath = "/sdcard/" + newPicFile;
//                        File outFile = new File(outPath);
//
//                        mCameraFileName = outFile.toString();
//                        Uri outuri = Uri.fromFile(outFile);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
//                        startActivityForResult(intent, 2);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    Log.v(TAG,"Permission is granted");
                                    values = new ContentValues();
                                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                                    imageUri = getContentResolver().insert(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                                } else {

                                    Log.v(TAG,"Permission is revoked");
                                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                                }
                            }
                            else { //permission is automatically granted on sdk<23 upon installation
                                Log.v(TAG,"Permission is granted");
                                Log.v(TAG,"Permission is granted");
                                values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                                imageUri = getContentResolver().insert(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent, CAMERA_REQUEST_CODE);
                            }


                        canopencam = false;
                    }
                }
                else if(i==1)
                {
                    canopencam = true;
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
    Bitmap thumbnail;
    String imageurl;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: camera request code = " + requestCode);
        //if(resultCode ==1 ) {
        try {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Log.d(TAG, "onActivityResult: done taking a photo.");
                Log.d(TAG, "onActivityResult: attempting to navigate to final share screen.");
//                Log.d(TAG, "onActivityResult: data = " + data.toString());

//                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                String img = data.toString();
//                Uri imageuri = data.getData();
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageuri);
                thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
//                imgView.setImageBitmap(thumbnail);
                imageurl = getRealPathFromURI(imageUri);

//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                final Bitmap bitmap = BitmapFactory.decodeFile(capturedImageUri, options);
//                Log.d(TAG, "onActivityResult: img = " + img);
//                try {
//                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                    Intent intent = new Intent(HomeActivity.this, NextActivity.class);
//                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
//                    intent.putExtra("selected_image", imageuri);
                intent.putExtra("selected_image", imageurl);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("type", 1);
                    startActivity(intent);
//                } catch (NullPointerException e) {
//                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
//                }

            }
        }catch (IOException e){}
        //}
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     * @param user
     */
     private void checkCurrentUser(FirebaseUser user){
         Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

         if(user == null){
             Intent intent = new Intent(mContext, LoginActivity.class);
             startActivity(intent);
         }
     }
    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

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
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(mAuth.getCurrentUser());
        try {
            String extras = getIntent().getStringExtra("from_chat");
            Log.d(TAG, "onCreate: back from chat extras = " + extras);
            if (extras.equals("1")) {
                Log.d(TAG, "onCreate: back from chat extras = set on 2");
                mViewPager.setCurrentItem(2);
            }
        }catch (Exception e){}
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


//    public boolean checkPermissions(String permission){
//        Log.d(TAG, "checkPermissions: checking permission: " + permission);
//
//        int permissionRequest = ActivityCompat.checkSelfPermission(HomeActivity.this, permission);
//
//        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
//            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
//            return false;
//        }
//        else{
//            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
//            return true;
//        }
//    }

    public void sendEmail(String body, String subject , String rec)
    {
        String[] recipients = {rec};
        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.activity = this;
        email.m = new Mail("luxuryshaukapp@gmail.com", "LuxuryShawk123");
        email.m.set_from("luxuryshaukapp@gmail.com");
        email.m.setBody(body);
        email.m.set_to(recipients);
        email.m.set_subject(subject);
        email.execute();
    }


    public void sendMessage(String body) {
        String[] recipients = { "luxuryshaukapp@gmail.com" };
        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.activity = this;
        email.m = new Mail("luxuryshaukapp@gmail.com", "LuxuryShawk123");
        email.m.set_from("luxuryshaukapp@gmail.com");
        email.m.setBody(body);
        email.m.set_to(recipients);
        email.m.set_subject("Post Reported!!!");
        email.execute();
    }

    public void displayMessage(String message) {
        try {
            Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }catch (Exception e){}
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if (requestCode == CAMERA_PHOTO && resultCode == Activity.RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//
//            Uri tempUri = getImageUri(getApplicationContext(), photo);
//
//            File finalFile = new File(getRealPathFromURI(tempUri));
//
//            System.out.println("Camera file Uri = " + tempUri.toString());
//        }
//    }
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                HomeActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(HomeActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }



}


