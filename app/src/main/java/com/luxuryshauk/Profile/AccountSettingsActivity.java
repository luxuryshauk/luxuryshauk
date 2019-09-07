package com.luxuryshauk.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.Utils.ManageTopSellers;
import com.luxuryshauk.Utils.MyWallet;
import com.luxuryshauk.Utils.buy;
import com.luxuryshauk.Utils.help;
import com.luxuryshauk.Utils.sell;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import com.luxuryshauk.R;
import com.luxuryshauk.Utils.BottomNavigationViewHelper;
import com.luxuryshauk.Utils.FirebaseMethods;
import com.luxuryshauk.Utils.SectionsStatePagerAdapter;

/**
 * Created by User on 6/4/2017.
 */

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context mContext;
    public SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    public static Context contextOfApplication;
    int RESULT_LOAD_IMAGE = 1;
    public static Context applicationContext;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    TextView countdownTimer;
    boolean email_sent = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        mContext = AccountSettingsActivity.this;
        Log.d(TAG, "onCreate: started.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);
        contextOfApplication = getApplicationContext();
        setupSettingsList();
        setupBottomNavigationView();
        setupFragments();
        getIncomingIntent();
        String c_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //setup the backarrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        Button buy_btn = (Button) findViewById(R.id.order_buy_button);
        Button sell_btn = (Button) findViewById(R.id.order_sold_button);
        final ImageView unseensold = (ImageView)findViewById(R.id.unseen_sell);
        final ImageView unseenbuy = (ImageView)findViewById(R.id.unseen_buy);
        final ImageView track_sell = (ImageView)findViewById(R.id.track_sell_indicator);
        unseenbuy.setVisibility(View.INVISIBLE);
        unseensold.setVisibility(View.INVISIBLE);
        track_sell.setVisibility(View.INVISIBLE);
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("buy").child(c_uid);
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String seen = ds.child("isseen").getValue(String.class);
                    if(seen.equals("unseen"))
                    {
                        unseenbuy.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference dref1 = FirebaseDatabase.getInstance().getReference().child("sell").child(c_uid);
        dref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String seen = ds.child("isseen").getValue(String.class);
                    if(seen.equals("unseen"))
                    {
                        unseensold.setVisibility(View.VISIBLE);
                        Animation animation = new AlphaAnimation(1, 0);
                        animation.setDuration(1000);
                        animation.setInterpolator(new LinearInterpolator());
                        animation.setRepeatCount(Animation.INFINITE);
                        animation.setRepeatMode(Animation.REVERSE);
                        track_sell.startAnimation(animation);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setallseenbuy();
                startActivity(new Intent(mContext, buy.class));
                //finish();

            }
        });
        sell_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unseensold.setVisibility(View.INVISIBLE);
                setallseensell();
                startActivity(new Intent(mContext, sell.class));
                //finish();
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");
                finish();
            }
        });
    }



    private void getIncomingIntent(){
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))){

            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
            Log.d(TAG, "getIncomingIntent: New incoming imgUrl");
            if(intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))){

                if(intent.hasExtra(getString(R.string.selected_image))){
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra(getString(R.string.selected_image)), null,0,1);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            null,(Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)),0,1);
                }

            }

        }

        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }



    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    private void setupFragments(){
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        String currentuid = FirebaseAuth.getInstance().getUid();
        if(currentuid.equals("sTZipFqqKFfT9y44QRNWmbPZkrn1"))
        {
            pagerAdapter.addFragment(new EditProfileFragment(), "Edit Profile"); //fragment 0
            pagerAdapter.addFragment(new AllOrders(), "Manage orders ");
            pagerAdapter.addFragment(new NoticeFragment(), "Important Notice");
            pagerAdapter.addFragment(new ManageTopSellers(), "Manage Top Sellers");
        }else if(currentuid.equals("3JwzJu0FDmRH1Mvno47lAx14oje2"))
        {
            pagerAdapter.addFragment(new EditProfileFragment(), "Edit Profile"); //fragment 0
            pagerAdapter.addFragment(new HandleRequests(), "Handle Requests"); //fragment 0
        }
        else{

            pagerAdapter.addFragment(new EditProfileFragment(), "Edit Profile"); //fragment 0
            pagerAdapter.addFragment(new MyWallet(), "My Wallet"); //fragment 1
            pagerAdapter.addFragment(new help(), "Help"); // Fragment 2

        }
        pagerAdapter.addFragment(new SignOutFragment(), "Sign Out"); //fragment 3


    }



    public void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigating to fragment #: " + fragmentNumber);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }
    @Override
    public void onBackPressed() {

        if (mRelativeLayout.getVisibility() == View.GONE) {
            mRelativeLayout.setVisibility(View.VISIBLE);
        }else{
            finish();
        }

    }

    private void setupSettingsList(){
        Log.d(TAG, "setupSettingsList: initializing 'Account Settings' list.");
        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        String currentuid = FirebaseAuth.getInstance().getUid();
        if(currentuid.equals("sTZipFqqKFfT9y44QRNWmbPZkrn1")) {
            options.add("Edit Profile");
            options.add("Manage orders ");
            options.add("Important Notice");
            options.add("Manage Top Sellers");
        }
        else if(currentuid.equals("3JwzJu0FDmRH1Mvno47lAx14oje2"))
        {
            options.add("Edit Profile");
            options.add("Handle Requests");
        }
        else {
            options.add(getString(R.string.edit_profile_fragment)); //fragment 0
            options.add("My Wallet");//fragment 1
            options.add("Help : chat with LuxuryShauk Executive"); //fragement 2
        }
        options.add(getString(R.string.sign_out_fragment)); //fragement 3

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragment#: " + position);
                setViewPager(position);
            }
        });

    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

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

    void setallseenbuy(){
        final String c_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference dref1 = FirebaseDatabase.getInstance().getReference().child("buy").child(c_uid);
        dref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    //String ds.child("isseen").getValue(String.class);
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("buy")
                            .child(c_uid)
                            .child(ds.getKey())
                            .child("isseen")
                            .setValue("seen");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void setallseensell(){
        final String c_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference dref1 = FirebaseDatabase.getInstance().getReference().child("sell").child(c_uid);
        dref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    //String ds.child("isseen").getValue(String.class);
//                    FirebaseDatabase.getInstance()
//                            .getReference()
//                            .child("sell")
//                            .child(c_uid)
//                            .child(ds.getKey())
//                            .child("isseen")
//                            .setValue("seen");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    @Override
//    public void onBackPressed() {
//
//        int count = getSupportFragmentManager().getBackStackEntryCount();
//
//        if (count == 0) {
//            super.onBackPressed();
//            //additional code
//
//        } else {
//            getSupportFragmentManager().popBackStack();
//        }
//
//    }


}
















