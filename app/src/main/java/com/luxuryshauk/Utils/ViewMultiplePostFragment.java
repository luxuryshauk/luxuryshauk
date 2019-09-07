package com.luxuryshauk.Utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luxuryshauk.Home.AddActivity;
import com.luxuryshauk.Home.HomeActivity;
import com.luxuryshauk.Profile.ProfileActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.models.Comment;
import com.luxuryshauk.models.Like;
import com.luxuryshauk.models.Photo;
import com.luxuryshauk.models.User;
import com.luxuryshauk.models.UserAccountSettings;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by User on 8/12/2017.
 */

public class ViewMultiplePostFragment extends Fragment implements FragmentOnBackClickInterface{



    private static final String TAG = "ViewMultplePostFragment";

    private String[] images_urls = new String[10];

    public interface OnCommentThreadSelectedListener{
        void onCommentThreadSelectedListener(Photo photo);
    }
    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    public ViewMultiplePostFragment(){
        super();
        setArguments(new Bundle());
    }

    public void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    String[] getImage = {"https://firebasestorage.googleapis.com/v0/b/instaaselldbase.appspot.com/o/photos%2Fusers%2F6IcgXH23AvbmqrH08zrnqHzy95q2%2Fphoto78?alt=media&token=db939ba7-cc0d-452b-aeb0-ca25bd8ad086%5D",null};
    String path_to_image;
    String[] items = new String[10];
    MainFeedListAdapter.ViewHolder mHolder;
    public String photouid;

    //widgets
    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView mBackLabel, mCaption, mUsername, mTimestamp, mLikes, mComments;
    private ImageView mBackArrow, mEllipses, mHeartRed, mHeartWhite, mProfileImage, mComment;

    long price_m;


    //vars
    public Photo mPhoto;
    private int mActivityNumber = 0;
    private String photoUsername = "";
    private String profilePhotoUrl = "";
    private UserAccountSettings mUserAccountSettings;
    private GestureDetector mGestureDetector;
    private Heart mHeart;
    private Boolean mLikedByCurrentUser;
    private StringBuilder mUsers;
    private String mLikesString = "";
    private User mCurrentUser;
    private ImageView save_to_my_store;
    private TextView price;
    private Button Buy_btn;
    private String[] urls;
    private ImageView share_btn;
    private int counter;
    private String[] img__url;
    private TextView count_d;
    ListView listView=null;
    Context mContext;
    int count = 0;
    ImageView[] sharePhoto = new ImageView[10];
    private Bundle bundle;
    private TextView moreless;
    public boolean more = true;
    int linecount;

    ListView rListView;
    String notKey = FirebaseDatabase.getInstance().getReference().push().getKey();
    long t_price;


    public void init(){
        //try{
        //mPhoto = getPhotoFromBundle();
        //UniversalImageLoader.setImage(urls[0], mPostImage, null, "");
        //mActivityNumber = getActivityNumFromBundle();



        final String photo_id = bundle.getString("id");
        Log.d(TAG, "init: bundle photo id = " + photo_id);

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_photos))
                .orderByChild(getString(R.string.field_photo_id))
                .equalTo(photo_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Photo newPhoto = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    try {


                        newPhoto.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        newPhoto.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        newPhoto.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                        photouid = newPhoto.getUser_id().toString();
                        Log.d(TAG, "onDataChange: photo uid = " + photouid);
                        alertBoxGenerate(photouid);
                        List<Comment> commentsList = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            commentsList.add(comment);
                        }
                        newPhoto.setComments(commentsList);

                        mPhoto = newPhoto;

                        String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (currentuid.equals(mPhoto.getUser_id())) {
                            Buy_btn.setVisibility(View.INVISIBLE);
                            save_to_my_store.setVisibility(View.INVISIBLE);
                        } else {
                            Buy_btn.setVisibility(View.VISIBLE);
                            save_to_my_store.setVisibility(View.VISIBLE);
                        }
                        mCaption.setText(mPhoto.getCaption());

                        mCaption.post(new Runnable() {
                            @Override
                            public void run() {
                                linecount = mCaption.getLineCount();
                            }
                        });

                        getCurrentUser();
                        getPhotoDetails();
                        //getLikesString();
                    }catch (Exception e){}

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });

//        }catch (NullPointerException e){
//            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
//        }
    }


    private void collectURL(String[] urls, final long count, View view) {
        int countt = (int)count;
        images_urls = new String[countt];
        System.out.println("Let see how it goes  = "+ Arrays.toString(urls));
        for(int l = 0;l<count;l++)
        {
            images_urls[l] = urls[l];
            Log.d(TAG, "collectURL: l = "+l + " image url = "+images_urls[l]);
        }
        System.out.println("Let see how it goes  = "+ Arrays.toString(images_urls));
        ViewPager mViewPager = view.findViewById(R.id.post_image);
        count_d = view.findViewById(R.id.counter);
        System.out.println("Let see how it goes final = "+ Arrays.toString(images_urls));
        MultiImageViewPager mImageView = new MultiImageViewPager(getContext(), images_urls);
        count_d.setText("1 of "+count);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                //System.out.println("Current position=="+num);
                count_d.setText((arg0+1)+" of "+ count);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

                System.out.println("onPageScrolled");
            }

            @Override
            public void onPageScrollStateChanged(int num) {
                // TODO Auto-generated method stub


            }
        });

        mViewPager.setAdapter(mImageView);


        //System.out.println("Urls = "+urls.toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_view_multi_post, container, false);
        view.bringToFront();
//        mPostImage = (SquareImageView) view.findViewById(R.id.post_image);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mBackLabel = (TextView) view.findViewById(R.id.tvBackLabel);
        mCaption = (TextView) view.findViewById(R.id.image_caption);
        mUsername = (TextView) view.findViewById(R.id.username);
        mTimestamp = (TextView) view.findViewById(R.id.image_time_posted);
        mEllipses = (ImageView) view.findViewById(R.id.ivEllipses);
        mHeartRed = (ImageView) view.findViewById(R.id.image_heart_red);
        mHeartWhite = (ImageView) view.findViewById(R.id.image_heart);
        mProfileImage = (ImageView) view.findViewById(R.id.profile_photo);
        mLikes = (TextView) view.findViewById(R.id.image_likes);
        mComment = (ImageView) view.findViewById(R.id.speech_bubble);
        mComments = (TextView) view.findViewById(R.id.image_comments_link);
        price = (TextView) view.findViewById(R.id.price_display);
        Buy_btn = (Button) view.findViewById(R.id.buyButton);
        save_to_my_store = (ImageView) view.findViewById(R.id.btn_save);
        share_btn = view.findViewById(R.id.share_btn);
        moreless = view.findViewById(R.id.moreless);
        mContext = getContext();
        listView = new ListView(mContext);
        bundle = getArguments();
        urls = bundle.getStringArray("url");
        mContext = getContext();
        img__url = new String[10];
        rListView = new ListView(mContext);


        FirebaseAuth.AuthStateListener mAuthListener;
        FirebaseDatabase mFirebaseDatabase;
        final DatabaseReference myRef;
        FirebaseMethods mFirebaseMethods;
        StorageReference mStorageReference;
        String userID;


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        //mCaption.setText(mPhoto.getCaption());



        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();

        }
        try {
            if(bundle.getString("type").equals("2")) {

                DatabaseReference r = (myRef.child(ViewMultiplePostFragment.this.getString(R.string.dbname_photos))
                        .child(bundle.getString("id")).child("image_path"));
                r.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            long childno;
                            if(count<10) {
                                img__url[count] = childDataSnapshot.getValue().toString();
                                childno = dataSnapshot.getChildrenCount();
                                Log.d(TAG, "multiple test key = " + childDataSnapshot.getKey());
                                Log.d(TAG, "multiple test url  = " + childDataSnapshot.getValue());
                                Log.d(TAG, "onDataChange: url = " + img__url[count] + " count = " + count + " child count = " + childno);
                                count++;
                                if (count == childno) {
                                    collectURL(img__url, count, view);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else
            {
                Log.d(TAG, "onCreateView: url = "+bundle.getStringArray("url") + " length = "+bundle.getStringArray("url").length);
                collectURL(bundle.getStringArray("url"),bundle.getStringArray("url").length,view);
            }
        }catch (Exception e)
        {

        }
        price = (TextView) view.findViewById(R.id.price_display);

        try {

            DatabaseReference s = (myRef.child(ViewMultiplePostFragment.this.getString(R.string.dbname_photos))

                    .child(bundle.getString("id")).child("price"));

            s.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        price_m = (dataSnapshot.getValue(Long.class));
                        Log.d(TAG, "onDataChange: price = " + price);
                        price.setText("₹ " + String.valueOf(price_m));
                    }catch (Exception e){}

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e)
        {

        }


        sharePhoto[0] = (ImageView) view.findViewById(R.id.sp0);
        sharePhoto[1] = (ImageView) view.findViewById(R.id.sp1);
        sharePhoto[2] = (ImageView) view.findViewById(R.id.sp2);
        sharePhoto[3] = (ImageView) view.findViewById(R.id.sp3);
        sharePhoto[4] = (ImageView) view.findViewById(R.id.sp4);
        sharePhoto[5] = (ImageView) view.findViewById(R.id.sp5);
        sharePhoto[6] = (ImageView) view.findViewById(R.id.sp6);
        sharePhoto[7] = (ImageView) view.findViewById(R.id.sp7);
        sharePhoto[8] = (ImageView) view.findViewById(R.id.sp8);
        final ImageLoader imageLoader = ImageLoader.getInstance();
        path_to_image= bundle.getString("path");
        try {

            if (!path_to_image.isEmpty()) {
                getImage = path_to_image.split(", ");
            } else {
                getImage = new String[]{"null", "null"};
            }
        }catch (Exception e){}

        int counting = 0;
        Log.d(TAG, "onDataChange: getimagepath() = " + path_to_image + "String[] getImage = " + getImage);
        for (String url : getImage) {
            try {
                if (!url.equals("[null")) {
                    imageLoader.displayImage(url, sharePhoto[counting]);
                    counting++;
                }
            }catch (Exception e){}
        }

            items = new String[3];
            items[0] = "Share";
            items[1] = "Unfollow";
            items[2] = "Report";
            save_to_my_store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("photos")
                            .child(mPhoto.getPhoto_id());
                    dbref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Photo newPhoto = new Photo();
                            Bundle b = new Bundle();


                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            long price = (long)objectMap.get("price");
                            Log.d(TAG, "onDataChange: save to my store price = " + price);
                            Log.d(TAG, "onDataChange: photo model hashmap " + objectMap.toString());
                            newPhoto.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());
                            newPhoto.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());
                            newPhoto.setPhoto_id(objectMap.get(mContext.getString(R.string.field_photo_id)).toString());
                            newPhoto.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
                            newPhoto.setDate_created(objectMap.get(mContext.getString(R.string.field_date_created)).toString());
                            newPhoto.setImage_path(objectMap.get(mContext.getString(R.string.field_image_path)).toString());
                            //newPhoto.setPrice(price);
                            newPhoto.setType(Integer.valueOf(objectMap.get("type").toString()));
                            Log.d(TAG, "onDataChange: retrieved photo model = " +newPhoto.toString());


                            Intent intent = new Intent(getContext(), AddActivity.class);
                            b.putParcelable("photo",newPhoto);
                            //b.putLong("price", price);
                            //intent.putExtra("photo",newPhoto);
                            //intent.putExtra("price", price);
                            intent.putExtras(b);
                            //intent.putExtra("price",price);
                            getContext().startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });




        mHeartRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference
                        .child(getString(R.string.dbname_photos))
                        .child(mPhoto.getPhoto_id())
                        .child(getString(R.string.field_likes));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                            String keyID = singleSnapshot.getKey();

                            //case1: Then user already liked the photo
                            if(mLikedByCurrentUser &&
                                    singleSnapshot.getValue(Like.class).getUser_id()
                                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                                myRef.child(getString(R.string.dbname_photos))
                                        .child(mPhoto.getPhoto_id())
                                        .child(getString(R.string.field_likes))
                                        .child(keyID)
                                        .removeValue();
///
                                myRef.child(getString(R.string.dbname_user_photos))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mPhoto.getPhoto_id())
                                        .child(getString(R.string.field_likes))
                                        .child(keyID)
                                        .removeValue();
                                myRef.child("notification").child(mPhoto.getUser_id()).child(notKey).removeValue();


                                mHeart.toggleLike();
                                getLikesString();
                            }
                            //case2: The user has not liked the photo
                            else if(!mLikedByCurrentUser){
                                //add new like

                                addNewLike();
                                break;
                            }


                        }
                        if(!dataSnapshot.exists()){
                            //add new like
                            addNewLike();
                            String notKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                            String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            SetNotification sn = new SetNotification();
                            sn.notify(currentuid,notKey,"unseen",getTimestamp(),"1",mHolder.photo.getUser_id(),mHolder.photo.getPhoto_id());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mHeartWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference
                        .child(getString(R.string.dbname_photos))
                        .child(mPhoto.getPhoto_id())
                        .child(getString(R.string.field_likes));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                            String keyID = singleSnapshot.getKey();

                            //case1: Then user already liked the photo
                            try {
                                if (mLikedByCurrentUser &&
                                        singleSnapshot.getValue(Like.class).getUser_id()
                                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                    myRef.child(getString(R.string.dbname_photos))
                                            .child(mPhoto.getPhoto_id())
                                            .child(getString(R.string.field_likes))
                                            .child(keyID)
                                            .removeValue();
///
                                    myRef.child(getString(R.string.dbname_user_photos))
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(mPhoto.getPhoto_id())
                                            .child(getString(R.string.field_likes))
                                            .child(keyID)
                                            .removeValue();
                                    myRef.child("notification").child(mPhoto.getUser_id()).child(notKey).removeValue();


                                    mHeart.toggleLike();
                                    getLikesString();
                                }
                                //case2: The user has not liked the photo
                                else if (!mLikedByCurrentUser) {
                                    //add new like

                                    addNewLike();
                                    break;
                                }
                            }catch (Exception E){}


                        }
                        if(!dataSnapshot.exists()){
                            //add new like
                            addNewLike();
                            String notKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                            String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            SetNotification sn = new SetNotification();
                            sn.notify(currentuid,notKey,"unseen",getTimestamp(),"1",mHolder.photo.getUser_id(),mHolder.photo.getPhoto_id());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });




        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareIt share = new ShareIt();
                if(mPhoto.getImage_path().contains(",")) {

                    String[] imageurls = mPhoto.getImage_path().split(", ");
                    String[] modifiedArray_f = Arrays.copyOfRange(imageurls, 1, imageurls.length);
                    share.shareit(modifiedArray_f, sharePhoto, mContext, price_m, mPhoto.getCaption(), mPhoto.getPhoto_id());
                }else{
                    String[] imgurl = {mPhoto.getImage_path()};
                    share.shareit(imgurl, sharePhoto, mContext, price_m, mPhoto.getCaption(), mPhoto.getPhoto_id());
                }


            }
        });
            final DatabaseReference s = (FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.dbname_photos))
                    .child(bundle.getString("id")).child("price"));

            Buy_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    s.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final long price = (dataSnapshot.getValue(Long.class));
                            String currentid = FirebaseAuth.getInstance().getCurrentUser().getUid();


                           buy_now(mPhoto.getPhoto_id(),currentid);

//                            Log.d(TAG, "onDataChange: going to buy now");
//                            Log.d(TAG, "onDataChange: user id should" + mPhoto.getUser_id());
//                            String seller_id = mPhoto.getUser_id();
//                            String current_user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
//                            String userID = mAuth.getCurrentUser().getUid();
//                            String userN = mCurrentUser.getUsername();
//                            String id = mPhoto.getPhoto_id();
//                            String capton = mPhoto.getCaption();
//                            String img_url = mPhoto.getImage_path();
//                            ((HomeActivity) mContext).buy_now(id, userN, price, current_user, userID, seller_id, img_url, capton);
                            //((HomeActivity)mContext).hideLayout();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

        mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to profile of: " +
                        mCurrentUser.getUsername());

                try {
                    Log.d(TAG, "onClick: from search = " + bundle.getString("fromsearch"));

//                    if(bundle.getString("fromsearch").equals("1"))
//                    {
                        FirebaseDatabase.getInstance().getReference().child("users").child(mPhoto.getUser_id()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                User user = dataSnapshot.getValue(User.class);
                                Intent intent = new Intent(mContext, ProfileActivity.class);
                                intent.putExtra(mContext.getString(R.string.calling_activity),
                                        mContext.getString(R.string.home_activity));
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(mContext.getString(R.string.intent_user), user);
                                mContext.startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//                    }

                }catch (Exception e){
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra(mContext.getString(R.string.calling_activity),
                            mContext.getString(R.string.home_activity));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(mContext.getString(R.string.intent_user), bundle.getParcelable("user"));
                    mContext.startActivity(intent);
                }
            }
        });

        mHeart = new Heart(mHeartWhite, mHeartRed);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());

        setupFirebaseAuth();
        setupBottomNavigationView();



        mEllipses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,"menu button hit", Toast.LENGTH_LONG).show();
                showDialogListView(v);
            }
        });

        return view;
    }




    @Override
    public void onResume() {
        super.onResume();
        if(isAdded()){
            init();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }

    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    try {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(getString(R.string.dbname_users))
                                .orderByChild(getString(R.string.field_user_id))
                                .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                    Log.d(TAG, "onDataChange: found like: " +
                                            singleSnapshot.getValue(User.class).getUsername());

                                    mUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                    mUsers.append(",");
                                }

                                String[] splitUsers = mUsers.toString().split(",");

                                if (mUsers.toString().contains(mCurrentUser.getUsername() + ",")) {
                                    mLikedByCurrentUser = true;
                                } else {
                                    mLikedByCurrentUser = false;
                                }

                                int length = splitUsers.length;
                                if (length == 0) {
                                    mLikesString = "0 Likes";
                                } else if (length == 1) {
                                    mLikesString = "Liked by " + splitUsers[splitUsers.length - 1];
                                } else if (length == 2) {
                                    mLikesString = "Liked by " + splitUsers[splitUsers.length - 1]
                                            + " and " + splitUsers[splitUsers.length - 2];
//                                        } else if (length == 3) {
//                                            holder.likesString = "Liked by " + splitUsers[0]
//                                                    + ", " + splitUsers[1]
//                                                    + " and " + splitUsers[2];

//                                        } else if (length == 4) {
//                                            holder.likesString = "Liked by " + splitUsers[0]
//                                                    + ", " + splitUsers[1]
//                                                    + ", " + splitUsers[2]
//                                                    + " and " + splitUsers[3];
                                } else if (length > 2) {
                                    mLikesString = "Liked by " + splitUsers[splitUsers.length - 1]
                                            + ", " + splitUsers[splitUsers.length - 2]
//                                                    + ", " + splitUsers[2]
                                            + " and " + (splitUsers.length - 2) + " others";
                                }
                                Log.d(TAG, "onDataChange: likes string: " + mLikesString);
                                setupWidgets();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }catch (Exception e){}
                }
                if(!dataSnapshot.exists()){
                    mLikesString = "0 Likes";
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getCurrentUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    mCurrentUser = singleSnapshot.getValue(User.class);
                }
                getLikesString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return true;
//        }


        //public boolean onDoubleTap(MotionEvent e) {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected.");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.dbname_photos))
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();

                        //case1: Then user already liked the photo
                        if(mLikedByCurrentUser &&
                                singleSnapshot.getValue(Like.class).getUser_id()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            myRef.child(getString(R.string.dbname_photos))
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();
///
                            myRef.child(getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mPhoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mHeart.toggleLike();
                            getLikesString();
                        }
                        //case2: The user has not liked the photo
                        else if(!mLikedByCurrentUser){
                            //add new like
                            addNewLike();
                            break;
                        }


                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void addNewLike(){
        Log.d(TAG, "addNewLike: adding new like");

        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(getString(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mHeart.toggleLike();
        getLikesString();
    }

    private void getPhotoDetails(){
        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    mUserAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
                }
                //setupWidgets();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }



    private void setupWidgets(){
        String timestampDiff = getTimestampDifference();
        if(!timestampDiff.equals("0")){
                mTimestamp.setText(timestampDiff + " DAYS AGO");
            }else{
                mTimestamp.setText("TODAY");
        }
        try {
            Log.d(TAG, "setupWidgets: mUserAccountSettings = " + mUserAccountSettings.toString());
            UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mProfileImage, null, "");

            mUsername.setText(mUserAccountSettings.getUsername());
            mLikes.setText(mLikesString);
            mCaption.setText(mPhoto.getCaption());

            if (mPhoto.getComments().size() > 0) {
                mComments.setText("View all " + mPhoto.getComments().size() + " comments");
            } else {
                mComments.setText("0 Comments");
            }
        }catch (Exception e){
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbname_user_account_settings))
                    .child(mPhoto.getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(DataSnapshot dataSnapshot) {

                                                             mUserAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                                                             Log.d(TAG, "setupWidgets: mUserAccountSettings = " + mUserAccountSettings.toString());
                                                             UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mProfileImage, null, "");

                                                             mUsername.setText(mUserAccountSettings.getUsername());
                                                             mLikes.setText(mLikesString);
                                                             mCaption.setText(mPhoto.getCaption());

                                                             if (mPhoto.getComments().size() > 0) {
                                                                 mComments.setText("View all " + mPhoto.getComments().size() + " comments");
                                                             } else {
                                                                 mComments.setText("0 Comments");
                                                             }
                                                         //setupWidgets();
                                                     }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
                                                 });


        }


        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to comments thread");
                try {
                    final String photo_id = bundle.getString("id");


                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.dbname_photos))
                            .orderByChild(getString(R.string.field_photo_id))
                            .equalTo(photo_id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                                Photo newPhoto = new Photo();
                                Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                newPhoto.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                                newPhoto.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                                newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                                newPhoto.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                                newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                                newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                                photouid = newPhoto.getUser_id().toString();
                                Log.d(TAG, "onDataChange: photo uid = " + photouid);
                                alertBoxGenerate(photouid);
                                List<Comment> commentsList = new ArrayList<Comment>();
                                for (DataSnapshot dSnapshot : singleSnapshot
                                        .child(getString(R.string.field_comments)).getChildren()){
                                    Comment comment = new Comment();
                                    comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                    comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                    comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                    commentsList.add(comment);
                                }
                                newPhoto.setComments(commentsList);

                                mPhoto = newPhoto;
                                onCommentThreadSelected(mPhoto,getActivity().getLocalClassName());
                                //mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: query cancelled.");
                        }
                    });
                    //mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mPhoto);
                }catch (Exception e){}

            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");

                    final String photo_id = bundle.getString("id");


                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.dbname_photos))
                            .orderByChild(getString(R.string.field_photo_id))
                            .equalTo(photo_id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                                Photo newPhoto = new Photo();
                                Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                newPhoto.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                                newPhoto.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                                newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                                newPhoto.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                                newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                                newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                                photouid = newPhoto.getUser_id().toString();
                                Log.d(TAG, "onDataChange: photo uid = " + photouid);
                                alertBoxGenerate(photouid);
                                List<Comment> commentsList = new ArrayList<Comment>();
                                for (DataSnapshot dSnapshot : singleSnapshot
                                        .child(getString(R.string.field_comments)).getChildren()){
                                    Comment comment = new Comment();
                                    comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                    comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                    comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                    commentsList.add(comment);
                                }
                                newPhoto.setComments(commentsList);

                                mPhoto = newPhoto;
                                onCommentThreadSelected(mPhoto,getActivity().getLocalClassName());
                                }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: query cancelled.");
                        }
                    });



            }
        });

        if(mLikedByCurrentUser){
            mHeartWhite.setVisibility(View.GONE);
            mHeartRed.setVisibility(View.VISIBLE);
            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch detected.");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
        else{
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeartRed.setVisibility(View.GONE);
            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch detected.");
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }


    }


    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mPhoto.getDate_created();
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
     * retrieve the activity number from the incoming bundle from profileActivity interface
     * @return
     */
    private int getActivityNumFromBundle(){
        Log.d(TAG, "getActivityNumFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        }else{
            return 0;
        }
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private Photo getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return null;
        }
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        try {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(getActivity(), getActivity(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }catch (Exception e){}
    }

       /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }
    AlertDialog.Builder builder;
    AlertDialog dialog;
    public void showDialogListView(View view)
    {
        builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setView(listView);

        dialog = builder.create();
        try {
            if (listView.getParent() != null) {
                ((ViewGroup) listView.getParent()).removeView(listView);
            }
        }catch (Exception e){}
        dialog.show();
    }

    public void showReportListView()
    {
        builder.setCancelable(true);
        builder.setView(null);
        builder.setView(rListView);
        dialog = builder.create();
        try {
            if (rListView.getParent() != null) {
                ((ViewGroup) rListView.getParent()).removeView(rListView);
            }
        }catch (Exception e){}
        //  dialog.setCancelable(true);
        try {
            dialog.show();
        }catch (Exception e){

        }
    }




    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onBackPressed() {
        getView().setVisibility(View.GONE);

    }

    public ViewGroup vg;

    public void alertBoxGenerate(String uid)
    {
        Log.d(TAG, "onCreateView: photo uid = " + uid);
        if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) {
            items = new String[2];
            //items[0] = "Share";
            items[1] = "Unfollow";
            items[0] = "Report";
        }else{
            items = new String[2];
            //items[0] = "Share";
            items[1] = "Report";
            items[0] = "Delete";
        }
        Log.d(TAG, "alertBoxGenerate: photo uid = " + photouid);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,R.layout.list_item,R.id.menu_item, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vg = (ViewGroup)view;
                TextView txt = (TextView)vg.findViewById(R.id.menu_item);
                Toast.makeText(mContext,txt.getText().toString(), Toast.LENGTH_LONG).show();
                switch (txt.getText().toString()){
                    case "Unfollow":
                        Log.d(TAG, "onItemClick: follow/unfollow with dialog box current user : "+FirebaseAuth.getInstance().getCurrentUser().getUid()+" user : "+photouid);
                        FirebaseDatabase.getInstance().getReference()
                                .child("following")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(photouid)
                                .removeValue();

                        FirebaseDatabase.getInstance().getReference()
                                .child("followers")
                                .child(photouid)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue();
                        break;
                    case "Report":
                        //((HomeActivity)mContext).email(mmPhoto.getPhoto_id(), user.getUsername());
                        String[] reportList = new String[4];
                        reportList[0] = "Abusive Content";
                        reportList[1] = "Fraud";
                        reportList[2] = "Harassment";
                        reportList[3] = "Illegal";
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,R.layout.list_item,R.id.menu_item, reportList);
                        rListView.setAdapter(adapter);
                        rListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                vg = (ViewGroup)view;
                                TextView txt = (TextView)vg.findViewById(R.id.menu_item);
                                String url = "";
                                String[] urls = new String[10];
                                if(mPhoto.getImage_path().contains(","))
                                {
                                    urls = mPhoto.getImage_path().split(", ");
                                }
                                else{
                                    url = mPhoto.getImage_path();
                                }
                                String emailBody = "Post id = " + mPhoto.getPhoto_id() +
                                        "\nPost pic URL = ";
                                //+ mmPhoto.getImage_path() +;
                                int countimgurl = 1;
                                if(mPhoto.getImage_path().contains(",")) {
                                    for (String u : urls) {
                                        if(!u.equals("[null") && !u.isEmpty()) {
                                            emailBody += "\n\t" + countimgurl + " : " + u;
                                            countimgurl++;
                                        }
                                    }
                                }
                                else{
                                    emailBody += "\n\t 1 : " + url;
                                }
                                emailBody += "\nPost caption = " + mPhoto.getCaption() +
                                        "\nPost date = " + mPhoto.getDate_created() +
                                        "\nPosted by username = " + mUserAccountSettings.getUsername() +
                                        "\nUser ID = " + mUserAccountSettings.getUser_id() +
                                        "\nReported type = " + txt.getText().toString() +
                                        "\nReported by = " + mCurrentUser.getUsername().toString();

                                HomeActivity ha = new HomeActivity();
                                ha.sendMessage(emailBody);
                                Toast.makeText(mContext,"Post reported successfully",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        dialog.dismiss();
                        showReportListView();
                        break;
                    case "Delete":
                        FirebaseDatabase.getInstance().getReference()
                                .child("user_photos")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mPhoto.getPhoto_id())
                                .setValue(null);

                        FirebaseDatabase.getInstance().getReference()
                                .child("photos")
                                .child(mPhoto.getPhoto_id())
                                .setValue(null);
                        Toast.makeText(getContext(),"Post Deleted",Toast.LENGTH_LONG);
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                        dialog.dismiss();
                        break;
                    case "Share":
                        ArrayList<Uri> file = new ArrayList<>();
                        Log.d(TAG, "onClick: sharing url : "+ mPhoto.getImage_path() +" image type = " + mPhoto.getType()
                                + " photoid = " + mPhoto.getPhoto_id());
                        ShareIt share = new ShareIt();
                        if(mPhoto.getImage_path().contains(",")) {

                            String[] imageurls = mPhoto.getImage_path().split(", ");
                            String[] modifiedArray_f = Arrays.copyOfRange(imageurls, 1, imageurls.length);
                            share.shareit(modifiedArray_f, sharePhoto, mContext, price_m, mPhoto.getCaption(), mPhoto.getPhoto_id());
                        }else{
                            String[] imgurl = {mPhoto.getImage_path()};
                            share.shareit(imgurl, sharePhoto, mContext, price_m, mPhoto.getCaption(), mPhoto.getPhoto_id());
                        }
                        dialog.dismiss();
//                                            if(imageurls.length>1) {
//
//                                                for (int st = 0; st < imageurls.length; st++) {
//                                                    if(!imageurls[st].contains("null")) {
//                                                        imageurls[st] = imageurls[st].replace("]","");
//                                                        try {
//                                                            Log.d(TAG, "onDataChange: image url at final phase " + imageurls[st]);
//                                                            Picasso.get().load(imageurls[st]).into(sharePhoto[st]);
//                                                            //URL url = new URL(imageurls[st]);
//                                                            //b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                                                            //String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b, "Image Description", null);
//                                                            d = sharePhoto[st].getDrawable();
//                                                            b = ((BitmapDrawable) d).getBitmap();
//                                                            String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b, "Image Description", null);
//                                                            Uri uri = Uri.parse(path);
//                                                            file.add(uri);
//                                                        }catch (Exception e){
//                                                            Log.d(TAG, "onDataChange: failed to load " + imageurls[st]);
//                                                        }
//                                                    }
//                                                }
//                                            }else{
//                                                String imgurl= imageurls[0];
//                                                if(!imgurl.equals("null")) {
//                                                    imgurl = imgurl.replace("]","");
//                                                    Log.d(TAG, "onItemClick: image url at final phase " + imgurl);
//                                                    try {
//                                                        Picasso.get().load(imgurl).into(sharePhoto[0]);
//                                                        d = sharePhoto[0].getDrawable();
//                                                        b = ((BitmapDrawable) d).getBitmap();
//                                                        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b, "Image Description", null);
//                                                        Uri uri = Uri.parse(path);
//                                                        file.add(uri);
//                                                    }catch (Exception e){}
//                                                }
//                                            }
//                        Drawable[] mDrawable = new Drawable[getImage.length];
//                        Bitmap[] mBitmap = new Bitmap[getImage.length];
//                        for(int n=0;n<getImage.length;n++) {
//                            try {
//                                mDrawable[n] = sharePhoto[n].getDrawable();
//                                mBitmap[n] = ((BitmapDrawable) mDrawable[n]).getBitmap();
//                                Log.d(TAG, "onClick: share test = urls = " + mBitmap[n].toString());
//                                String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), mBitmap[n], "Image Description", null);
//                                Uri uri = Uri.parse(path);
//                                file.add(uri);
//                            }catch (Exception e){}
//
//                        }
//                                            try {
//                                                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//                                                intent.setType("image/jpeg");
//
//                                                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, file);
//
//                                                intent.putExtra(Intent.EXTRA_TEXT, mmPhoto.getCaption()+" \nPrice : ₹ "+ price +
//                                                        "\nBuy this : https://collectmoney.in/app/?id=" + mmPhoto.getPhoto_id());
//                                                mContext.startActivity(intent);
//                                            }catch (NullPointerException e)
//                                            {}
//                        Drawable[] mDrawable = new Drawable[getImage.length];
//                        Bitmap[] mBitmap = new Bitmap[getImage.length];
//                        for(int n=0;n<getImage.length;n++) {
//                            try {
//                                mDrawable[n] = sharePhoto[n].getDrawable();
//                                mBitmap[n] = ((BitmapDrawable) mDrawable[n]).getBitmap();
//                                Log.d(TAG, "onClick: share test = urls = " + mBitmap[n].toString());
//                                String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), mBitmap[n], "Image Description", null);
//                                Uri uri = Uri.parse(path);
//                                file.add(uri);
//                            }catch (Exception e){}
//
//                        }
//                        try {
//                            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//                            intent.setType("image/jpeg");
//
//                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, file);
//                            final DatabaseReference s = (FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.dbname_photos))
//                                    .child(mmPhoto.getPhoto_id()).child("price"));
//                            s.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                                    t_price = dataSnapshot.getValue(long.class);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                            intent.putExtra(Intent.EXTRA_TEXT, mPhoto.getCaption()+" \nPrice : ₹ "+ t_price +
//                                    "\nBuy this : https://collectmoney.in/app/?id=" + mPhoto.getPhoto_id());
//                            mContext.startActivity(intent);
//                        }catch (NullPointerException e)
//                        {}

                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
    }
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    public void buy_now(String photo_id, String buyer)
    {
        Bundle b = new Bundle();
        b.putString("photoid",photo_id);
        b.putString("buyer",buyer);
        Intent buy = new Intent(getActivity(), PlaceOrder.class);
        buy.putExtras(b);
        startActivity(buy);
    }

}





















