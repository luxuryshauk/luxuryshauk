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
import com.luxuryshauk.models.order;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BuyListAdapter extends ArrayAdapter<order> {
    private static final String TAG = "Notification Adapter";


    private LayoutInflater mInflater;
    private List<order> mNotification = null;
    private int layoutResource;
    private Context mContext;

    public BuyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<order> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mNotification = objects;
    }

    private static class ViewHolder{
        TextView order_id_v;
        TextView caption;
        TextView seller;
        ImageView product;
        Button order;
        Button track;
        TextView price;
        TextView datetime;
        ImageView unseen_order;
        ImageView unseen_order_track;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final BuyListAdapter.ViewHolder holder;
//        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new BuyListAdapter.ViewHolder();
             holder.order_id_v = convertView.findViewById(R.id.order_id);
             holder.caption = convertView.findViewById(R.id.caption);
             holder.seller = convertView.findViewById(R.id.seller);
             holder.product = convertView.findViewById(R.id.product);
             holder.order = convertView.findViewById(R.id.order);
             holder.track = convertView.findViewById(R.id.track);
             holder.price = convertView.findViewById(R.id.price);
             holder.datetime = convertView.findViewById(R.id.time_date);
             holder.unseen_order = convertView.findViewById(R.id.unseen_buy);
             holder.unseen_order_track = convertView.findViewById(R.id.unseen_buy_track);
             holder.unseen_order.setVisibility(View.INVISIBLE);
             holder.unseen_order_track.setVisibility(View.INVISIBLE);
             if(getItem(position).getIsseen().equals("unseen"))
             {
                 holder.unseen_order.setVisibility(View.VISIBLE);
             }
             String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

             FirebaseDatabase.getInstance()
                     .getReference()
                     .child("buy")
                     .child(userid)
                     .child(getItem(position).getOrder_id())
                     .child("track")
                     .child("status").addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     String status = dataSnapshot.getValue(String.class);
                     if(!status.equals("0") && getItem(position).getIsseen().equals("unseen"))
                     {
                         holder.unseen_order_track.setVisibility(View.VISIBLE);
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
            //holder.empty = (TextView) convertView.findViewById(R.id.empty_not);
            convertView.setTag(holder);
//        }else{
//            holder = (BuyListAdapter.ViewHolder) convertView.getTag();
//        }

        holder.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.unseen_order.setVisibility(View.INVISIBLE);
                BuyProductFragment fragment = new BuyProductFragment();
                Activity activity = (Activity)mContext;
                FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager()
                        .beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", getItem(position));
                fragment.setArguments(bundle);
                ft.addToBackStack("backsell");
                ft.replace(R.id.buycontainer,fragment);
                ft.commit();

            }
        });
        holder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyProductTrackFragment fragment = new BuyProductTrackFragment();
                Activity activity = (Activity)mContext;
                FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager()
                        .beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", getItem(position));
                fragment.setArguments(bundle);
                ft.addToBackStack("backsell");
                ft.replace(R.id.buycontainer,fragment);
                ft.commit();

            }
        });

        holder.datetime.setText(getItem(position).getDate());

        holder.price.setText("₹ "+getItem(position).getPrice());
        Picasso.get().load(getItem(position).getImgurl()).into(holder.product);
        holder.order_id_v.setText("Order ID :#"+getItem(position).getOrder_no());
        //caption.setText(Order.);
        holder.seller.setText("Sold by "+getItem(position).getSeller());
        holder.caption.setText(getItem(position).getCaption());
        return convertView;
    }
}
