package com.luxuryshauk.Utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luxuryshauk.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlaceOrder extends AppCompatActivity {

    private final String TAG = "Place Order : ";

    private TextView Cust_name,Phone,flat,Street,City,State,Pin;
    private Button ptp;
    private String userS;
    private ImageView photo;
    public int order_no;
    WebView wb;

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        //setContentView(R.layout.layout_place_order);
        setContentView(R.layout.collect_money);
        wb=(WebView)findViewById(R.id.webView1);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setLoadWithOverviewMode(true);
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setBuiltInZoomControls(true);
        wb.getSettings().setPluginState(WebSettings.PluginState.ON);
        wb.setWebViewClient(new HelloWebViewClient());
        Bundle bundle = getIntent().getExtras();
        String buyer = bundle.getString("buyer");
        String photoid = bundle.getString("photoid");
        String sellerid = bundle.getString("seller");
//        Bundle bundle = getIntent().getExtras();
//        String seller = bundle.getString("seller_username");
//        String price = bundle.getString("price");
//        String current_U = bundle.getString("current_user");
//        String img_url = bundle.getString("img_url");
//        String seller_uid = bundle.getString("seller_uid");
//        String caption = bundle.getString("caption");
//        String[] withoutnull;
//        String[] url;
//        String[] new_url = img_url.split(", ");
//        for (String i:new_url)
//            Log.d(TAG, "onCreate: img url = " + i);
//        Log.d(TAG, "onCreate: img lenght = "+ new_url.length);
//        if(new_url.length>1) {
//            withoutnull = img_url.split(", ");
//            url = withoutnull[1].split("&");
//        }
//        else {
//            url = img_url.split("&");
//        }
//        String id = bundle.getString("id");
//        FirebaseAuth mAuth;
//        mAuth = FirebaseAuth.getInstance();
//        String userID = mAuth.getCurrentUser().getUid();
//        for(String u:url)
//            Log.d(TAG, "onCreate: for url = "+u);

        String urlString = "https://collectmoney.in/app/index_buy.php?id="+photoid+"&buyer="+buyer+"&seller="+sellerid;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null);
            startActivity(intent);
        }
        wb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });
        //https://collectmoney.in/app/?id=
//        wb.loadUrl("https://collectmoney.in/app/index_buy.php?id="+photoid+"&buyer="+buyer);


        //wb.loadUrl("https://www.collectmoney.in/products/?pdetails="+caption+","+seller_uid+","+url[0]+","+url[1]+","+userID+",app=1"+","+id+","+current_U);
   //     init();


    }

    public void init()
    {

        TextView caption_view = (TextView)findViewById(R.id.caption);
        ImageView img_view = (ImageView) findViewById(R.id.preview_img);
        TextView price_view = (TextView)findViewById(R.id.price);

        Cust_name = (TextView) findViewById(R.id.CustomerName);
        Phone = (TextView) findViewById(R.id.PhoneNo);
        flat = (TextView) findViewById(R.id.BuildingAddress);
        photo = (ImageView) findViewById(R.id.preview_img);
        Street = (TextView) findViewById(R.id.StreetAddress);
        State = (TextView) findViewById(R.id.State);
        City = (TextView) findViewById(R.id.CustomerName);
        Pin = (TextView) findViewById(R.id.pin);
        ptp = (Button) findViewById(R.id.ptp);

        Bundle bundle = getIntent().getExtras();
        String seller = bundle.getString("seller_username");
        String price = bundle.getString("price");
        String current_U = bundle.getString("current_user");
        String img_url = bundle.getString("img_url");
        String caption = bundle.getString("caption");
        caption_view.setText(caption);
        Picasso.get().load(img_url).into(img_view);
        price_view.setText("₹ "+price);
        ptp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_details();
                setContentView(R.layout.order_success);
            }
        });
    }

    public void upload_details()
    {


        final FirebaseAuth mAuth;
        final FirebaseAuth.AuthStateListener mAuthListener;
        final FirebaseDatabase mFirebaseDatabase;
        final DatabaseReference myRef;
        final FirebaseMethods mFirebaseMethods;
        final StorageReference mStorageReference;
        final String userID;

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mStorageReference = FirebaseStorage.getInstance().getReference();
        final Bundle bundle = getIntent().getExtras();
        final String seller = bundle.getString("seller_username");
        final String price = bundle.getString("price");
        final String current_U = bundle.getString("current_user");
        final String img_url = bundle.getString("img_url");
        final String caption = bundle.getString("caption");


//        if(mAuth.getCurrentUser() != null){
//            userID = mAuth.getCurrentUser().getUid();
//        }
        final String key = myRef.child("buy").push().getKey();




        if(Cust_name.getText().equals("") && Phone.getText().equals("") && flat.getText().equals("") && Street.getText().equals("") && State.getText().equals("") && City.getText().equals("") && Pin.getText().equals("") )
        {
            Toast toast = Toast.makeText(getApplicationContext(), "All fields Are mandatory", Toast.LENGTH_SHORT); toast.show();
        }
        else
        {
            //Buy Database Entries
            //order no
            myRef.child("next_count").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String userID = mAuth.getCurrentUser().getUid();
                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    order_no = dataSnapshot.getValue(Integer.class);
                    Log.d(TAG, "onDataChange: order_no = " + order_no);
                    increment_order_count(order_no);
                    String seller_uid = bundle.getString("seller_uid");
                    Log.d(TAG, "upload_details: seller = " + seller);
                    myRef.child("buy").child(userID).child(key).child("seller").setValue(seller);
                    myRef.child("buy").child(userID).child(key).child("price").setValue(price);
                    myRef.child("buy").child(userID).child(key).child("order_id").setValue(key);
                    myRef.child("buy").child(userID).child(key).child("order_no").setValue(order_no);
                    myRef.child("buy").child(userID).child(key).child("name").setValue(Cust_name.getText().toString());
                    myRef.child("buy").child(userID).child(key).child("phone").setValue(Phone.getText().toString());
                    myRef.child("buy").child(userID).child(key).child("flat").setValue(flat.getText().toString());
                    myRef.child("buy").child(userID).child(key).child("street").setValue(Street.getText().toString());
                    myRef.child("buy").child(userID).child(key).child("state").setValue(State.getText().toString());
                    myRef.child("buy").child(userID).child(key).child("city").setValue(City.getText().toString());
                    myRef.child("buy").child(userID).child(key).child("pin").setValue(Pin.getText().toString());
                    myRef.child("buy").child(userID).child(key).child("imgurl").setValue(img_url);
                    myRef.child("buy").child(userID).child(key).child("caption").setValue(caption);
                    myRef.child("buy").child(userID).child(key).child("seller_id").setValue(seller_uid);
                    myRef.child("buy").child(userID).child(key).child("track").child("status").setValue("0");
                    myRef.child("buy").child(userID).child(key).child("date").setValue(date);
                    //sell Database Entries
                    myRef.child("sell").child(seller_uid).child(key).child("buyer").setValue(current_U);
                    myRef.child("sell").child(seller_uid).child(key).child("price").setValue(price);
                    myRef.child("sell").child(seller_uid).child(key).child("order_no").setValue(order_no);
                    myRef.child("sell").child(seller_uid).child(key).child("order_id").setValue(key);
                    myRef.child("sell").child(seller_uid).child(key).child("name").setValue(Cust_name.getText().toString());
                    myRef.child("sell").child(seller_uid).child(key).child("phone").setValue(Phone.getText().toString());
                    myRef.child("sell").child(seller_uid).child(key).child("flat").setValue(flat.getText().toString());
                    myRef.child("sell").child(seller_uid).child(key).child("street").setValue(Street.getText().toString());
                    myRef.child("sell").child(seller_uid).child(key).child("state").setValue(State.getText().toString());
                    myRef.child("sell").child(seller_uid).child(key).child("city").setValue(City.getText().toString());
                    myRef.child("sell").child(seller_uid).child(key).child("pin").setValue(Pin.getText().toString());
                    myRef.child("sell").child(seller_uid).child(key).child("imgurl").setValue(img_url);
                    myRef.child("sell").child(seller_uid).child(key).child("caption").setValue(caption);
                    myRef.child("sell").child(seller_uid).child(key).child("buyer_id").setValue(userID);
                    myRef.child("sell").child(seller_uid).child(key).child("track").child("status").setValue("0");
                    myRef.child("sell").child(seller_uid).child(key).child("date").setValue(date);

                    myRef.child("mywallet").child(userID).child(key).child("orderno").setValue(String.valueOf(order_no));
                    myRef.child("mywallet").child(userID).child(key).child("price").setValue(price);
                    myRef.child("mywallet").child(userID).child(key).child("date").setValue(date);
                    myRef.child("mywallet").child(userID).child(key).child("imgurl").setValue(img_url);
                    myRef.child("mywallet").child(userID).child(key).child("type").setValue("1");
                    myRef.child("mywallet").child(seller_uid).child(key).child("orderno").setValue(String.valueOf(order_no));
                    myRef.child("mywallet").child(seller_uid).child(key).child("price").setValue(price);
                    myRef.child("mywallet").child(seller_uid).child(key).child("date").setValue(date);
                    myRef.child("mywallet").child(seller_uid).child(key).child("imgurl").setValue(img_url);
                    myRef.child("mywallet").child(seller_uid).child(key).child("type").setValue("2");

                    //notification
                    final String mGroupId = myRef.child("notification").child(seller_uid).push().getKey();
                    myRef.child("notification").child(seller_uid).child(mGroupId).child("by_user").setValue(current_U);
                    myRef.child("notification").child(seller_uid).child(mGroupId).child("type").setValue("order");
                    myRef.child("notification").child(seller_uid).child(mGroupId).child("time").setValue(date);
                    myRef.child("notification").child(seller_uid).child(mGroupId).child("id").setValue(mGroupId);
                    myRef.child("notification").child(seller_uid).child(mGroupId).child("seen").setValue(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            //Wallet buy
            Toast toast = Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT); toast.show();
        }
    }

    public int increment_order_count(int count_no)
    {
        try {
            DatabaseReference myRef;
            myRef = FirebaseDatabase.getInstance().getReference();
            Log.d(TAG, "upload_details: order no = " + order_no);
            myRef.child("next_count").setValue(order_no + 1);
        }catch (Exception e)
        {
            Log.d(TAG, "increment_order_count: exception"+ e.toString());
        }
        return count_no-1;
    }


}
