package com.luxuryshauk.Likes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luxuryshauk.Profile.ProfileActivity;
import com.luxuryshauk.Profile.SellProductFragment;
import com.luxuryshauk.Utils.FirebaseMethods;
import com.luxuryshauk.Utils.NotificationListAdapter;
import com.luxuryshauk.Utils.ViewMultiplePostFragment;
import com.luxuryshauk.models.Comment;
import com.luxuryshauk.models.Photo;
import com.luxuryshauk.models.notification;
import com.luxuryshauk.models.order_sell;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import com.luxuryshauk.R;
import com.luxuryshauk.Utils.BottomNavigationViewHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by User on 5/28/2017.
 */

public class LikesActivity extends AppCompatActivity{
    private static final String TAG = "LikesActivity";
    private static final int ACTIVITY_NUM = 3;

    ListView lv;
    NotificationListAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    FirebaseMethods mFirebaseMethods;
    StorageReference mStorageReference;
    String userID;
    String by_user;
    private LinearLayoutManager mLayoutManager;
    public ArrayList<notification> notiArray;
    private ArrayList<notification> rev;



    private Context mContext = LikesActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notifications);
        Log.d(TAG, "onCreate: started.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        userID = mAuth.getCurrentUser().getUid();
        lv = (ListView) findViewById(R.id.notifications);
        notiArray = new ArrayList<>();
        rev = new ArrayList<>();


        String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference dref1 = FirebaseDatabase.getInstance().getReference()
                .child("notification").child(currentuid);
        dref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    dref1.child(ds.getKey()).child("seen").setValue("seen");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query = FirebaseDatabase.getInstance().getReference().child("notification").child(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    try {
                        notification n = ds.getValue(notification.class);
                        TextView empty = findViewById(R.id.empty_not);
                        empty.setVisibility(View.GONE);
                        notiArray.add(n);
                        Log.d(TAG, "onCreate: reversing array = " + notiArray.size());
                        if (notiArray.size() == dataSnapshot.getChildrenCount()) {
                            int reverseArrayCounter = notiArray.size() - 1;

                            for (int i = reverseArrayCounter; i >= 0; i--) {
                                Log.d(TAG, "onCreate: reversing array = " + notiArray.get(i).getTarget());
                                rev.add(notiArray.get(i));
                            }

                        }
                    }catch (Exception e){}

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Collections.reverse(notiArray);

        adapter = new NotificationListAdapter(mContext, R.layout.fragment_notification, rev);


//        FirebaseListOptions<notification> options = new FirebaseListOptions.Builder<notification>()
//                .setLayout(R.layout.fragment_notification)
//                .setQuery(query, notification.class)
//                .setLifecycleOwner(LikesActivity.this)
//                .build();

//        adapter = new FirebaseListAdapter(options) {
//
//
//            @Override
//            protected void populateView(final @NonNull View v, @NonNull Object model, int position) {
//                final CircleImageView profile_pic = v.findViewById(R.id.profilepic);
//                final TextView comment = v.findViewById(R.id.comments);
//                final TextView time = v.findViewById(R.id.time);
//                final TextView empty = findViewById(R.id.empty_not);
//                final notification Notification = (notification) model;
//
//                myRef.child("users").child(Notification.by_user).child("username").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        by_user = dataSnapshot.getValue(String.class);
//                        String timestampDifference = getTimestampDifference(Notification);
//
//                        v.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                FirebaseDatabase.getInstance().getReference().child("photos").child(Notification.target).addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Photo photo = dataSnapshot.getValue(Photo.class);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                                Toast.makeText(mContext,"Notification Clicked " + Notification.toString(),Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                        myRef.child("user_account_settings").child(Notification.by_user).child("profile_photo").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String imgurl = dataSnapshot.getValue(String.class);
//                                if(Notification.getType().equals("4")) {
//                                    Picasso.get().load("https://collectmoney.in/app/image.png").into(profile_pic);
//                                }else{
//                                    Picasso.get().load(imgurl).into(profile_pic);
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                        try
//                        {
//                                if(Notification.getType().equals("1"))
//                                {
//                                    empty.setVisibility(View.GONE);
//                                    String sourceString = "<b>" + by_user + "</b> liked your post ";
//                                    comment.setText( Html.fromHtml(sourceString));
//
//                                }else if(Notification.getType().equals("2")){
//                                    empty.setVisibility(View.GONE);
//                                    String sourceString = "<b>" + by_user + "</b> commented on your post ";
//                                    comment.setText( Html.fromHtml(sourceString));
//
//                                }else if(Notification.getType().equals("3"))
//                                {
//                                    empty.setVisibility(View.GONE);
//                                    String sourceString = "<b>" + by_user + "</b> placed an order";
//                                    comment.setText( Html.fromHtml(sourceString));
//                                }else if(Notification.getType().equals("4"))
//                                {
//                                    empty.setVisibility(View.GONE);
//                                    String sourceString = "<b>" + Notification.getBy_user() + "</b> placed an order ";
//                                    comment.setText( Html.fromHtml(sourceString));
//                                }
//                                if(!timestampDifference.equals("0")){
//                                    time.setText(timestampDifference + " d");
//                                }else{
//                                    time.setText("today");
//                                }
//                        }catch(Exception e) {
//                            System.out.println(e);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//        };
        final DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(rev.get(position).getType().equals("1") || rev.get(position).getType().equals("2"))
                {
                    dref.child("photos").child(rev.get(position).getTarget()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                Photo newPhoto = new Photo();
                                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                                newPhoto.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                                newPhoto.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                                newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                                newPhoto.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                                newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                                newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                                List<Comment> commentsList = new ArrayList<Comment>();
                                for (DataSnapshot dSnapshot : dataSnapshot
                                        .child(getString(R.string.field_comments)).getChildren()) {
                                    Comment comment = new Comment();
                                    comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                    comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                    comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                    commentsList.add(comment);
                                }
                                newPhoto.setComments(commentsList);
                                Intent i = new Intent(LikesActivity.this, ProfileActivity.class);
                                String[] imagesurl = newPhoto.getImage_path().split(", ");
                                String[] modifiedArray_f = newPhoto.getImage_path().split(", ");
                                for (String m : modifiedArray_f) {
                                    Log.d(TAG, "onDataChange: final modified image url list = " + m);
                                }
                                if (newPhoto.getImage_path().contains(",")) {

                                    imagesurl[imagesurl.length - 1] = imagesurl[imagesurl.length - 1].replace("]", "");

                                    for (String s : imagesurl)
                                        Log.d(TAG, "onDataChange: notification images url = " + s);
                                    modifiedArray_f = Arrays.copyOfRange(imagesurl, 1, imagesurl.length);
                                }

                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("notification", 1);
                                i.putExtra("image", newPhoto.getImage_path());
                                i.putExtra("id", newPhoto.getPhoto_id());
                                i.putExtra("type", newPhoto.getType());
                                i.putExtra("images", modifiedArray_f);
                                startActivity(i);
                            }catch (Exception e)
                            {
                                Toast.makeText(mContext, "Post unavailable",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else if(rev.get(position).getType().equals("3") || rev.get(position).getType().equals("4"))
                {
                    myRef.child("sell").child(userID).child(rev.get(position).getNot_id())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    try {
                                        if(dataSnapshot.exists()) {
                                            order_sell o = dataSnapshot.getValue(order_sell.class);
                                            SellProductFragment fragment = new SellProductFragment();
                                            Activity activity = (Activity) mContext;
                                            FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager()
                                                    .beginTransaction();
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("model", o);
                                            fragment.setArguments(bundle);
                                            ft.addToBackStack("backsell");
                                            ft.replace(R.id.container, fragment);
                                            ft.commit();
                                        }else{
                                            Toast.makeText(mContext, "Post unavailable",Toast.LENGTH_LONG).show();

                                        }
                                    }catch (Exception e){
                                        Toast.makeText(mContext, "Post unavailable",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                    //startActivity(new Intent(LikesActivity.this, AccountSettingsActivity.class));
                }
            }
        });

        lv.setAdapter(adapter);

        setupBottomNavigationView();
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

    private String getTimestampDifference(notification Notification){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = Notification.getTime();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    FragmentManager mFragmentManager = getSupportFragmentManager();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void multi_show(String url, String image_id, String[] img_url, String type)
    {
        Bundle bundle = new Bundle();
        bundle.putString("path", url);
        bundle.putStringArray("url",img_url);
        bundle.putString("id",image_id);
        bundle.putString("type",type);
        ViewMultiplePostFragment view_post = new ViewMultiplePostFragment();
        view_post.setArguments(bundle);
        mFragmentManager.beginTransaction().add(R.id.container, view_post).addToBackStack("ViewMultplePostFragment").commit();

    }
}
