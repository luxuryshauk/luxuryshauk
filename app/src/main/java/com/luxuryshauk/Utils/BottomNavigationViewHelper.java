package com.luxuryshauk.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.allenliu.badgeview.BadgeFactory;
import com.allenliu.badgeview.BadgeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.models.notification;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import com.luxuryshauk.Home.HomeActivity;
import com.luxuryshauk.Likes.LikesActivity;
import com.luxuryshauk.Profile.ProfileActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.Search.SearchActivity;
import com.luxuryshauk.Share.ShareActivity;

/**
 * Created by User on 5/28/2017.
 */

public class BottomNavigationViewHelper {

    public static boolean somethingnew = false;

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
//        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
//        bottomNavigationViewEx.enableAnimation(false);
//        bottomNavigationViewEx.enableItemShiftingMode(false);
//        bottomNavigationViewEx.enableShiftingMode(false);
//        bottomNavigationViewEx.setTextVisibility(false);
    }


    public static void enableNavigation(final Context context, final Activity callingActivity, final BottomNavigationViewEx view){
        try {
            final MenuItem heartbtn = view.getMenu().getItem(3);
            heartbtn.setIcon(R.drawable.ic_alert);


            try {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        while (!isInterrupted()) {
                            try {
                                Thread.sleep(1000);

                                callingActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            DatabaseReference dref = FirebaseDatabase.getInstance().getReference()
                                                    .child("notification").child(currentuid);

                                            dref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        notification N = ds.getValue(notification.class);
                                                        if (N.getSeen().equals("unseen")) {
                                                            //addBadgeViewAt(3,"1", view,context);
                                                            heartbtn.setIcon(R.mipmap.ic_alert_not);
                                                        } else {
                                                            //addBadgeViewAt(3,"1", view,context);
                                                            heartbtn.setIcon(R.drawable.ic_alert);

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } catch (Exception e) {
                                        }

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };

                t.start();
            } catch (Exception e) {
            }


            view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.ic_house:
                            Activity activity = (Activity) context;
                            activity.finish();
                            Intent intent1 = new Intent(context, HomeActivity.class);//ACTIVITY_NUM = 0
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                            context.startActivity(intent1);
                            callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            break;

                        case R.id.ic_search:
                            Intent intent2 = new Intent(context, SearchActivity.class);//ACTIVITY_NUM = 1
                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent2);
                            callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            break;

                        case R.id.ic_circle:
                            Intent intent3 = new Intent(context, ShareActivity.class);//ACTIVITY_NUM = 2
                            intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent3);
                            callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            break;

                        case R.id.ic_alert:
                            Intent intent4 = new Intent(context, LikesActivity.class);//ACTIVITY_NUM = 3
                            intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent4);
                            callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            break;

                        case R.id.ic_android:

                            Intent intent5 = new Intent(context, ProfileActivity.class);//ACTIVITY_NUM = 4
                            intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent5);
                            callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            break;
                    }


                    return false;
                }
            });
        }catch (Exception e){}
    }
    private static BadgeView addBadgeViewAt(int position, String text , BottomNavigationViewEx view, Context context) {
        ImageView icon = view.getIconAt(3);

        return BadgeFactory.create(context)
                .setTextColor(Color.WHITE)
                .setWidthAndHeight(15, 15)
                .setBadgeBackground(Color.RED)
                .setTextSize(7)
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setBadgeCount(text)
                .setShape(BadgeView.SHAPE_CIRCLE)
                .setMargin(0, 0, 0, 0)
                .bind(icon);
    }

}
