package com.luxuryshauk.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luxuryshauk.Profile.SellListAdapter;
import com.luxuryshauk.R;
import com.luxuryshauk.models.order_sell;

import java.util.ArrayList;

public class sell extends AppCompatActivity {


    ListView lv;
    String TAG = "sell";
    SellListAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    FirebaseMethods mFirebaseMethods;
    StorageReference mStorageReference;
    String userID;
    ArrayList<order_sell> orders_array,rev;
    public Uri selectedImageUri;
    public int SELECT_FILE1=1;
    public String selectedPath1;
    Context mContext = sell.this;

    int RESULT_LOAD_IMAGE = 1;
    public static Context applicationContext;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    TextView countdownTimer;
    boolean email_sent = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sell_record);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        userID = mAuth.getCurrentUser().getUid();
        rev = new ArrayList<>();
        orders_array = new ArrayList<>();

        lv = (ListView) findViewById(R.id.order_sell);
        Query query = FirebaseDatabase.getInstance().getReference().child("sell").child(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    orders_array.add(ds.getValue(order_sell.class));
                    Log.d(TAG, "onDataChange: sell user key = " + ds.getKey());
                }
                if (orders_array.size() == dataSnapshot.getChildrenCount()) {
                    int reverseArrayCounter = orders_array.size() - 1;

                    for (int i = reverseArrayCounter; i >= 0; i--) {
                        //Log.d(TAG, "onCreate: reversing array = " + buylist.get(i).getTarget());
                        rev.add(orders_array.get(i));
                    }
                }
                adapter = new SellListAdapter(mContext, R.layout.fragment_orders_sell, rev);
                lv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseListOptions<order_sell> options = new FirebaseListOptions.Builder<order_sell>()
                .setLayout(R.layout.fragment_orders_sell)
                .setQuery(query, order_sell.class)
                .setLifecycleOwner(sell.this)
                .build();

//        adapter = new FirebaseListAdapter(options) {
//            @Override
//            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
//                TextView order_id_v = v.findViewById(R.id.order_id);
//                TextView caption = v.findViewById(R.id.caption);
//                TextView buyer = v.findViewById(R.id.buyer_name);
//                TextView price = v.findViewById(R.id.price);
//                ImageView product = v.findViewById(R.id.product);
//                Button order = v.findViewById(R.id.order);
//                Button track_btn = v.findViewById(R.id.track);
//
//                final order_sell Order = (order_sell) model;
//                if(Order.getBuyer()!= null)
//                {
//                    price.setText("₹ "+ Order.getPrice());
//                    Picasso.get().load(Order.getImgurl()).into(product);
//
//                order_id_v.setText("Order ID :#"+Order.getOrder_no());
//                buyer.setText("Sold to "+Order.getBuyer());
//                caption.setText(Order.getCaption());
//
//                    order.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            setContentView(R.layout.order_summary_sell);
//                            TextView caption = (TextView)findViewById(R.id.caption_sum);
//                            TextView order_id = (TextView)findViewById(R.id.order_no);
//                            TextView total_price = (TextView)findViewById(R.id.price_display_sum);
//                            TextView inst_price = (TextView)findViewById(R.id.instaasell_fee);
//                            TextView total_earn = (TextView)findViewById(R.id.total_sum);
//                            TextView buyer = (TextView)findViewById(R.id.CustomerName_sum);
//                            TextView address = (TextView)findViewById(R.id.Address_sum);
//                            TextView phone = (TextView)findViewById(R.id.phone_sum);
//                            final TextView time = (TextView)findViewById(R.id.time);
//                            final TextView status = (TextView)findViewById(R.id.track_details_status);
//                            ImageView pic = (ImageView)findViewById(R.id.imageView);
//                            caption.setText(Order.getCaption());
//                            order_id.setText("Order ID: #"+Order.getOrder_no());
//                            int t_p = Integer.parseInt(Order.getPrice());
//                            int i_p = (int)Math.ceil((float)t_p*.05);
//                            int e_p = (int)Math.floor((float)t_p*.95);
//                            Picasso.get().load(Order.getImgurl()).into(pic);
//                            total_price.setText("₹ "+String.valueOf(t_p));
//                            inst_price.setText("- ₹ "+String.valueOf(i_p));
//                            total_earn.setText("₹ "+String.valueOf(e_p));
//                            buyer.setText(Order.getBuyer());
//                            address.setText(Order.getFlat()+", "+ Order.getStreet()+", "+Order.getCity()+", "+Order.getStreet() + " - "+Order.getPin()+" India");
//                            phone.setText("Mobile No +91-" + Order.getPhone());
//                            DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("track");
//                            t_ref.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    track Track = dataSnapshot.getValue(track.class);
//                                    if(Track.getStatus().equals("0"))
//                                    {
//                                        status.setText("Not Yet Updated");
//                                    }
//                                    else
//                                    {
//                                        status.setText("Updated");
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                            DatabaseReference d_ref = FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("date");
//                            d_ref.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    String date = dataSnapshot.getValue(String.class);
//                                    time.setText(date);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                    });
//                    track_btn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            setContentView(R.layout.layout_track_details_seller);
//                            final EditText c_name = (EditText) findViewById(R.id.courier_service_name);
//                            final EditText c_no = (EditText) findViewById(R.id.courier_number);
//                            DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("track");
//                            t_ref.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    track Track = dataSnapshot.getValue(track.class);
//                                    if(!Track.getStatus().equals("0")) {
//                                        if (!Track.getName().equals("") || !Track.getNo().equals("")) {
//                                            c_name.setText(Track.getName());
//                                            c_no.setText(Track.getNo());
//                                        }
//                                    }
//                                }
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                            ImageView pic_upload = (ImageView) findViewById(R.id.upload_btn);
//                            Button proceed = (Button) findViewById(R.id.proceed);
//                            try
//                            {
//                                if(selectedPath1 !=null)
//                                {
//                                    Picasso.get().load(selectedImageUri).into(pic_upload);
//                                }
//                            }catch (NullPointerException e)
//                            {
//                                System.out.println(e.toString());
//                            }
//
//                            pic_upload.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    openGallery(SELECT_FILE1);
//                                }
//                            });
//
//                            proceed.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                        String name = c_name.getText().toString();
//                                        String no = c_no.getText().toString();
//                                        if(!name.equals("") && !no.equals("")) {
//                                            track Track = new track();
//                                            Track.setName(name);
//                                            Track.setNo(no);
//                                            Track.setPic("URL");
//                                            Track.setStatus("1");
//                                            Toast.makeText(getApplicationContext(), "Tracking Details Updated!", Toast.LENGTH_LONG);
//                                            DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("track");
//                                            //DatabaseReference db_ref1 = FirebaseDatabase.getInstance().getReference().child("buy").child(Order.getBuyer_id()).child(Order.getOrder_id()).child("track");
//                                            db_ref.setValue(Track);
//                                            //db_ref1.setValue(Track);
//                                        }else
//                                        {
//                                            Toast.makeText(getApplicationContext(), "Fill All the fields", Toast.LENGTH_LONG);
//                                        }
//                                    //uploadimg();
//                                }
//                            });
//                        }
//                    });
//                }
//                else
//                {
//                    order_id_v.setText("Sold List is Empty");
//                }
//
//
//            }
//        };
    }

    public void uploadimg()
    {
        if(selectedPath1 !=null)
        {

        }
    }

    public void openGallery(int req_code) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select file to upload "), req_code);
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
////        if (resultCode == RESULT_OK) {
////            selectedImageUri = data.getData();
////            selectedPath1 = getPath(selectedImageUri);
////            System.out.println("selectedPath1 : " + selectedPath1);
////            System.out.println("selectedPath1 : " + selectedImageUri);
////            final StorageReference ref = mStorageReference.child("images/track_invoice/img"+ UUID.randomUUID().toString());
////            ref.putFile(selectedImageUri).
////                    addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>(){
////                        @Override
////                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
////                            Toast.makeText(getApplicationContext(), "Uploaded",Toast.LENGTH_LONG);
////                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
////                                @Override
////                                public void onSuccess(Uri downloadPhotoUrl) {
////                                    //downloadPhotoUrl.toString();
////                                }
////                            });
////                        }
////                    }).addOnFailureListener(new OnFailureListener() {
////                @Override
////                public void onFailure(@NonNull Exception e) {
////                    Toast.makeText(getApplicationContext(), "Failed",Toast.LENGTH_LONG);
////                }
////            }).addOnProgressListener(new OnProgressListener(){
////                @Override
////                public void onProgress(Object o) {
////                    Toast.makeText(getApplicationContext(), "Uploading",Toast.LENGTH_LONG);
////                }
////            });
////        }
////        else
////        {
//            Log.d(TAG, "onActivityResult: activity success");
//            try {
//                Log.d(TAG, "onActivityResult: activity success uploading pic....");
//                filePath = data.getData();
//
//                applicationContext = AccountSettingsActivity.getContextOfApplication();
//                //Log.d(TAG, "onActivityResult: URI = "+filePath.toString() + " Application Context = " + applicationContext.toString());
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
//                //                imageView.setImageBitmap(bitmap);
//                SellProductTrackFragment sptf = new SellProductTrackFragment();
//                sptf.uploadImage(filePath);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
////        }
//
//    }


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

//
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
