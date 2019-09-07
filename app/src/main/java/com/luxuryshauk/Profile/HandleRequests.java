package com.luxuryshauk.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.Home.HomeActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.models.User;
import com.luxuryshauk.models.order_sell;
import com.luxuryshauk.models.track;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HandleRequests extends Fragment {

    public static String TAG = "Handle Request";

    boolean email_sent = false;
    TextView msg;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_handle_requests, container, false);

        msg = (TextView) view.findViewById(R.id.track_msg);
        msg.setText("Connecting to Server");

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                email_sent = false;
                //final DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("track");
                final DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("sell");
                t_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            for(DataSnapshot ads : ds.getChildren()) {
                                msg.setText("Handling Requests....");

                                final order_sell Order = ads.getValue(order_sell.class);
//                                Log.d(TAG, "onDataChange: ds.key = " + ds.getKey() + " ads.key = " + ads.getKey());
                                track Track = ads.child("track").getValue(track.class);
//                                Log.d(TAG, "onDataChange: Tracking details for : " + ads.getKey() + " is : " + Track.toString());
                                if (Order.getStatus() != 4) {
                                    if (Track.getStatus().equals("0")) {
                                        String timestampDifference = getTimestampDifference(Order);
                                        if (Integer.valueOf(timestampDifference) < 0) {
                                            FirebaseDatabase.getInstance().getReference().child("users").child(ds.getKey()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (!email_sent) {

                                                        final User user = dataSnapshot.getValue(User.class);
                                                        HomeActivity homeActivity = new HomeActivity();
                                                        String body = "Your Order : #" + Order.getOrder_no() + "" +
                                                                "\nOrder Name : " + Order.getCaption() + " has been cancelled, this could be due to one of the following reasons : " +
                                                                "\n\t1. Due to late tracking detail upload." +
                                                                "\n\t2. Due to non availability of product" +
                                                                "\n\t3. Due to any issue in transit." +
                                                                "\n\t4. Due to parcel lost." +
                                                                "\n\t5. Due to cancelled by customer on complaint." +
                                                                "\n\t6. Due to fraud or any unethical activity." +
                                                                "\n\t7. Cancelled by LuxuryShauk executive due to safety and security reason." +
                                                                "\n\nYou can contact to the seller Email : " + user.getEmail() + " Phone : " + user.getPhone_number() + "\n" +
                                                                "\n\nThank You, \nLuxuryShauk Team";
                                                        String body1 = "Your Order : #" + Order.getOrder_no() + "" +
                                                                "\nOrder Name : " + Order.getCaption() + " has been cancelled, this could be due to one of the following reasons : " +
                                                                "\n\t1. Due to late tracking detail upload." +
                                                                "\n\t2. Due to non availability of product" +
                                                                "\n\t3. Due to any issue in transit." +
                                                                "\n\t4. Due to parcel lost." +
                                                                "\n\t5. Due to cancelled by customer on complaint." +
                                                                "\n\t6. Due to fraud or any unethical activity." +
                                                                "\n\t7. Cancelled by LuxuryShauk executive due to safety and security reason." +
                                                                "\n\nFor any concern mail us on Luxuryshaukapp@gmail.com" +
                                                                "\n\nThank You, \nLuxuryShauk Team";
                                                        String body2 = "Order : #" + Order.getOrder_no() + "" +
                                                                "\nOrder Name : " + Order.getCaption() + " by user " + user.getUser_id() + "has been cancelled, this could be due to one of the following reasons : " +
                                                                "\n\t1. Due to late tracking detail upload." +
                                                                "\n\t2. Due to non availability of product" +
                                                                "\n\t3. Due to any issue in transit." +
                                                                "\n\t4. Due to parcel lost." +
                                                                "\n\t5. Due to cancelled by customer on complaint." +
                                                                "\n\t6. Due to fraud or any unethical activity." +
                                                                "\n\t7. Cancelled by LuxuryShauk executive due to safety and security reason." +
                                                                "\n\nThank You, \nLuxuryShauk Team";
                                                        FirebaseDatabase.getInstance().getReference().child("sell").child(ds.getKey()).child(Order.getOrder_id()).child("status").setValue(4);
//                                                                    FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("status").setValue(4);
                                                        FirebaseDatabase.getInstance().getReference().child("buy").addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot adataSnapshot) {
                                                                for (DataSnapshot dds : adataSnapshot.getChildren()) {
                                                                    for (DataSnapshot aads : dds.getChildren()) {
                                                                        if (aads.getKey().equals(Order.getOrder_id())) {
                                                                            FirebaseDatabase.getInstance().getReference().child("buy").child(dds.getKey()).child(aads.getKey()).child("status").setValue(4);
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        FirebaseDatabase.getInstance().getReference().child("sell").child(ds.getKey()).child(Order.getOrder_id()).child("isseen").setValue("unseen");
                                                        homeActivity.sendEmail(body, "Order Cancelled #" + Order.getOrder_no(), Order.getBuyer_email());
                                                        homeActivity.sendEmail(body1, "Order Cancelled #" + Order.getOrder_no(), user.getEmail());
                                                        homeActivity.sendEmail(body2, "Order Cancelled #" + Order.getOrder_no(), "support@collectmoney.in");
                                                        email_sent = true;
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        } else {

                                        }

                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }, 0, 5, TimeUnit.SECONDS);

        return view;
    }


    private String getTimestampDifference(order_sell Notification){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        String photoTimestamp = Notification.getDate();
        if(photoTimestamp.contains(" "))
        {
            photoTimestamp = photoTimestamp.replace(" ","T");
            photoTimestamp += "Z";

        }
        Log.d(TAG, "getTimestampDifference: Time from notification model = " + photoTimestamp);
        try{
            timestamp = sdf.parse(photoTimestamp);
            Log.d(TAG, "getTimestampDifference: today time = " + today.getTime() + " timestamp.getTime() = " + timestamp.getTime());
            difference = String.valueOf(Math.round((timestamp.getTime()-(today.getTime()) - ((1800+43200-259200 - 86400) * 1000)) /1000));
//            difference = String.valueOf(Math.round((timestamp.getTime()-(today.getTime()) - ((1080+43200) * 1000)) /1000));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

}
