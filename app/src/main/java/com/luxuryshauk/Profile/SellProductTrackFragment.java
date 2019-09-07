package com.luxuryshauk.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luxuryshauk.Home.HomeActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.Utils.FilePaths;
import com.luxuryshauk.models.User;
import com.luxuryshauk.models.order_sell;
import com.luxuryshauk.models.track;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SellProductTrackFragment extends Fragment {
    String TAG = "SellProduct";
    int RESULT_LOAD_IMAGE = 1;
    public static Context applicationContext;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    TextView countdownTimer;
    boolean email_sent = false;
    boolean email_sent1 = false;
    boolean email_sent2 = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_track_details_seller, container, false);
        final String userID = FirebaseAuth.getInstance().getUid();
        final EditText c_name = (EditText) view.findViewById(R.id.courier_service_name);
        final EditText c_no = (EditText) view.findViewById(R.id.courier_number);
        countdownTimer = (TextView)view.findViewById(R.id.cancel_timer);
        applicationContext = AccountSettingsActivity.getContextOfApplication();
        applicationContext.getContentResolver();
        final ImageView pic_upload = (ImageView) view.findViewById(R.id.upload_btn);
        Button proceed = (Button) view.findViewById(R.id.proceed);
        final order_sell Order = (order_sell) getArguments().getSerializable("model");
        final DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("track");
        t_ref.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                track Track = dataSnapshot.getValue(track.class);
                if (!Track.getStatus().equals("0")) {
                    if (!Track.getName().equals("") || !Track.getNo().equals("")) {
                        c_name.setText(Track.getName());
                        c_no.setText(Track.getNo());
                        countdownTimer.setText("Successfully Uploaded Details");
                        //countdownTimer.setTextSize(20);
                        countdownTimer.setTextColor(Color.parseColor("#32CD32"));
                    }
                }else {
                    if (Order.getStatus() != 4) {

//                    if(!timestampDifference.equals("0")){
                        try {
                            Thread t = new Thread() {
                                @Override
                                public void run() {
                                    while (!isInterrupted()) {
                                        try {
                                            Thread.sleep(1000);

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String timestampDifference = getTimestampDifference(Order);
                                                    if (Integer.valueOf(timestampDifference) < 0) {
                                                        FirebaseDatabase.getInstance().getReference().child("users").child(userID).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                countdownTimer.setText("Order Cancelled");
                                                                if(!email_sent) {

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
                                                                    FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("status").setValue(4);
//                                                                    FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("status").setValue(4);
                                                                    FirebaseDatabase.getInstance().getReference().child("buy").addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            for(DataSnapshot ds :dataSnapshot.getChildren())
                                                                            {
                                                                                for(DataSnapshot ads : ds.getChildren())
                                                                                {
                                                                                    if(ads.getKey().equals(Order.getOrder_id()))
                                                                                    {
                                                                                        FirebaseDatabase.getInstance().getReference().child("buy").child(ds.getKey()).child(ads.getKey()).child("status").setValue(4);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                                    FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("isseen").setValue("unseen");
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
                                                        Log.d(TAG, "run: Timestamp = " + timestampDifference);

                                                        int day = (int) TimeUnit.SECONDS.toDays(Integer.valueOf(timestampDifference));
                                                        long hours = TimeUnit.SECONDS.toHours(Integer.valueOf(timestampDifference)) - (day * 24);
                                                        long minute = TimeUnit.SECONDS.toMinutes(Integer.valueOf(timestampDifference)) - (TimeUnit.SECONDS.toHours(Integer.valueOf(timestampDifference)) * 60);
                                                        long second = TimeUnit.SECONDS.toSeconds(Integer.valueOf(timestampDifference)) - (TimeUnit.SECONDS.toMinutes(Integer.valueOf(timestampDifference)) * 60);
                                                        countdownTimer.setText("Update tracking Before : " +
                                                                "\n" + day + " d " + hours + " h " + minute + " min " + second + " sec" +
                                                                "\nto avoid cancellation");
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };

                            t.start();
                        } catch (Exception e) {
                        }
//                    }else{
//                        holder.time.setText("today");
//                    }
                    }else {
                        countdownTimer.setText("Order Cancelled");
                        pic_upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(),"Order is cancelled you cannot add tracking now", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        pic_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = c_name.getText().toString();
                final String no = c_no.getText().toString();
                if (name.equals("") && no.equals("")) {
                    Toast.makeText(getContext(), "Enter courier name and number first", Toast.LENGTH_LONG).show();
                }else {
                    chooseImage();
                }
                Log.d("track upload test ", "onClick: button clicked" );
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_sent = false;



                final String name = c_name.getText().toString();
                final String no = c_no.getText().toString();
                if (!name.equals("") && !no.equals("")) {
                    FirebaseDatabase.getInstance().getReference().child("sell")
                            .child(userID).child(Order.getOrder_id())
                            .child("track").child("pic").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
//                                track Track = new track();
//                                Track.setName(name);
//                                Track.setNo(no);
//                                Track.setStatus("1");
                                DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("track");
                                //db_ref.setValue(Track);
                                db_ref.child("name").setValue(name);
                                db_ref.child("no").setValue(no);
                                db_ref.child("status").setValue("1");


                                FirebaseDatabase.getInstance().getReference().child("buy").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds :dataSnapshot.getChildren())
                                        {
                                            for(DataSnapshot ads : ds.getChildren())
                                            {
                                                if(ads.getKey().equals(Order.getOrder_id()))
                                                {
                                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("buy").child(ds.getKey())
                                                            .child(Order.getOrder_id()).child("track");
                                                    final DataSnapshot d = ds;
                                                    FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("track").child("pic").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            String img = dataSnapshot.getValue(String.class);
                                                            FirebaseDatabase.getInstance().getReference().child("buy").child(d.getKey())
                                                                    .child(Order.getOrder_id()).child("isseen").setValue("unseen");


                                                            ref.child("name").setValue(name);
                                                            ref.child("no").setValue(no);
                                                            ref.child("status").setValue("1");
                                                            ref.child("pic").setValue(img);

                                                            FirebaseDatabase.getInstance().getReference().child("users").child(userID).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    final User user = dataSnapshot.getValue(User.class);

                                                                    //countdownTimer.setText("Order Cancelled");
                                                                    if(!email_sent) {
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("sell").child(userID).child(Order.getOrder_id())
                                                                                .child("track").addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                                track Track = dataSnapshot.getValue(track.class);

                                                                                HomeActivity homeActivity = new HomeActivity();
                                                                                String body = "Your Order : #" + Order.getOrder_no() + "" +
                                                                                        "\nOrder Name : " + Order.getCaption() + " tracking has been updated, \nwith logistic partner name : "+ Track.getName() +
                                                                                        "\nTrack no : " + Track.getNo() + "" +
                                                                                        "\nReciept copy : " + Track.getPic() +
                                                                                        "\n\nThank You, \nLuxuryShauk Team";
                                                                                String body1 = "Your Order : #" + Order.getOrder_no() + "" +
                                                                                        "\nOrder Name : " + Order.getCaption() + " tracking has been updated, \nwith logistic partner name : "+ Track.getName() +
                                                                                        "\nTrack no : " + Track.getNo() + "" +
                                                                                        "\nReciept copy : " + Track.getPic() +
                                                                                        "\n\nThank You, \nLuxuryShauk Team";
                                                                                String body2 = "Your Order : #" + Order.getOrder_no() + "" +
                                                                                        "\nOrder Name : " + Order.getCaption() + " tracking has been updated, \nwith logistic partner name : "+ Track.getName() +
                                                                                        "\nTrack no : " + Track.getNo() + "" +
                                                                                        "\nReciept copy : " + Track.getPic() +
                                                                                        "\nSeller -> " + user.getUsername() + " Buyer -> " + Order.buyer +
                                                                                        "\n\nThank You, \nLuxuryShauk Team";
//                                                        FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("status").setValue(4);
                                                                                if(!email_sent2) {
                                                                                    FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("isseen").setValue("unseen");
                                                                                    homeActivity.sendEmail(body, "Tracking update order #" + Order.getOrder_no(), Order.getBuyer_email());
                                                                                    homeActivity.sendEmail(body1, "Tracking update order #" + Order.getOrder_no(), user.getEmail());
                                                                                    homeActivity.sendEmail(body2, "Tracking update order #" + Order.getOrder_no(), "support@collectmoney.in");
                                                                                    email_sent2 = true;
                                                                                }
                                                                                email_sent = true;

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                    Log.d(TAG, "onDataChange: order found " + Order.getOrder_id() + " User = " + ds.getKey());

                                                }
                                                else{
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(userID).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            final User user = dataSnapshot.getValue(User.class);

                                                            //countdownTimer.setText("Order Cancelled");
                                                            if(!email_sent) {
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("sell").child(userID).child(Order.getOrder_id())
                                                                        .child("track").addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                        track Track = dataSnapshot.getValue(track.class);


                                                                        HomeActivity homeActivity = new HomeActivity();
                                                                        String body = "Your Order : #" + Order.getOrder_no() + "" +
                                                                                "\nOrder Name : " + Order.getCaption() + " tracking has been updated, \nwith logistic partner name : "+ Track.getName() +
                                                                                "\nTrack no : " + Track.getNo() + "" +
                                                                                "\nReciept copy : " + Track.getPic() +
                                                                                "\n\nThank You, \nLuxuryShauk Team";
                                                                        String body1 = "Your Order : #" + Order.getOrder_no() + "" +
                                                                                "\nOrder Name : " + Order.getCaption() + " tracking has been updated, \nwith logistic partner name : "+ Track.getName() +
                                                                                "\nTrack no : " + Track.getNo() + "" +
                                                                                "\nReciept copy : " + Track.getPic() +
                                                                                "\n\nThank You, \nLuxuryShauk Team";
                                                                        String body2 = "Your Order : #" + Order.getOrder_no() + "" +
                                                                                "\nOrder Name : " + Order.getCaption() + " tracking has been updated, \nwith logistic partner name : "+ Track.getName() +
                                                                                "\nTrack no : " + Track.getNo() + "" +
                                                                                "\nReciept copy : " + Track.getPic() +
                                                                                "\nSeller -> " + user.getUsername() + " Buyer -> " + Order.buyer +
                                                                                "\n\nThank You, \nLuxuryShauk Team";
//                                                        FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("status").setValue(4);
                                                                        if(!email_sent1) {
                                                                            FirebaseDatabase.getInstance().getReference().child("sell").child(userID).child(Order.getOrder_id()).child("isseen").setValue("unseen");
                                                                            homeActivity.sendEmail(body, "Tracking update order #" + Order.getOrder_no(), Order.getBuyer_email());
                                                                            homeActivity.sendEmail(body1, "Tracking update order #" + Order.getOrder_no(), user.getEmail());
                                                                            homeActivity.sendEmail(body2, "Tracking update order #" + Order.getOrder_no(), "support@collectmoney.in");
                                                                            email_sent1 = true;
                                                                        }
                                                                        email_sent = true;
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
                                Intent i = getActivity().getIntent();
                                    startActivity(i);
                                    getActivity().finish();
                            }else{
                                Toast.makeText(getContext(), "Select Image", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                } else {
                    Toast.makeText(getContext(), "Fill All the fields", Toast.LENGTH_LONG).show();
                }
                //uploadimg();
            }
        });
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




    private void chooseImage() {
        Log.d(TAG, "Choose image onActivityResult: activity success about to start");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//        if (resultCode == RESULT_OK) {
//            selectedImageUri = data.getData();
//            selectedPath1 = getPath(selectedImageUri);
//            System.out.println("selectedPath1 : " + selectedPath1);
//            System.out.println("selectedPath1 : " + selectedImageUri);
//            final StorageReference ref = mStorageReference.child("images/track_invoice/img"+ UUID.randomUUID().toString());
//            ref.putFile(selectedImageUri).
//                    addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>(){
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            Toast.makeText(getApplicationContext(), "Uploaded",Toast.LENGTH_LONG);
//                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri downloadPhotoUrl) {
//                                    //downloadPhotoUrl.toString();
//                                }
//                            });
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getApplicationContext(), "Failed",Toast.LENGTH_LONG);
//                }
//            }).addOnProgressListener(new OnProgressListener(){
//                @Override
//                public void onProgress(Object o) {
//                    Toast.makeText(getApplicationContext(), "Uploading",Toast.LENGTH_LONG);
//                }
//            });
//        }
//        else
//        {
        Log.d(TAG, "onActivityResult: activity success");
        try {
            Log.d(TAG, "onActivityResult: activity success uploading pic....");
            filePath = data.getData();

            applicationContext = AccountSettingsActivity.getContextOfApplication();
            //Log.d(TAG, "onActivityResult: URI = "+filePath.toString() + " Application Context = " + applicationContext.toString());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
            //                imageView.setImageBitmap(bitmap);
            uploadImage(filePath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
//        }

    }






    public void uploadImage(Uri filePath) {
        try {

            final FilePaths filePaths = new FilePaths();

            final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final order_sell Order = (order_sell) getArguments().getSerializable("model");
            Log.d(TAG, "uploadImage: track order id = " + Order.getOrder_id());

//        if(filePath != null)
//        {
            final StorageReference ref = storageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + userID + "/trackslip" + UUID.randomUUID());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri = uri;
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("sell")
                                            .child(userID)
                                            .child(Order.getOrder_id())
                                            .child("track")
                                            .child("pic")
                                            .setValue(downloadUri.toString());

                                    Toast.makeText(getContext(), "Ready to upload", Toast.LENGTH_LONG).show();
//                                    Intent i = getActivity().getIntent();
//                                    startActivity(i);
//                                    getActivity().finish();
                                }

                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            Toast.makeText(getContext(), "Preparing image....", Toast.LENGTH_SHORT).show();


                        }
                    });
        }catch (Exception e){}
        }

//    }
}

