package com.luxuryshauk.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllOrders extends Fragment {

    AllListAdapter adapter;
    Context mContext;
    ArrayList<String> rev , rev1;
    ListView lv;
    String TAG = "AllOrders";

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.layout_all_record, container, false);
        mContext = getContext();
        lv = view.findViewById(R.id.order_all);
        FirebaseDatabase.getInstance().getReference().child("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rev = new ArrayList<>();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Map<String, Object> objectMap = (HashMap<String, Object>) ds.getValue();
                    String orders = objectMap.get("order_id").toString();
                    Log.d(TAG, "onDataChange: order id = " + orders);
                    rev.add(orders);
                }
                int reverseArrayCounter = rev.size() - 1;
                rev1 = new ArrayList<>();

                for (int i = reverseArrayCounter; i >= 0; i--) {
                    //Log.d(TAG, "onCreate: reversing array = " + buylist.get(i).getTarget());
                    rev1.add(rev.get(i));
                }
                adapter = new AllListAdapter(mContext, R.layout.fragment_orders_admin, rev1);
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return view;
    }

}
