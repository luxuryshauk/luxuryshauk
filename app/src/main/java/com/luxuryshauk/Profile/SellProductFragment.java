package com.luxuryshauk.Profile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.R;
import com.luxuryshauk.models.order_sell;
import com.luxuryshauk.models.track;
import com.squareup.picasso.Picasso;

public class SellProductFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_summary_sell, container, false);

        String userID = FirebaseAuth.getInstance().getUid();
        TextView caption = (TextView)view.findViewById(R.id.caption_sum);
        TextView order_id = (TextView)view.findViewById(R.id.order_no);
        TextView total_price = (TextView)view.findViewById(R.id.price_display_sum);
        TextView inst_price = (TextView)view.findViewById(R.id.instaasell_fee);
        TextView total_earn = (TextView)view.findViewById(R.id.total_sum);
        TextView buyer = (TextView)view.findViewById(R.id.CustomerName_sum);
        TextView address = (TextView)view.findViewById(R.id.Address_sum);
        TextView phone = (TextView)view.findViewById(R.id.phone_sum);
        final TextView time = (TextView)view.findViewById(R.id.time);
        final TextView status = (TextView)view.findViewById(R.id.track_details_status);
        TextView orderstatus = (TextView)view.findViewById(R.id.order_status);
        ImageView pic = (ImageView)view.findViewById(R.id.imageView);
        TextView extra = (TextView)view.findViewById(R.id.extra_text);

        order_sell Order = (order_sell) getArguments().getSerializable("model");
        caption.setText(Order.getCaption());
        order_id.setText("Order ID: #"+Order.getOrder_no());

        int t_p = Integer.parseInt(Order.getPrice());
        int i_p = (int)Math.ceil((float)t_p*.05);
        int e_p = (int)Math.floor((float)t_p*.95);
        extra.setText(Order.getExtra());

        switch (Order.getStatus())
        {
            case 0:
                orderstatus.setText("In Transit");
                orderstatus.setTextColor(Color.parseColor("#CCCC00"));

                break;
            case 1:
                orderstatus.setText("Issue in Transit");
                orderstatus.setTextColor(Color.parseColor("#8B0000"));
                break;
            case 2:
                orderstatus.setText("Delivered");
                orderstatus.setTextColor(Color.parseColor("#228B22"));
                break;
            case 3:
                orderstatus.setText("Complaint");
                orderstatus.setTextColor(Color.parseColor("#8B0000"));
                break;
            case 4:
                orderstatus.setText("Cancelled");
                orderstatus.setTextColor(Color.parseColor("#8B0000"));
                break;
//            default:
//                orderstatus.setText("All Done");
//                break;
        }


        Picasso.get().load(Order.getImgurl()).into(pic);
        total_price.setText("₹ "+String.valueOf(t_p));
        inst_price.setText("- ₹ "+String.valueOf(i_p));
        total_earn.setText("₹ "+String.valueOf(e_p));
        buyer.setText(Order.getBuyer());
        address.setText(Order.getFlat()+", "+ Order.getStreet()+", "+Order.getCity()+", "+Order.getStreet() + " - "+Order.getPin()+" India");
        phone.setText("Mobile No +91-" + Order.getPhone());
        DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("track");
        t_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                track Track = dataSnapshot.getValue(track.class);
                if(Track.getStatus().equals("0"))
                {
                    status.setText("Not Yet Updated");
                }
                else
                {
                    status.setText("Updated");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference d_ref = FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("date");
        d_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String date = dataSnapshot.getValue(String.class);
                time.setText(date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}