package com.luxuryshauk.Utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.luxuryshauk.Home.AddActivity;
import com.luxuryshauk.Home.HomeActivity;
import com.luxuryshauk.Profile.ProfileActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.models.Comment;
import com.luxuryshauk.models.Like;
import com.luxuryshauk.models.Photo;
import com.luxuryshauk.models.User;
import com.luxuryshauk.models.UserAccountSettings;
import com.luxuryshauk.models.notification;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnLoadMoreItemsListener{
        void onLoadMoreItems();
    }
    OnLoadMoreItemsListener mOnLoadMoreItemsListener;


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Photo> productList;

    boolean isImageFitToScreen;
    private LayoutInflater mInflater;
    private int mLayoutResource,i;
//    private Context mCtx;
    private DatabaseReference mReference;
    private String currentUsername = "";
    private int counter;
//    private ImageView imageView;
    private String fullScreenInd;
    private ArrayList<String> img_urls = new ArrayList<>();
    private String[] img__url;
    String[] items, items1;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    ListView listView;
    ListView listView_d;
    ListView rListView;
    ArrayList<Bitmap> files = new ArrayList<Bitmap>();
    String path_to_image;
    ViewGroup vg;
    long t_price;
    ArrayList<Uri> file;

    boolean more = true;
    int refreshcount = 0;
    Boolean likeRefreshed = false;
    Boolean notification_delete = false;
    Boolean canDelete = false;
    String photoowner;
    String TAG = "ProductAdapter";
    private OnLoadMoreListener onLoadMoreListener;
    boolean savetostoreclicked = false;


    //getting the context and product list with constructor
    public ProductAdapter(Context mCtx, List<Photo> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder

            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.layout_mainfeed_listitem, null);
            return new ProductViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {

        items = new String[2];
        items[1] = "Report";
        items[0] = "Unfollow";
        items1 = new String[2];
        items1[1] = "Report";
        items1[0] = "Delete";
        listView = new ListView((HomeActivity)mCtx);
        rListView = new ListView((HomeActivity)mCtx);
        listView_d = new ListView((HomeActivity)mCtx);
        SendEmailAsyncTask email = new SendEmailAsyncTask();
        photoowner = productList.get(position).getUser_id();
        holder.photo = productList.get(position);
        holder.detector = new GestureDetector(mCtx, new ProductAdapter.GestureListener(holder));
        holder.users = new StringBuilder();
        holder.heart = new Heart(holder.heartWhite, holder.heartRed);

        //getting the product of the specified position
        final Photo product = productList.get(position);
        getCurrentUsername();
        //binding the data with the viewholder views
        if(!product.getImage_path().contains(",")) {
            try {
//                Drawable drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(getItem(position).getImage_path().getBytes(), 0, getItem(position).getImage_path().length()));
//                URL url = new URL("http://....");
//                Bitmap bitmap = BitmapFactory.decodeStream(getItem(position).getImage_path().openConnection().getInputStream());
//                Bitmap x;
//
//                HttpURLConnection connection = (HttpURLConnection) new URL(getItem(position).getImage_path()).openConnection();
//                connection.connect();
//                InputStream input = connection.getInputStream();
//
//                x = BitmapFactory.decodeStream(input);
//                Drawable drawable = new BitmapDrawable(x);
//
//                 image.setImageDrawable(drawable);
//                ImageLoader.getInstance().image.setDrawingCacheEnabled(true);
//                ImageLoader.getInstance().displayImage(getItem(position).getImage_path(),  image,imageOptions);




//                Picasso.get().load(getItem(position).getImage_path()).into( image);

            }catch (Exception e){}


      Picasso.get().load(productList.get(position).getImage_path()).into(holder.imageView);
//            holder.imageView.setImageBitmap(ImageNicer.decodeSampledBitmapFromResource(mCtx.getResources(), productList.get(position).getImage_path(), 100,100));
//            Glide.with(mCtx).load(productList.get(position).getImage_path()).into(holder.imageView);
//            Glide

//        cacheImage(getItem(position).getImage_path());
        }
        else if(productList.get(position).getImage_path().contains(",")){
//            imageLoader1 = ImageLoader.getInstance();
            String[] urls = productList.get(position).getImage_path().split(", ");
            try {
//                Drawable drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(urls[1].getBytes(), 0, urls[1].length()));
//                 image.setImageDrawable(drawable);
//                 image.setDrawingCacheEnabled(true);
//                Bitmap x;
//
//                HttpURLConnection connection = (HttpURLConnection) new URL(urls[1]).openConnection();
//                connection.connect();
//                InputStream input = connection.getInputStream();
//
//                x = BitmapFactory.decodeStream(input);
//                Drawable drawable = new BitmapDrawable(x);
//
//                 image.setImageDrawable(drawable);
//                 image.setDrawingCacheEnabled(true);
//                ImageLoader.getInstance().displayImage(urls[1],  image,imageOptions);

            }catch (Exception e){}
//
//            Picasso.get().load(urls[1]).into( imageView);
//            Glide.with(mCtx).load(urls[1]).into(holder.imageView);
            Picasso.get().load(urls[1]).into(holder.imageView);



//            cacheImage(urls[1]);
//            imageLoader1.displayImage(urls[1],  image);
        }
        getLikesString(holder);

        builder = new AlertDialog.Builder((HomeActivity)mCtx);
        final String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(currentuid.equals(holder.photo.getUser_id()))
        {

            holder.Buy_btn.setVisibility(View.GONE);
            holder.save_to_my_store.setVisibility(View.GONE);
            holder.option.setVisibility(View.GONE);
            holder.option_d.setVisibility(View.VISIBLE);
            canDelete = true;
        }else{

            holder.Buy_btn.setVisibility(View.VISIBLE);
            holder.save_to_my_store.setVisibility(View.VISIBLE);
            holder.option.setVisibility(View.VISIBLE);
            holder.option_d.setVisibility(View.GONE);
            canDelete = false;
        }
        holder.caption.setText(product.getCaption());

        holder.caption.post(new Runnable() {
            @Override
            public void run() {
                holder.linecount = holder.caption.getLineCount();

                more = true;
                if(holder.linecount<2)
                    holder.moreless.setVisibility(View.INVISIBLE);
                else
                    holder.moreless.setVisibility(View.VISIBLE);

                holder.moreless.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(more) {
                            holder.caption.setMaxLines(50);
                            holder.moreless.setText("show less");
                            more = false;
                        }else
                        {
                            holder.caption.setMaxLines(2);
                            holder.moreless.setText("show more");
                            more = true;
                        }
                    }
                });
            }
        });

        final String capton = product.getCaption();

        //set the comment
        List<Comment> comments = product.getComments();
        holder.comments.setText("View all " + comments.size() + " comments");
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: loading comment thread for " + product.getPhoto_id());
                ((HomeActivity)mCtx).onCommentThreadSelected(productList.get(position),
                        mCtx.getString(R.string.home_activity));

                //going to need to do something else?
                ((HomeActivity)mCtx).hideLayout();

            }
        });

        //set the time it was posted
        String timestampDifference = getTimestampDifference(product);
        if(!timestampDifference.equals("0")){
            holder.timeDetla.setText(timestampDifference + " DAYS AGO");
        }else{
            holder.timeDetla.setText("TODAY");
        }

        //set the profile image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        path_to_image = product.getImage_path();

        //Picasso.get().load(getItem(position).getImage_path()).into(holder.image);




        //get the profile image and username
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mCtx.getString(R.string.dbname_user_account_settings))
                .orderByChild(mCtx.getString(R.string.field_user_id))
                .equalTo(product.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

//                     currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot);


                    holder.username.setText(singleSnapshot.getValue(UserAccountSettings.class).getUsername());



                    final FirebaseAuth mAuth;
                    FirebaseAuth.AuthStateListener mAuthListener;
                    FirebaseDatabase mFirebaseDatabase;
                    DatabaseReference myRef;
                    FirebaseMethods mFirebaseMethods;
                    StorageReference mStorageReference;
                    final String userID;

                    mAuth = FirebaseAuth.getInstance();
                    mFirebaseDatabase = FirebaseDatabase.getInstance();
                    myRef = mFirebaseDatabase.getReference();
                    mStorageReference = FirebaseStorage.getInstance().getReference();

                    userID = mAuth.getCurrentUser().getUid();


                    final ProductAdapter.ProductViewHolder mHolder;
                    mHolder = holder;

//                    final DatabaseReference s = (mReference.child(mCtx.getString(R.string.dbname_photos))
//                            .child(mHolder.photo.getPhoto_id()).child("price"));

                    final DatabaseReference s = (mReference.child("user_photos").child(mHolder.photo.getUser_id())
                            .child(mHolder.photo.getPhoto_id()).child("price"));
                    s.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                t_price = (dataSnapshot.getValue(Long.class));
                                Log.d(TAG, "onDataChange: price = " + t_price);
                                holder.price.setText("₹ " + String.valueOf(t_price));
                            }catch (NullPointerException e)
                            {}

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//                    holder.image.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Log.d(TAG, "onClick: Clicked on Image! : "+ path_to_image);
//
//
//                            //mCtx.getSupportActionBar().hide();
//
////                            holder.image.getLayoutParams().height = ViewGroup.LayoutParams.;
////                            holder.image.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
////                            holder.image.setAdjustViewBounds(false);
////                            holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
//                            //((HomeActivity)mCtx).full_screen(path_to_image);
//                        }
//                    });

                    counter = 0;
                    img__url = new String[10000];

                    final DatabaseReference typeref = (mReference.child(mCtx.getString(R.string.dbname_photos))
                            .child(mHolder.photo.getPhoto_id()).child("type"));
                    boolean imageloaded = false;
                    if(!imageloaded) {
                        imageloaded = true;
                        typeref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long type;
                                try {
                                    type = (dataSnapshot.getValue(Long.class));
                                } catch (NullPointerException e) {
                                    type = 1;
                                }

                                Log.d(TAG, "onDataChange: price = " + type);
                                final DatabaseReference img_ref = (mReference.child(mCtx.getString(R.string.dbname_photos))
                                        .child(mHolder.photo.getPhoto_id()).child("image_path"));
                                //holder.price.setText("₹ " + String.valueOf(price));
                                try {

                                    if (type == 1) {
                                        holder.ismultiple.setVisibility(View.GONE);

                                        holder.finalimage = product.getImage_path();
//                                        Picasso.get().load(holder.finalimage)
//                                            .placeholder( R.drawable.ic_alert).
//                                                into(holder.image);

//                                holder.ismultiple.setVisibility(View.GONE);
//                                img_ref.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        String imgg;
//                                        try {
//                                            imgg = dataSnapshot.getValue().toString();
//                                        }catch (NullPointerException e){
//                                            imgg = "none";
//                                        }
//                                        //imageLoader.displayImage(imgg, holder.image);
//                                        Picasso.get().load(imgg).into(holder.image);
//                                    }
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                    }
//                                });

                                    } else if (type == 2) {

                                        holder.ismultiple.setVisibility(View.VISIBLE);
                                        img_ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                                    img__url[counter] = childDataSnapshot.getValue(String.class);
                                                    Log.d(TAG, "onDataChange: img__url[" + String.valueOf(counter) + "] = " + img__url[counter]);
                                                    //imageLoader.displayImage(img__url[0], holder.image);
                                                    Log.d(TAG, " new test no : " + childDataSnapshot.getKey());
                                                    Log.d(TAG, " new test url : " + childDataSnapshot.getValue());
                                                    Log.d(TAG, " new test Count : " + String.valueOf(counter));
                                                    counter++;
                                                }
                                                holder.finalimage = img__url[0];
//                                                Picasso.get().load(holder.finalimage)
//                                                        .placeholder( R.drawable.ic_alert ).
//                                                            into(holder.image);


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }catch (Exception e){}
                                //getItem(position).getImage_path()

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                    final String[] urls = img_urls.toArray(new String[0]);
                    Log.d(TAG, "onDataChange: image url = " + img__url[0]);


                    holder.ismultiple.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final DatabaseReference typeref = (mReference.child(mCtx.getString(R.string.dbname_photos))
                                    .child(mHolder.photo.getPhoto_id()).child("type"));

                            typeref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        final long type = (dataSnapshot.getValue(Long.class));
                                        Log.d(TAG, "onDataChange: type = " + type);
                                        //holder.price.setText("₹ " + String.valueOf(price));
                                        if (type == 2) {
                                            String img_id = mHolder.photo.getPhoto_id();
                                            //String img_url = mHolder.photo.getImage_path();
                                            ((HomeActivity) mCtx).multi_show(img_id, urls, String.valueOf(type), holder.user);
                                            ((HomeActivity) mCtx).hideLayout();
                                        }
                                    }catch (Exception e){}

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    });

//                    holder.save_to_my_store.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//
//                        }
//                    });


                    Log.d(TAG, "onDataChange: holder.user.getUsername() == " + holder.user.getUsername() + " currentUsername = " + currentUsername);

                    //try {
                    //holder.Buy_btn.setVisibility(View.VISIBLE);

                    Log.d(TAG, "caption = "+holder.photo.getCaption() + " onDataChange: userID = " + userID + " \nholder.user.getUser_id() = "+ holder.photo.getUser_id() + " onDataChange: holder.user.getUsername() == " + holder.user.getUsername() + " currentUsername = " + currentUsername);
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>((HomeActivity)mCtx,R.layout.list_item,R.id.menu_item, items1);
                    listView_d.setAdapter(adapter1);
                    listView_d.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            vg = (ViewGroup)view;

                            TextView txt = (TextView)vg.findViewById(R.id.menu_item);
                            Toast.makeText((HomeActivity)mCtx,txt.getText().toString(), Toast.LENGTH_LONG);
                            Log.d(TAG, "onItemClick: trying to toast : "+ txt.getText().toString());



                            switch (txt.getText().toString()){
                                case "Unfollow":
                                    Log.d(TAG, "onItemClick: follow/unfollow with dialog box current user : "+FirebaseAuth.getInstance().getCurrentUser().getUid()+" user : "+holder.user.getUser_id());
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("following")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(holder.user.getUser_id())
                                            .removeValue();

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("followers")
                                            .child(holder.user.getUser_id())
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    dialog.dismiss();
                                    break;
                                case "Report":
                                    //((HomeActivity)mCtx).email(mHolder.photo.getPhoto_id(), holder.user.getUsername());
                                    String[] reportList = new String[4];
                                    reportList[0] = "Abusive Content";
                                    reportList[1] = "Fraud";
                                    reportList[2] = "Harassment";
                                    reportList[3] = "Illegal";
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>((HomeActivity)mCtx,R.layout.list_item,R.id.menu_item, reportList);
                                    rListView.setAdapter(adapter);
                                    rListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            vg = (ViewGroup)view;
                                            TextView txt = (TextView)vg.findViewById(R.id.menu_item);
                                            String url = "";
                                            String[] urls = new String[10];
                                            if(mHolder.photo.getImage_path().contains(","))
                                            {
                                                urls = mHolder.photo.getImage_path().split(", ");
                                            }
                                            else{
                                                url = mHolder.photo.getImage_path();
                                            }
                                            String emailBody = "Post id = " + mHolder.photo.getPhoto_id() +
                                                    "\nPost pic URL = ";
                                            //+ mHolder.photo.getImage_path() +;
                                            int countimgurl = 1;
                                            if(mHolder.photo.getImage_path().contains(",")) {
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
                                            emailBody += "\nPost caption = " + mHolder.photo.getCaption() +
                                                    "\nPost date = " + mHolder.photo.getDate_created() +
                                                    "\nPosted by username = " + mHolder.user.getUsername() +
                                                    "\nUser ID = " + mHolder.user.getUser_id() +
                                                    "\nReported type = " + txt.getText().toString() +
                                                    "\nReported by = " + currentUsername;

                                            HomeActivity ha = new HomeActivity();
                                            ha.sendMessage(emailBody);
                                            Toast.makeText(mCtx,"Post reported successfully",Toast.LENGTH_SHORT).show();
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
                                            .child(holder.photo.getPhoto_id())
                                            .setValue(null);

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("photos")
                                            .child(holder.photo.getPhoto_id())
                                            .setValue(null);
                                    Toast.makeText(mCtx,"Post Deleted",Toast.LENGTH_LONG).show();
                                    Activity activity = (Activity)mCtx;
                                    Intent intent = activity.getIntent();
                                    Intent intent1 = new Intent(activity, HomeActivity.class);
                                    activity.finish();
                                    mCtx.startActivity(intent1);
                                    mCtx.startActivity(intent);
                                    dialog.dismiss();
                                    break;
                                case "Share":

                                    Log.d(TAG, "onClick: sharing url : "+ mHolder.photo.getImage_path() +" image type = " + mHolder.photo.getType()
                                            + " photoid = " + mHolder.photo.getPhoto_id());
                                    final ShareIt share = new ShareIt();

//                                    final DatabaseReference s = (mReference.child(mCtx.getString(R.string.dbname_photos))
//                                            .child(mHolder.photo.getPhoto_id()).child("price"));

                    final DatabaseReference s = (mReference.child("user_photos").child(mHolder.photo.getUser_id())
                            .child(mHolder.photo.getPhoto_id()).child("price"));

                                    s.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                t_price = (dataSnapshot.getValue(Long.class));
                                                Log.d(TAG, "onDataChange: price = " + t_price);
                                                holder.price.setText("₹ " + String.valueOf(t_price));
                                                if(holder.photo.getImage_path().contains(",")) {
                                                    String[] imageurls = holder.photo.getImage_path().split(", ");
                                                    String[] modifiedArray_f = Arrays.copyOfRange(imageurls, 1, imageurls.length);
                                                    share.shareit(modifiedArray_f,holder.sharePhoto, mCtx, t_price , holder.photo.getCaption(),holder.photo.getPhoto_id());

                                                }else {
                                                    String[] imgurl = {holder.photo.getImage_path()};
                                                    share.shareit(imgurl, holder.sharePhoto, mCtx, t_price, holder.photo.getCaption(), holder.photo.getPhoto_id());
                                                }
                                            }catch (NullPointerException e)
                                            {}

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
//                                            }else{
//                                                ShareIt share = new ShareIt();
//                                                share.shareit(modifiedArray_f, holder.sharePhoto, mCtx, t_price, mHolder.photo.getCaption(), mHolder.photo.getPhoto_id());
//                                            }
//                                            if(imageurls.length>1) {
//
//                                                for (int st = 0; st < imageurls.length; st++) {
//                                                    if(!imageurls[st].contains("null")) {
//                                                        imageurls[st] = imageurls[st].replace("]","");
//                                                        try {
//                                                            Log.d(TAG, "onDataChange: image url at final phase " + imageurls[st]);
//                                                            Picasso.get().load(imageurls[st]).into(holder.sharePhoto[st]);
//                                                            //URL url = new URL(imageurls[st]);
//                                                            //b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                                                            //String path = MediaStore.Images.Media.insertImage(mCtx.getContentResolver(), b, "Image Description", null);
//                                                            d = holder.sharePhoto[st].getDrawable();
//                                                            b = ((BitmapDrawable) d).getBitmap();
//                                                            String path = MediaStore.Images.Media.insertImage(mCtx.getContentResolver(), b, "Image Description", null);
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
//                                                        Picasso.get().load(imgurl).into(holder.sharePhoto[0]);
//                                                        d = holder.sharePhoto[0].getDrawable();
//                                                        b = ((BitmapDrawable) d).getBitmap();
//                                                        String path = MediaStore.Images.Media.insertImage(mCtx.getContentResolver(), b, "Image Description", null);
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
//                                String path = MediaStore.Images.Media.insertImage(mCtx.getContentResolver(), mBitmap[n], "Image Description", null);
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
//                                                intent.putExtra(Intent.EXTRA_TEXT, mHolder.photo.getCaption()+" \nPrice : ₹ "+ price +
//                                                        "\nBuy this : https://collectmoney.in/app/?id=" + mHolder.photo.getPhoto_id());
//                                                mCtx.startActivity(intent);
//                                            }catch (NullPointerException e)
//                                            {}
                                    dialog.dismiss();
                                    break;
                                default:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>((HomeActivity)mCtx,R.layout.list_item,R.id.menu_item, items);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            vg = (ViewGroup)view;

                            TextView txt = (TextView)vg.findViewById(R.id.menu_item);
                            Toast.makeText((HomeActivity)mCtx,txt.getText().toString(), Toast.LENGTH_LONG);
                            Log.d(TAG, "onItemClick: trying to toast : "+ txt.getText().toString());



                            switch (txt.getText().toString()){
                                case "Unfollow":
                                    Log.d(TAG, "onItemClick: follow/unfollow with dialog box current user : "+FirebaseAuth.getInstance().getCurrentUser().getUid()+" user : "+holder.user.getUser_id());
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("following")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(holder.user.getUser_id())
                                            .removeValue();

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("followers")
                                            .child(holder.user.getUser_id())
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    dialog.dismiss();
                                    break;
                                case "Report":
                                    //((HomeActivity)mCtx).email(mHolder.photo.getPhoto_id(), holder.user.getUsername());
                                    String[] reportList = new String[4];
                                    reportList[0] = "Abusive Content";
                                    reportList[1] = "Fraud";
                                    reportList[2] = "Harassment";
                                    reportList[3] = "Illegal";
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>((HomeActivity)mCtx,R.layout.list_item,R.id.menu_item, reportList);
                                    rListView.setAdapter(adapter);
                                    rListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            vg = (ViewGroup)view;
                                            TextView txt = (TextView)vg.findViewById(R.id.menu_item);
                                            String url = "";
                                            String[] urls = new String[10];
                                            if(mHolder.photo.getImage_path().contains(","))
                                            {
                                                urls = mHolder.photo.getImage_path().split(", ");
                                            }
                                            else{
                                                url = mHolder.photo.getImage_path();
                                            }
                                            String emailBody = "Post id = " + mHolder.photo.getPhoto_id() +
                                                    "\nPost pic URL = ";
                                            //+ mHolder.photo.getImage_path() +;
                                            int countimgurl = 1;
                                            if(mHolder.photo.getImage_path().contains(",")) {
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
                                            emailBody += "\nPost caption = " + mHolder.photo.getCaption() +
                                                    "\nPost date = " + mHolder.photo.getDate_created() +
                                                    "\nPosted by username = " + mHolder.user.getUsername() +
                                                    "\nUser ID = " + mHolder.user.getUser_id() +
                                                    "\nReported type = " + txt.getText().toString() +
                                                    "\nReported by = " + currentUsername;

                                            HomeActivity ha = new HomeActivity();
                                            ha.sendMessage(emailBody);
                                            Toast.makeText(mCtx,"Post reported successfully",Toast.LENGTH_SHORT).show();
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
                                            .child(holder.photo.getPhoto_id())
                                            .setValue(null);

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("photos")
                                            .child(holder.photo.getPhoto_id())
                                            .setValue(null);
                                    Toast.makeText(mCtx,"Post Deleted",Toast.LENGTH_LONG).show();
                                    Activity activity = (Activity)mCtx;
                                    Intent intent = activity.getIntent();
                                    Intent intent1 = new Intent(activity, HomeActivity.class);
                                    activity.finish();
                                    mCtx.startActivity(intent1);
                                    mCtx.startActivity(intent);
                                    dialog.dismiss();
                                    break;
                                case "Share":

                                    Log.d(TAG, "onClick: sharing url : "+ mHolder.photo.getImage_path() +" image type = " + mHolder.photo.getType()
                                            + " photoid = " + mHolder.photo.getPhoto_id());
                                    final ShareIt share = new ShareIt();

//                                    final DatabaseReference s = (mReference.child(mCtx.getString(R.string.dbname_photos))
//                                            .child(mHolder.photo.getPhoto_id()).child("price"));

                    final DatabaseReference s = (mReference.child("user_photos").child(mHolder.photo.getUser_id())
                            .child(mHolder.photo.getPhoto_id()).child("price"));

                                    s.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                t_price = (dataSnapshot.getValue(Long.class));
                                                Log.d(TAG, "onDataChange: price = " + t_price);
                                                holder.price.setText("₹ " + String.valueOf(t_price));
                                                if(holder.photo.getImage_path().contains(",")) {
                                                    String[] imageurls = holder.photo.getImage_path().split(", ");
                                                    String[] modifiedArray_f = Arrays.copyOfRange(imageurls, 1, imageurls.length);
                                                    share.shareit(modifiedArray_f,holder.sharePhoto, mCtx, t_price , holder.photo.getCaption(),holder.photo.getPhoto_id());

                                                }else {
                                                    String[] imgurl = {holder.photo.getImage_path()};
                                                    share.shareit(imgurl, holder.sharePhoto, mCtx, t_price, holder.photo.getCaption(), holder.photo.getPhoto_id());
                                                }
                                            }catch (NullPointerException e)
                                            {}

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
//                                            }else{
//                                                ShareIt share = new ShareIt();
//                                                share.shareit(modifiedArray_f, holder.sharePhoto, mCtx, t_price, mHolder.photo.getCaption(), mHolder.photo.getPhoto_id());
//                                            }
//                                            if(imageurls.length>1) {
//
//                                                for (int st = 0; st < imageurls.length; st++) {
//                                                    if(!imageurls[st].contains("null")) {
//                                                        imageurls[st] = imageurls[st].replace("]","");
//                                                        try {
//                                                            Log.d(TAG, "onDataChange: image url at final phase " + imageurls[st]);
//                                                            Picasso.get().load(imageurls[st]).into(holder.sharePhoto[st]);
//                                                            //URL url = new URL(imageurls[st]);
//                                                            //b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                                                            //String path = MediaStore.Images.Media.insertImage(mCtx.getContentResolver(), b, "Image Description", null);
//                                                            d = holder.sharePhoto[st].getDrawable();
//                                                            b = ((BitmapDrawable) d).getBitmap();
//                                                            String path = MediaStore.Images.Media.insertImage(mCtx.getContentResolver(), b, "Image Description", null);
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
//                                                        Picasso.get().load(imgurl).into(holder.sharePhoto[0]);
//                                                        d = holder.sharePhoto[0].getDrawable();
//                                                        b = ((BitmapDrawable) d).getBitmap();
//                                                        String path = MediaStore.Images.Media.insertImage(mCtx.getContentResolver(), b, "Image Description", null);
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
//                                String path = MediaStore.Images.Media.insertImage(mCtx.getContentResolver(), mBitmap[n], "Image Description", null);
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
//                                                intent.putExtra(Intent.EXTRA_TEXT, mHolder.photo.getCaption()+" \nPrice : ₹ "+ price +
//                                                        "\nBuy this : https://collectmoney.in/app/?id=" + mHolder.photo.getPhoto_id());
//                                                mCtx.startActivity(intent);
//                                            }catch (NullPointerException e)
//                                            {}
                                    dialog.dismiss();
                                    break;
                                default:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });

//                    }catch (Exception e)
//                    {
//                        Log.d(TAG, "onDataChange: e = " + e.toString());
//                    }
                    holder.option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //try {
                            items = new String[2];
                            items[1] = "Unfollow";
                            items[0] = "Report";

                            showDialogListView();
//                            }catch (Exception e)
//                            {
//                            }

                        }
                    });
                    holder.option_d.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            items1 = new String[2];
                            items1[1] = "Report";
                            items1[0] = "Delete";
                            showDialogListView_d();
                        }
                    });



                    holder.Buy_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            s.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final long price = (dataSnapshot.getValue(Long.class));

                                    Log.d(TAG, "onDataChange: going to buy now");
                                    Log.d(TAG, "onDataChange: user id should" + holder.user.getUser_id());
                                    String buyer = currentuid;

//                                    String seller_id = holder.user.getUser_id();
//                                    String current_user = currentUsername;
//                                    String userN = holder.user.getUsername();
//                                    String id = mHolder.photo.getPhoto_id();
                                    // ((HomeActivity)mCtx).buy_now(id,userN,price,current_user,userID,seller_id,path_to_image,capton);
                                    ((HomeActivity)mCtx).buy_now(holder.photo.getPhoto_id(),buyer);
                                    //((HomeActivity)mCtx).hideLayout();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });


                    holder.save_to_my_store.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            savetostoreclicked = true;
                            if (savetostoreclicked) {
                                savetostoreclicked = false;
                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("user_photos")
                                        .child(holder.photo.getUser_id())
                                        .child(holder.photo.getPhoto_id());
                                dbref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Photo newPhoto = new Photo();
                                        Bundle b = new Bundle();


                                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                        long price = (long) objectMap.get("price");
                                        Log.d(TAG, "onDataChange: save to my store price = " + price);
                                        Log.d(TAG, "onDataChange: photo model hashmap " + objectMap.toString());
                                        newPhoto.setCaption(objectMap.get(mCtx.getString(R.string.field_caption)).toString());
                                        newPhoto.setTags(objectMap.get(mCtx.getString(R.string.field_tags)).toString());
                                        newPhoto.setPhoto_id(objectMap.get(mCtx.getString(R.string.field_photo_id)).toString());
                                        newPhoto.setUser_id(objectMap.get(mCtx.getString(R.string.field_user_id)).toString());
                                        newPhoto.setDate_created(objectMap.get(mCtx.getString(R.string.field_date_created)).toString());
                                        newPhoto.setImage_path(objectMap.get(mCtx.getString(R.string.field_image_path)).toString());
                                        //newPhoto.setPrice(price);
                                        newPhoto.setType(Integer.valueOf(objectMap.get("type").toString()));
                                        Log.d(TAG, "onDataChange: retrieved photo model = " + newPhoto.toString());


                                        Intent intent = new Intent(mCtx, AddActivity.class);
                                        b.putParcelable("photo", newPhoto);
                                        //b.putLong("price", price);
                                        //intent.putExtra("photo",newPhoto);
                                        //intent.putExtra("price", price);
                                        intent.putExtras(b);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //intent.putExtra("price",price);
                                        mCtx.startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

//                            s.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    final long price = (dataSnapshot.getValue(Long.class));
                                //                             ((HomeActivity)mCtx).add_to_my_store(price,path_to_image,capton);
//                                    ((HomeActivity)mCtx).hideLayout();
//                                }
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
                            }
                        }
                    });

                    //initialize all images

                    try {
                        final String[] getImage = product.getImage_path().split(", ");
                        int counting = 0;
                        Log.d(TAG, "onDataChange: getimagepath() = " + product.getImage_path() + "String[] getImage = " + getImage);
                        for (String url : getImage) {
                            if (!url.equals("[null")) {
                                imageLoader.displayImage(url, holder.sharePhoto[counting]);
                                counting++;
                            }
                        }


                        //end initialize images

                        holder.share_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ShareIt share = new ShareIt();

                                final DatabaseReference s = (mReference.child("user_photos").child(mHolder.photo.getUser_id())
                                        .child(mHolder.photo.getPhoto_id()).child("price"));

                                s.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            t_price = (dataSnapshot.getValue(Long.class));
                                            Log.d(TAG, "onDataChange: price = " + t_price);
                                            holder.price.setText("₹ " + String.valueOf(t_price));
                                            if(holder.photo.getImage_path().contains(",")) {
                                                String[] imageurls = holder.photo.getImage_path().split(", ");
                                                String[] modifiedArray_f = Arrays.copyOfRange(imageurls, 1, imageurls.length);
                                                share.shareit(modifiedArray_f,holder.sharePhoto, mCtx, t_price , holder.photo.getCaption(),holder.photo.getPhoto_id());

                                            }else {
                                                String[] imgurl = {holder.photo.getImage_path()};
                                                share.shareit(imgurl, holder.sharePhoto, mCtx, t_price, holder.photo.getCaption(), holder.photo.getPhoto_id());
                                            }
                                        }catch (NullPointerException e)
                                        {}

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                // long t_price = holder.photo.getPrice();

                            }
                        });
                    }catch (Exception e)
                    {

                    }




                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    holder.user.getUsername());
                            Intent intent = new Intent(mCtx, ProfileActivity.class);
                            intent.putExtra(mCtx.getString(R.string.calling_activity),
                                    mCtx.getString(R.string.home_activity));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(mCtx.getString(R.string.intent_user), holder.user);
                            mCtx.startActivity(intent);
                        }
                    });



                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                            holder.mprofileImage);
                    holder.mprofileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    holder.user.getUsername());

                            Intent intent = new Intent(mCtx, ProfileActivity.class);
                            intent.putExtra(mCtx.getString(R.string.calling_activity),
                                    mCtx.getString(R.string.home_activity));
                            intent.putExtra(mCtx.getString(R.string.intent_user), holder.user);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            mCtx.startActivity(intent);
                        }
                    });



                    holder.settings = singleSnapshot.getValue(UserAccountSettings.class);
                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((HomeActivity)mCtx).onCommentThreadSelected(product,
                                    mCtx.getString(R.string.home_activity));

                            //another thing?
                            ((HomeActivity)mCtx).hideLayout();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mReference = FirebaseDatabase.getInstance().getReference();

        //get the user object
        Query userQuery = mReference
                .child(mCtx.getString(R.string.dbname_users))
                .orderByChild(mCtx.getString(R.string.field_user_id))
                .equalTo(product.getUser_id());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.getValue(User.class).getUsername());

                    holder.user = singleSnapshot.getValue(User.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(reachedEndOfList(position)){
            loadMoreData();
        }


//        Picasso.get().load(productList.get(position).getImage_path()).into( imageView);

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView imageView;
        CircleImageView mprofileImage;
        String likesString;
        TextView username, timeDetla, caption, likes, comments;
        SquareImageView image;
        ImageView heartRed, heartWhite, comment,save_to_my_store;
        ImageView[] sharePhoto = new ImageView[10];
        TextView price;
        Button Buy_btn;
        ImageView share_btn;
        TextView moreless;
        ImageButton ismultiple;
        UserAccountSettings settings = new UserAccountSettings();
        User user  = new User();
        StringBuilder users;
        String mLikesString;
        int linecount;
        boolean likeByCurrentUser;
        Heart heart;
        GestureDetector detector;
        Photo photo;
        Button buy_now;
        ImageView option;
        String finalimage;
        ImageView option_d;
        String notKey = FirebaseDatabase.getInstance().getReference().push().getKey();

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.caption);
            textViewShortDesc = itemView.findViewById(R.id.image_likes);
            imageView = itemView.findViewById(R.id.post_image);
            username = (TextView) itemView.findViewById(R.id.username);
            price = (TextView) itemView.findViewById(R.id.price_display);
            image = (SquareImageView) itemView.findViewById(R.id.post_image);
            heartRed = (ImageView) itemView.findViewById(R.id.image_heart_red);
            heartWhite = (ImageView) itemView.findViewById(R.id.image_heart);
            comment = (ImageView) itemView.findViewById(R.id.speech_bubble);
            likes = (TextView) itemView.findViewById(R.id.image_likes);
            comments = (TextView) itemView.findViewById(R.id.image_comments_link);
            caption = (TextView) itemView.findViewById(R.id.image_caption);
            timeDetla = (TextView) itemView.findViewById(R.id.image_time_posted);
            mprofileImage = (CircleImageView) itemView.findViewById(R.id.profile_photo);
            Buy_btn = (Button) itemView.findViewById(R.id.buyButton);
             save_to_my_store = (ImageView) itemView.findViewById(R.id.btn_save);
             share_btn = (ImageView) itemView.findViewById(R.id.share_btn);
             option = (ImageView)itemView.findViewById(R.id.ivEllipses);
             option_d = (ImageView)itemView.findViewById(R.id.ivEllipses_d);
            buy_now = (Button) itemView.findViewById(R.id.buyButton);
            ismultiple = (ImageButton) itemView.findViewById(R.id.ismultiple);
            moreless = (TextView)itemView.findViewById(R.id.moreless);
             sharePhoto[0] = (ImageView) itemView.findViewById(R.id.sp0);
             sharePhoto[1] = (ImageView) itemView.findViewById(R.id.sp1);
             sharePhoto[2] = (ImageView) itemView.findViewById(R.id.sp2);
             sharePhoto[3] = (ImageView) itemView.findViewById(R.id.sp3);
             sharePhoto[4] = (ImageView) itemView.findViewById(R.id.sp4);
             sharePhoto[5] = (ImageView) itemView.findViewById(R.id.sp5);
             sharePhoto[6] = (ImageView) itemView.findViewById(R.id.sp6);
             sharePhoto[7] = (ImageView) itemView.findViewById(R.id.sp7);
             sharePhoto[8] = (ImageView) itemView.findViewById(R.id.sp8);
             Buy_btn.setVisibility(View.GONE);

        }
    }
    public class GestureListener extends GestureDetector.SimpleOnGestureListener{

        ProductAdapter.ProductViewHolder mHolder;
        public GestureListener(ProductAdapter.ProductViewHolder holder) {
            mHolder = holder;
        }



        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected.");

            Log.d(TAG, "onDoubleTap: clicked on photo: " + mHolder.photo.getPhoto_id());

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(mCtx.getString(R.string.dbname_photos))
                    .child(mHolder.photo.getPhoto_id())
                    .child(mCtx.getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        for(DataSnapshot ads : singleSnapshot.getChildren()) {
                            String userid = ads.getValue(String.class);
                            Log.d(TAG, "onDataChange: like string this : " + userid);

                            String keyID = singleSnapshot.getKey();

                            //case1: Then user already liked the photo
                            if(mHolder.likeByCurrentUser
//                                && singleSnapshot.getValue(Like.class).getUser_id()
//                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            ){
                                if(userid.equals(FirebaseAuth.getInstance().getUid())) {

                                    mReference.child(mCtx.getString(R.string.dbname_photos))
                                            .child(mHolder.photo.getPhoto_id())
                                            .child(mCtx.getString(R.string.field_likes))
                                            .child(keyID)
                                            .removeValue();



///
                                    mReference.child(mCtx.getString(R.string.dbname_user_photos))
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(mHolder.photo.getUser_id())
                                            .child(mHolder.photo.getPhoto_id())
                                            .child(mCtx.getString(R.string.field_likes))
                                            .child(keyID)
                                            .removeValue();
                                    notification_delete = false;

                                    mReference.child("notification").child(mHolder.photo.getUser_id()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot ds : dataSnapshot.getChildren())
                                            {
                                                if(!notification_delete) {
                                                    notification Not = ds.getValue(notification.class);
                                                    if (Not.getTarget().equals(mHolder.photo.getPhoto_id()) &&
                                                            Not.getBy_user().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        mReference.child("notification")
                                                                .child(mHolder.photo.getUser_id()).child(ds.getKey()).setValue(null);
                                                    }
                                                    notification_delete = true;
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                    mHolder.heart.toggleLike();
                                    getLikesString(mHolder);
                                }
                            }
                            //case2: The user has not liked the photo
                            else if(!mHolder.likeByCurrentUser){
                                //add new like
                                addNewLike(mHolder);
                                break;
                            }
                        }

                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike(mHolder);
                        notification Not = new notification();
                        String id = FirebaseDatabase.getInstance().getReference().push().getKey();
                        Not.setTarget(mHolder.photo.getPhoto_id());
                        Not.setNot_id(id);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        Not.setTime(currentDateandTime);
                        Not.setType("1");
                        Not.setSeen("unseen");
                        Not.setBy_user(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if(!currentuid.equals(mHolder.photo.getUser_id())) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("notification")
                                    .child(mHolder.photo.getUser_id())
                                    .child(id).setValue(Not);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

//    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
//
//        ViewHolder mHolder;
//        public GestureListener(ViewHolder holder) {
//            mHolder = holder;
//        }
//
// //       @Override
// //       public boolean onDown(MotionEvent e) {
// //           return true;
// //       }
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            Log.d(TAG, "onDoubleTap: double tap detected.");
//
//            Log.d(TAG, "onDoubleTap: clicked on photo: " + mHolder.photo.getPhoto_id());
//
//            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//            final String currentDateandTime = sdf.format(new Date());
//
//            final String mGroupId = reference.child("notification").child(mHolder.photo.getUser_id()).push().getKey();
//
//
//
//            Query query = reference
//                    .child(mCtx.getString(R.string.dbname_photos))
//                    .child(mHolder.photo.getPhoto_id())
//                    .child(mCtx.getString(R.string.field_likes));
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//
//                        String keyID = singleSnapshot.getKey();
//
//                        //case1: Then user already liked the photo
//                        if(mHolder.likeByCurrentUser
//                                && singleSnapshot.getValue(Like.class).getUser_id()
//                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
    //){

//                            mReference.child(mCtx.getString(R.string.dbname_photos))
//                                    .child(mHolder.photo.getPhoto_id())
//                                    .child(mCtx.getString(R.string.field_likes))
//                                    .child(keyID)
//                                    .removeValue();
/////
//                            mReference.child(mCtx.getString(R.string.dbname_user_photos))
////                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                    .child(mHolder.photo.getUser_id())
//                                    .child(mHolder.photo.getPhoto_id())
//                                    .child(mCtx.getString(R.string.field_likes))
//                                    .child(keyID)
//                                    .removeValue();
//                            reference.child("notification").child(mHolder.photo.getUser_id()).child(mGroupId).removeValue();
//
//                            mHolder.heart.toggleLike();
//                            getLikesString(mHolder);
//                        }
//                        //case2: The user has not liked the photo
//                        else if(!mHolder.likeByCurrentUser){
//                            //add new like
//                            addNewLike(mHolder);
//                            break;
//                        }
//                    }
//                    if(!dataSnapshot.exists()){
//                        //add new like
//
//                        addNewLike(mHolder);
//                        String notKey = FirebaseDatabase.getInstance().getReference().push().getKey();
//                        String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        SetNotification sn = new SetNotification();
//                        sn.notify(currentuid,notKey,"unseen",getTimestamp(),"1",mHolder.photo.getUser_id(),mHolder.photo.getPhoto_id());
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//            return true;
//        }
//    }
private boolean reachedEndOfList(int position){
    return position == getItemCount() - 1;
}

    private void loadMoreData(){

        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) mCtx;
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    private void addNewLike(final ProductAdapter.ProductViewHolder holder){
        Log.d(TAG, "addNewLike: adding new like");

        String newLikeID = mReference.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mReference.child(mCtx.getString(R.string.dbname_photos))
                .child(holder.photo.getPhoto_id())
                .child(mCtx.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mReference.child(mCtx.getString(R.string.dbname_user_photos))
                .child(holder.photo.getUser_id())
                .child(holder.photo.getPhoto_id())
                .child(mCtx.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        holder.heart.toggleLike();
        getLikesString(holder);
    }

    private void getCurrentUsername(){
        Log.d(TAG, "getCurrentUsername: retrieving user account settings");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mCtx.getString(R.string.dbname_users))
                .orderByChild(mCtx.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLikesString(final ProductAdapter.ProductViewHolder holder){
        likeRefreshed = false;

        Log.d(TAG, "getLikesString: getting likes string");

        Log.d(TAG, "getLikesString: photo id: " + holder.photo.getPhoto_id());
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(mCtx.getString(R.string.dbname_photos))
                    .child(holder.photo.getPhoto_id())
                    .child(mCtx.getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.users = new StringBuilder();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(mCtx.getString(R.string.dbname_users))
                                .orderByChild(mCtx.getString(R.string.field_user_id))
                                .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                    Log.d(TAG, "onDataChange: found like: " +
                                            singleSnapshot.getValue(User.class).getUsername());

                                    holder.users.append(singleSnapshot.getValue(User.class).getUsername());
                                    holder.users.append(",");
                                }

                                String[] splitUsers = holder.users.toString().split(",");
                                List<String> arrList = new ArrayList<String>();
                                int cnt= 0;
                                //List<String> arrList = Arrays.asList(arr);
                                List<String> lenList = new ArrayList<String>();
                                for(int i=0;i<splitUsers.length;i++){
                                    for(int j=i+1;j<splitUsers.length;j++){
                                        if(splitUsers[i].equals(splitUsers[j])){
                                            cnt+=1;
                                        }
                                    }
                                    if(cnt<1){
                                        arrList.add(splitUsers[i]);
                                    }
                                    cnt=0;
                                }
                                splitUsers = new String[arrList.size()];
                                for(int k=0;k<arrList.size();k++){
                                    System.out.println("Array without Duplicates: "+arrList.get(k));
                                    splitUsers[k] = arrList.get(k);
                                }



                                if (holder.users.toString().contains(currentUsername + ",")) {
                                    holder.likeByCurrentUser = true;
                                } else {
                                    holder.likeByCurrentUser = false;
                                }

                                int length = splitUsers.length;

                                if (length == 0) {
                                    holder.likesString = "0 Likes";
                                } else if (length == 1) {
                                    holder.likesString = "Liked by " + splitUsers[splitUsers.length-1];
                                } else if (length == 2) {
                                    holder.likesString = "Liked by " + splitUsers[splitUsers.length-1]
                                            + " and " + splitUsers[splitUsers.length-2];
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
                                    holder.likesString = "Liked by " + splitUsers[splitUsers.length-1]
                                            + ", " + splitUsers[splitUsers.length-2]
//                                                    + ", " + splitUsers[2]
                                            + " and " + (splitUsers.length - 2) + " others";
                                }
                                Log.d(TAG, "onDataChange: likes string: " + holder.likesString);
                                //setup likes string

                                setupLikesString(holder, holder.likesString);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    if (!dataSnapshot.exists()) {
                        holder.likesString = "0 Likes";
                        holder.likeByCurrentUser = false;
                        //setup likes string
                        setupLikesString(holder, holder.likesString);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
            Log.e(TAG, "getLikesString: NullPointerException: " + e.getMessage());
            holder.likesString = "";
            holder.likeByCurrentUser = false;
            //setup likes string
            setupLikesString(holder, holder.likesString);
        }

    }

    private void setupLikesString(final ProductAdapter.ProductViewHolder holder, String likesString){
        Log.d(TAG, "setupLikesString: likes string:" + holder.likesString);

        Log.d(TAG, "setupLikesString: photo id: " + holder.photo.getPhoto_id());
        if(holder.likeByCurrentUser){
            Log.d(TAG, "setupLikesString: photo is liked by current user");
            holder.heartWhite.setVisibility(View.GONE);
            holder.heartRed.setVisibility(View.VISIBLE);
            holder.heartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        }else{
            Log.d(TAG, "setupLikesString: photo is not liked by current user");
            holder.heartWhite.setVisibility(View.VISIBLE);
            holder.heartRed.setVisibility(View.GONE);
            holder.heartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        }
        holder.likes.setText(likesString);
    }
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(Photo photo){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photo.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

    public void showDialogListView()
    {

        builder.setCancelable(true);
        builder.setView(null);
        builder.setView(listView);

        dialog = builder.create();
        try {
            if (listView.getParent() != null) {
                ((ViewGroup) listView.getParent()).removeView(listView);
            }
        }catch (Exception e){}
        //  dialog.setCancelable(true);

        dialog.show();
    }
    public void showDialogListView_d()
    {

        builder.setCancelable(true);
        builder.setView(null);
        builder.setView(listView_d);

        dialog = builder.create();
        try {
            if (listView_d.getParent() != null) {
                ((ViewGroup) listView_d.getParent()).removeView(listView_d);
            }
        }catch (Exception e){}
        //  dialog.setCancelable(true);

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

            dialog.show();

    }

    public void share()
    {

    }


}