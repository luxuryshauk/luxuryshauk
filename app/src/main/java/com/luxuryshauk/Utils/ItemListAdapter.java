package com.luxuryshauk.Utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.luxuryshauk.R;
import com.luxuryshauk.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 9/17/2017.
 */

public class ItemListAdapter extends ArrayAdapter<Photo>{

    private static final String TAG = "ItemListAdapter";


    private LayoutInflater mInflater;
    private List<Photo> mUsers = null;
    private int layoutResource;
    private Context mContext;
    private String[] img_urls;

    public ItemListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }

    private static class ViewHolder{
        TextView username, email;
        CircleImageView profileImage;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        String original_path = getItem(position).getImage_path();
        try {
            if (original_path.contains(",")) {
                img_urls = original_path.split(", ");
                original_path = img_urls[1];
            }
        }catch (Exception e){

        }
        Log.d(TAG, "getView: image path = "+ getItem(position).getImage_path());


        holder.username.setText(getItem(position).getCaption());
        holder.email.setText("₹ " + String.valueOf(getItem(position).getPrice()));
        Picasso.get().load(original_path).into(holder.profileImage);

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference.child(mContext.getString(R.string.dbname_user_account_settings))
//                .orderByChild(mContext.getString(R.string.field_user_id))
//                .equalTo(getItem(position).getUser_id());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
//                    Log.d(TAG, "onDataChange: found user: " +
//                            singleSnapshot.getValue(UserAccountSettings.class).toString());
//
//                    ImageLoader imageLoader = ImageLoader.getInstance();
//
//                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
//                            holder.profileImage);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        return convertView;
    }
}

























