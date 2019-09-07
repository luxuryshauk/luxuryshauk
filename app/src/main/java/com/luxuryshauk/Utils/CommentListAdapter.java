package com.luxuryshauk.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.Home.HomeActivity;
import com.luxuryshauk.Profile.ProfileActivity;
import com.luxuryshauk.models.Photo;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import com.luxuryshauk.R;
import com.luxuryshauk.models.Comment;
import com.luxuryshauk.models.User;
import com.luxuryshauk.models.UserAccountSettings;

/**
 * Created by User on 8/22/2017.
 */

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentListAdapter";

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;
    String user_id,photo_id,comment_id;

    public CommentListAdapter(@NonNull Context context, @LayoutRes int resource,
                              @NonNull List<Comment> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;

        layoutResource = resource;
    }

    private static class ViewHolder{
        TextView comment, username, timestamp, reply, likes, delete_btn;
        CircleImageView profileImage;
        ImageView like;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.comment = (TextView) convertView.findViewById(R.id.comment);
            holder.username = (TextView) convertView.findViewById(R.id.comment_username);
            holder.timestamp = (TextView) convertView.findViewById(R.id.comment_time_posted);
            //holder.reply = (TextView) convertView.findViewById(R.id.comment_reply);
            holder.like = (ImageView) convertView.findViewById(R.id.comment_like);
            holder.likes = (TextView) convertView.findViewById(R.id.comment_likes);
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.comment_profile_image);
            holder.delete_btn = (TextView) convertView.findViewById(R.id.delete_btn);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //set the comment
        holder.comment.setText(getItem(position).getComment());
        user_id = getItem(position).getUser_id();
        photo_id = getItem(position).getPhoto_id();
        comment_id = getItem(position).getComment_id();
        final View view = convertView;

        //set the timestamp difference
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timestamp.setText(timestampDifference + " d");
        }else{
            holder.timestamp.setText("today");
        }


        //set the username and profile image
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( final DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    holder.username.setText(
                            singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            User u = new User();
                            u.setUser_id(getItem(position).getUser_id());
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity),
                                    mContext.getString(R.string.home_activity));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(mContext.getString(R.string.intent_user), u);
                            mContext.startActivity(intent);
                        }
                    });
                    FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    if(singleSnapshot.getValue(UserAccountSettings.class).getUser_id().equals(mAuth.getCurrentUser().getUid()))
                    {
                        holder.delete_btn.setVisibility(View.VISIBLE);
                        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                view.setVisibility(View.GONE);
                                Log.d(TAG, "onClick: comment model = " + getItem(position).toString() + " Photo id = " + photo_id + " comment id = "+ comment_id);
                                FirebaseDatabase.getInstance().getReference()
                                        .child("user_photos")
                                        .child(user_id)
                                        .child(photo_id)
                                        .child("comments")
                                        .child(getItem(position).getComment_id())
                                        .setValue(null);

                                FirebaseDatabase.getInstance().getReference().
                                        child("photos")
                                        .child(photo_id)
                                        .child("comments")
                                        .child(getItem(position).getComment_id())
                                        .setValue(null);
                                FirebaseDatabase.getInstance().getReference()
                                        .child("photos")
                                        .child(photo_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Photo p = dataSnapshot.getValue(Photo.class);
                                        Photo newPhoto = new Photo();
                                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                                        newPhoto.setCaption(objectMap.get("caption").toString());
                                        newPhoto.setTags(objectMap.get("tags").toString());
                                        newPhoto.setPhoto_id(objectMap.get("photo_id").toString());
                                        newPhoto.setUser_id(objectMap.get("user_id").toString());
                                        newPhoto.setDate_created(objectMap.get("date_created").toString());
                                        newPhoto.setImage_path(objectMap.get("image_path").toString());
//                                        try {
//                                            ((HomeActivity) mContext).onCommentThreadSelected(newPhoto,
//                                                    mContext.getString(R.string.home_activity));
//                                        }catch (Exception e){
//                                            ((ProfileActivity) mContext).onCommentThreadSelectedListener(newPhoto);
//                                        }
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
//                                ((HomeActivity)mContext).onCommentThreadSelected(getItem(position),
//                                        mContext.getString(R.string.home_activity));

                                //going to need to do something else?
                                ((HomeActivity)mContext).hideLayout();



                                notifyDataSetChanged();


                            }
                        });
                    }

                    imageLoader.displayImage(
                            singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                            holder.profileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });

        try{
            if(position == 0){
                holder.like.setVisibility(View.GONE);
                holder.likes.setVisibility(View.GONE);
                holder.reply.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){
            Log.e(TAG, "getView: NullPointerException: " + e.getMessage() );
        }


        return convertView;
    }

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(Comment comment){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = comment.getDate_created();
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






























