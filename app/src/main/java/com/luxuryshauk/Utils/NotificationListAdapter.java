package com.luxuryshauk.Utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.R;
import com.luxuryshauk.models.notification;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationListAdapter extends ArrayAdapter<notification> {
    private static final String TAG = "Notification Adapter";


    private LayoutInflater mInflater;
    private List<notification> mNotification = null;
    private int layoutResource;
    private Context mContext;

    public NotificationListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<notification> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mNotification = objects;
    }

    private static class ViewHolder{
         CircleImageView profile_pic;
         TextView comment;
         TextView time;
         TextView empty;
         notification Notification;
         String by_user;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final NotificationListAdapter.ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new NotificationListAdapter.ViewHolder();

            holder.profile_pic = convertView.findViewById(R.id.profilepic);
            holder.comment = (TextView) convertView.findViewById(R.id.comments);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            //holder.empty = (TextView) convertView.findViewById(R.id.empty_not);
            convertView.setTag(holder);
        }else{
            holder = (NotificationListAdapter.ViewHolder) convertView.getTag();
        }
        FirebaseDatabase.getInstance().getReference()
                .child("user_account_settings").child(getItem(position).by_user).child("profile_photo").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    String imgurl = dataSnapshot.getValue(String.class);
                                    if (getItem(position).getType().equals("4")) {
                                        Picasso.get().load("https://collectmoney.in/app/image.png").into(holder.profile_pic);
                                    } else {
                                        Picasso.get().load(imgurl).into(holder.profile_pic);
                                    }
                                }catch (Exception e){}
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getItem(position).by_user).child("username");

        if (getItem(position).getType().equals("1")) {
     //       holder.empty.setVisibility(View.GONE);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String uname = dataSnapshot.getValue(String.class);
                        String sourceString = "<b>" + uname + "</b> liked your post ";
                        holder.comment.setText(Html.fromHtml(sourceString));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (getItem(position).getType().equals("2")) {
//            holder.empty.setVisibility(View.GONE);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String uname = dataSnapshot.getValue(String.class);
                        String sourceString = "<b>" + uname + "</b> commented on your post ";
                        holder.comment.setText(Html.fromHtml(sourceString));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (getItem(position).getType().equals("3")) {
   //         holder.empty.setVisibility(View.GONE);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String uname = dataSnapshot.getValue(String.class);
                        String sourceString = "<b>" + uname + "</b> placed an order ";
                        holder.comment.setText(Html.fromHtml(sourceString));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (getItem(position).getType().equals("4")) {
 //           holder.empty.setVisibility(View.GONE);
            String sourceString = "<b>" + getItem(position).getBy_user() + "</b> placed an order ";
            holder.comment.setText(Html.fromHtml(sourceString));
        }
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.time.setText(timestampDifference + " d");
        }else{
            holder.time.setText("today");
        }
            //holder.time.setText("Time");


        return convertView;

    }
    private String getTimestampDifference(notification Notification){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        String photoTimestamp = Notification.getTime();
        if(photoTimestamp.contains(" "))
        {
            photoTimestamp = photoTimestamp.replace(" ","T");
            photoTimestamp += "Z";

        }
        Log.d(TAG, "getTimestampDifference: Time from notification model = " + photoTimestamp);
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

}
