package com.luxuryshauk.Utils;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luxuryshauk.Profile.BuyListAdapter;
import com.luxuryshauk.models.order;
import com.luxuryshauk.R;

import java.util.ArrayList;

public class buy extends AppCompatActivity {
    String TAG = "buymod";
    ListView lv;
    BuyListAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    FirebaseMethods mFirebaseMethods;
    StorageReference mStorageReference;
    String userID;
    private ArrayList<order> buylist,rev;
    order morder;
    Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_buy_record);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        userID = mAuth.getCurrentUser().getUid();
        mContext = buy.this;
        buylist = new ArrayList<>();
        rev = new ArrayList<>();
        lv = (ListView) findViewById(R.id.order_buy);
        Query query = FirebaseDatabase.getInstance().getReference().child("buy").child(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: buy module = " + ds.getKey());
                    morder = ds.getValue(order.class);
                    Log.d(TAG, "onDataChange: buy module = " + morder.getOrder_no() + morder.getName());

                    buylist.add(morder);
//                    Toast.makeText(mContext,"order id = " + ds.getKey(),Toast.LENGTH_SHORT).show();
                }
                if (buylist.size() == dataSnapshot.getChildrenCount()) {
                    int reverseArrayCounter = buylist.size() - 1;

                    for (int i = reverseArrayCounter; i >= 0; i--) {
                        //Log.d(TAG, "onCreate: reversing array = " + buylist.get(i).getTarget());
                        rev.add(buylist.get(i));
                    }
                }
                adapter = new BuyListAdapter(mContext, R.layout.fragment_orders_buy, rev);

                lv.setAdapter(adapter);
//                if(buylist.size() == dataSnapshot.getChildrenCount()) {
//                    int reverseArrayCounter = buylist.size()-1;
//
//                    for (int i = reverseArrayCounter; i >= 0; i--) {
//                        //Log.d(TAG, "onCreate: reversing array = " + notiArray.get(i).getTarget());
//                        rev.add(buylist.get(i));
//                    }
//                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "onDataChange: buy module = " + buylist.size());



//        FirebaseListOptions<order> options = new FirebaseListOptions.Builder<order>()
//                .setLayout(R.layout.fragment_orders_buy)
//                .setQuery(query,order.class)
//                .setLifecycleOwner(buy.this)
//                .build();
//        adapter = new FirebaseListAdapter(options) {
//            @Override
//            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
//                TextView order_id_v = v.findViewById(R.id.order_id);
//                TextView caption = v.findViewById(R.id.caption);
//                TextView seller = v.findViewById(R.id.seller);
//                ImageView product = v.findViewById(R.id.product);
//                Button order = v.findViewById(R.id.order);
//                final Button track = v.findViewById(R.id.track);
//                TextView price = v.findViewById(R.id.price);
//                final order Order = (order) model;
//                price.setText("₹ "+Order.getPrice());
//                Picasso.get().load(Order.getImgurl()).into(product);
//                order_id_v.setText("Order ID :#"+Order.getOrder_no());
//                //caption.setText(Order.);
//                seller.setText("Sold by "+Order.getSeller());
//                caption.setText(Order.getCaption());
//                order.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        setContentView(R.layout.order_summary_buy);
////                        getSupportFragmentManager()
////                                .beginTransaction()
////                                .add(R.id.container, R.layout.order_summary_buy, "order")
////                                .addToBackStack("order")
////                                .commit();
//                        ImageView img = (ImageView)findViewById(R.id.imageView);
//                        TextView cap = (TextView)findViewById(R.id.caption_sum);
//                        TextView price = (TextView)findViewById(R.id.price_display_sum);
//                        TextView order_id = (TextView)findViewById(R.id.order_no);
//                        TextView name = (TextView)findViewById(R.id.CustomerName_sum);
//                        TextView addr = (TextView)findViewById(R.id.Address_sum);
//                        TextView phone = (TextView)findViewById(R.id.phone_sum);
//                        final TextView time = findViewById(R.id.time);
//                        final TextView status = (TextView)findViewById(R.id.track_details_status);
//                        Picasso.get().load(Order.getImgurl()).into(img);
//                        cap.setText(Order.getCaption());
//                        price.setText("₹ "+Order.getPrice());
//                        order_id.setText("Order ID : #"+String.valueOf(Order.getOrder_no()));
//                        name.setText(Order.getName());
//                        addr.setText(Order.getFlat()+", "+ Order.getStreet()+", "+Order.getCity()+", "+Order.getStreet() + " - "+Order.getPin()+" India");
//                        phone.setText("Mobile No +91-" + Order.getPhone());
//                        DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("buy").child(userID).child(Order.getOrder_id()).child("track");
//                        t_ref.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                com.instaasell.models.track Track = dataSnapshot.getValue(com.instaasell.models.track.class);
//                                if(Track.getStatus().equals("0"))
//                                {
//                                    status.setText("Not Yet Updated");
//                                }
//                                else
//                                {
//                                    status.setText("Updated");
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                            }
//                        });
//                        DatabaseReference d_ref = FirebaseDatabase.getInstance().getReference().child("buy").child(userID).child(Order.getOrder_id()).child("date");
//                        d_ref.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String date = dataSnapshot.getValue(String.class);
//                                time.setText(date);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                });
//                track.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        setContentView(R.layout.layout_track_details_buyer);
//                        final TextView c_name = (TextView)findViewById(R.id.courier_service_name);
//                        final TextView c_no = (TextView)findViewById(R.id.courier_number);
//                        final TextView download = (TextView)findViewById(R.id.download);
//                        final TextView status = (TextView)findViewById(R.id.status);
//                        DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("buy").child(userID).child(Order.getOrder_id()).child("track");
//                        t_ref.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                com.instaasell.models.track Track = dataSnapshot.getValue(com.instaasell.models.track.class);
//                                if(Track.getStatus().equals("0"))
//                                {
//                                    status.setText("Not Yet Updated");
//
//                                }
//                                else
//                                {
//                                    status.setText("Updated");
//                                    c_name.setText(Track.getName());
//                                    c_no.setText(Track.getNo());
//                                    download.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//                });
//
//            }
//        };

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
}

