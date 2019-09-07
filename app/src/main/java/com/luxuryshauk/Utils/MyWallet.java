package com.luxuryshauk.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.luxuryshauk.Profile.CashoutActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.models.wallet;

import java.util.ArrayList;
import java.util.Arrays;

public class MyWallet extends Fragment {

    String TAG = "Mywallet";

    WalletListAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    FirebaseMethods mFirebaseMethods;
    StorageReference mStorageReference;
    String userID;
    ListView w_sum;
    public float blnce=0f;
    boolean[] added;
    int count=1;
    ArrayList<wallet> wallets,rev;

    long total=0,sell_total=0,buy_total=0;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.layout_mywallet, container, false);
        added = new boolean[10000000];
        Arrays.fill(added, Boolean.FALSE);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        userID = mAuth.getCurrentUser().getUid();
        final TextView balance = (TextView) view.findViewById(R.id.Balance_wallet);
        final Button cashout_btn = (Button) view.findViewById(R.id.cashout_btn);
        wallets = new ArrayList<>();
        rev = new ArrayList<>();

        w_sum = (ListView) view.findViewById(R.id.wallet_summary);
        Query mQueryRef = myRef.child("mywallet").child(userID);

        mQueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blnce = 0f;
                for (DataSnapshot ds:dataSnapshot.getChildren() ) {
                    wallet w = ds.getValue(wallet.class);
                    try {
                        if (w.getType().equals("1")) {
                            //Earned
                            blnce += Float.valueOf(w.getPrice());
                        } else if (w.getType().equals("2")) {
                            //Spent
                            blnce -= Float.valueOf(w.getPrice());
                        } else if (w.getType().equals("3")) {
                            //withdrawl
                            blnce -= Float.valueOf(w.getPrice());
                        }
                    }catch (Exception e){}
                }
                balance.setText("= ₹ " + String.valueOf(blnce) + "/-");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mQueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    wallet w = ds.getValue(wallet.class);
                    wallets.add(w);
                }
                if (wallets.size() == dataSnapshot.getChildrenCount()) {
                    int reverseArrayCounter = wallets.size() - 1;

                    for (int i = reverseArrayCounter; i >= 0; i--) {
                        //Log.d(TAG, "onCreate: reversing array = " + buylist.get(i).getTarget());
                        rev.add(wallets.get(i));
                    }
                }
                try {
                    adapter = new WalletListAdapter(getContext(), R.layout.snippet_wallet, rev);
                    w_sum.setAdapter(adapter);
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







        FirebaseListOptions<wallet> options = new FirebaseListOptions.Builder<wallet>()
                .setLayout(R.layout.snippet_wallet)
                .setQuery(mQueryRef, wallet.class)
                .setLifecycleOwner(getActivity())
                .build();
        Log.d(TAG, "onCreateView: adapter starting");
//        adapter = new FirebaseListAdapter(options) {
//            @Override
//            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
//                Log.d(TAG, "populateView: adapter started");
//                TextView no = (TextView) v.findViewById(R.id.order_id);
//                Log.d(TAG, "populateView: balance = " + blnce);
//                TextView caption = (TextView) v.findViewById(R.id.caption);
//                final TextView date_1 = (TextView) v.findViewById(R.id.date);
//                TextView price = (TextView) v.findViewById(R.id.price);
//                ImageView img = (ImageView) v.findViewById(R.id.product);
//                wallet Wallet = (wallet) model;
////                Log.d(TAG, "populateView: array is : " + Integer.parseInt(Wallet.getOrderno()));
//                Log.d(TAG, "populateView: array is string : " + Wallet.getOrderno());
//                //Log.d(TAG, "populateView: array is bool : " + added[Integer.parseInt(Wallet.getOrderno())]);
//                try {
////                    if (Wallet.getType().equals("1")) {
////                        price.setText("+ ₹ " + Wallet.getPrice());
////                        no.setText("ORDER ID #" + Wallet.getOrderno());
//////                        if (!added[Integer.parseInt(Wallet.getOrderno())]) {
//////                           // blnce += Float.valueOf(Wallet.getPrice());
//////                            added[Integer.parseInt(Wallet.getOrderno())] = true;
//////                        }
////                        caption.setText("Earned for Order");
////                        Log.d(TAG, "populateView: earn balance = " + blnce);
////                        Picasso.get().load(Wallet.getImgurl()).into(img);
////                        date_1.setText(Wallet.getDate());
////                    } else if (Wallet.getType().equals("2")) {
////                        price.setText("- ₹ " + Wallet.getPrice());
////                        no.setText("ORDER ID #" + Wallet.getOrderno());
//////                        if (!added[Integer.parseInt(Wallet.getOrderno())]) {
//////                         //   blnce -= Float.valueOf(Wallet.getPrice());
//////                            added[Integer.parseInt(Wallet.getOrderno())] = true;
//////                        }
////                        caption.setText("Penalty on Order (-5%)");
////                        Log.d(TAG, "populateView: spend balance = " + blnce);
////                        Picasso.get().load(Wallet.getImgurl()).into(img);
////                        date_1.setText(Wallet.getDate());
////                    } else if (Wallet.getType().equals("3")) {
////                        price.setText("- ₹ " + Wallet.getPrice());
////                        no.setText("ORDER ID #" + Wallet.getOrderno());
//////                        if (!added[Integer.parseInt(Wallet.getOrderno())]) {
//////                        //    blnce -= Float.valueOf(Wallet.getPrice());
//////                            added[Integer.parseInt(Wallet.getOrderno())] = true;
//////                        }
////                        Log.d(TAG, "populateView: withdrawl balance = " + blnce);
////                        caption.setText("Withdrawal");
////                        date_1.setText(Wallet.getDate());
////                    }
////
////                    Log.d(TAG, "populateView: final balance = " + blnce);
////                    Log.d(TAG, "populateView: working : true");
//                    //balance.setText("= ₹ " + String.valueOf(blnce) + "/-");
//                }catch (NullPointerException e)
//                {
//                    Toast.makeText(getContext(), "You don't have sufficient balance", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "populateView: exception " + e.toString());
//                }
//            }
//        };
        cashout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(blnce>500) {
                    Log.d(TAG, "populateView: final balance(inside button on click) =  " + blnce);
                    Intent intent = new Intent(getActivity(), CashoutActivity.class);
                    intent.putExtra("tbal", blnce);
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    Toast.makeText(getContext(), "Minimum cashout limit is 500", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.d(TAG, "onCreateView: final price = "+balance.getText().toString());
//        w_sum.setAdapter(adapter);

        return view;
    }
}
