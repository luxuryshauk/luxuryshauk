package com.luxuryshauk.Profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.R;
import com.luxuryshauk.models.order_sell;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SellListAdapter extends ArrayAdapter<order_sell> {
    private static final String TAG = "Notification Adapter";


    private LayoutInflater mInflater;
    private List<order_sell> mNotification = null;
    private int layoutResource;
    private Context mContext;

    public SellListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<order_sell> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;

        this.mNotification = objects;
    }

    private static class ViewHolder {
        TextView order_id_v;
        TextView caption;
        TextView buyer;
        TextView price;
        ImageView product;
        Button order;
        Button track_btn;
        TextView datetime;
        ImageView neworder_i;
        ImageView newtrack_i;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final SellListAdapter.ViewHolder holder;
//        if(convertView == null){
        convertView = mInflater.inflate(layoutResource, parent, false);
        holder = new SellListAdapter.ViewHolder();
        holder.order_id_v = convertView.findViewById(R.id.order_id);
        holder.caption = convertView.findViewById(R.id.caption);
        holder.buyer = convertView.findViewById(R.id.buyer_name);
        holder.price = convertView.findViewById(R.id.price);
        holder.product = convertView.findViewById(R.id.product);
        holder.order = convertView.findViewById(R.id.order);
        holder.track_btn = convertView.findViewById(R.id.track);
        holder.datetime = convertView.findViewById(R.id.time_date);
        holder.neworder_i = convertView.findViewById(R.id.unseen_sell);
        holder.newtrack_i = convertView.findViewById(R.id.track_sell_indicator);

        holder.neworder_i.setVisibility(View.INVISIBLE);
        holder.newtrack_i.setVisibility(View.INVISIBLE);
        if(getItem(position).getIsseen().equals("unseen"))
        {
            holder.neworder_i.setVisibility(View.VISIBLE);

        }


        FirebaseDatabase.getInstance().getReference().child("sell")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getItem(position).getOrder_id()).child("track")
                .child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);

                if(status.equals("0") && getItem(position).getStatus()==0)
                {
                    Animation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(1000);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setRepeatMode(Animation.REVERSE);
                    holder.newtrack_i.startAnimation(animation);
                    holder.newtrack_i.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (getItem(position).getBuyer() != null) {
            holder.price.setText("₹ " + getItem(position).getPrice());
            Picasso.get().load(getItem(position).getImgurl()).into(holder.product);

            holder.order_id_v.setText("Order ID :#" + getItem(position).getOrder_no());
            holder.buyer.setText("Sold to " + getItem(position).getBuyer());
            holder.caption.setText(getItem(position).getCaption());
            holder.datetime.setText(getItem(position).getDate());
            convertView.setTag(holder);


            holder.order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.neworder_i.setVisibility(View.INVISIBLE);
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("sell")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getItem(position).getOrder_id())
                            .child("isseen")
                            .setValue("seen");
                    SellProductFragment fragment = new SellProductFragment();
                    Activity activity = (Activity)mContext;
                    FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager()
                            .beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("model", getItem(position));
                    fragment.setArguments(bundle);
                    ft.addToBackStack("backsell");
                    ft.replace(R.id.sellcontainer,fragment);
                    ft.commit();
                    //Toast.makeText(mContext, "Track tapped", Toast.LENGTH_SHORT).show();
                }
            });
            holder.track_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.newtrack_i.setVisibility(View.INVISIBLE);

                    SellProductTrackFragment fragment = new SellProductTrackFragment();
                    Activity activity = (Activity)mContext;
                    FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager()
                            .beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("model", getItem(position));
                    fragment.setArguments(bundle);
                    ft.addToBackStack("backsell");
                    ft.replace(R.id.sellcontainer,fragment);
                    ft.commit();
                    //Toast.makeText(mContext, "Order tapped", Toast.LENGTH_SHORT).show();
                }
            });

        }
        return convertView;

    }
}
