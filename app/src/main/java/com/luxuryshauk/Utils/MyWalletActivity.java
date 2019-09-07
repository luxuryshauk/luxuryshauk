package com.luxuryshauk.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luxuryshauk.R;
import com.luxuryshauk.models.wallet;
import com.squareup.picasso.Picasso;

public class MyWalletActivity extends AppCompatActivity {

    String TAG = "Mywallet";

    FirebaseListAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    FirebaseMethods mFirebaseMethods;
    StorageReference mStorageReference;
    String userID;
    ListView w_sum;

    long total=0,sell_total=0,buy_total=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mywallet);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        userID = mAuth.getCurrentUser().getUid();
        w_sum = (ListView) findViewById(R.id.wallet_summary);
        Query mQueryRef = myRef.child("mywallet").child(userID);
        FirebaseListOptions<wallet> options = new FirebaseListOptions.Builder<wallet>()
                .setLayout(R.layout.snippet_wallet)
                .setQuery(mQueryRef, wallet.class)
                .setLifecycleOwner(MyWalletActivity.this)
                .build();
        Log.d(TAG, "onCreateView: adapter starting");
        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
                Log.d(TAG, "populateView: adapter started");
                TextView no = (TextView) v.findViewById(R.id.order_id);
                TextView caption = (TextView) v.findViewById(R.id.caption);
                final TextView date_1 = (TextView) v.findViewById(R.id.date);
                TextView price = (TextView) v.findViewById(R.id.price);
                ImageView img = (ImageView)v.findViewById(R.id.product);
                wallet Wallet = (wallet) model;
                if(Wallet.getType().equals("1")) {
                    price.setText("+ ₹ " + Wallet.getPrice());
                    no.setText("ORDER ID #" + Wallet.getOrderno());
                    caption.setText("Earned for Order");
                    Picasso.get().load(Wallet.getImgurl()).into(img);
                    date_1.setText(Wallet.getDate());
                }else
                {
                    price.setText("+ ₹ " + Wallet.getPrice());
                    no.setText("ORDER ID #" + Wallet.getOrderno());
                    caption.setText("Spend on Order");
                    Picasso.get().load(Wallet.getImgurl()).into(img);
                    date_1.setText(Wallet.getDate());
                }
                Log.d(TAG, "populateView: working : true");
            }
        };

        w_sum.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
