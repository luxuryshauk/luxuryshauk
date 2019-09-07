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
import com.luxuryshauk.models.order;
import com.squareup.picasso.Picasso;

public class BuyProductFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_summary_buy, container, false);
       
                        ImageView img = (ImageView)view.findViewById(R.id.imageView);
                        TextView cap = (TextView)view.findViewById(R.id.caption_sum);
                        TextView price = (TextView)view.findViewById(R.id.price_display_sum);
                        TextView order_id = (TextView)view.findViewById(R.id.order_no);
                        TextView name = (TextView)view.findViewById(R.id.CustomerName_sum);
                        TextView addr = (TextView)view.findViewById(R.id.Address_sum);
                        TextView phone = (TextView)view.findViewById(R.id.phone_sum);
                        TextView orderstatus = (TextView)view.findViewById(R.id.order_status);
                        TextView extra = (TextView)view.findViewById(R.id.extra_text);
                        final TextView time = view.findViewById(R.id.time);
                        final TextView status = (TextView)view.findViewById(R.id.track_details_status);
                        String userID = FirebaseAuth.getInstance().getUid();
                        order Order = (order) getArguments().getSerializable("model");
                        Picasso.get().load(Order.getImgurl()).into(img);
                        cap.setText(Order.getCaption());
                        price.setText("₹ "+Order.getPrice());
                        order_id.setText("Order ID : #"+String.valueOf(Order.getOrder_no()));
                        name.setText(Order.getName());
                        addr.setText(Order.getFlat()+", "+ Order.getStreet()+", "+Order.getCity()+", "+Order.getStreet() + " - "+Order.getPin()+" India");
                        phone.setText("Mobile No +91-" + Order.getPhone());
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
                        }





                        DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("buy").child(userID).child(Order.getOrder_id()).child("track");
                        t_ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                com.luxuryshauk.models.track Track = dataSnapshot.getValue(com.luxuryshauk.models.track.class);
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
                        DatabaseReference d_ref = FirebaseDatabase.getInstance().getReference().child("buy").child(userID).child(Order.getOrder_id()).child("date");
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
