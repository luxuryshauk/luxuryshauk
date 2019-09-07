package com.luxuryshauk.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.R;
import com.luxuryshauk.models.UserAccountSettings;

import java.util.ArrayList;

public class ManageTopSellers extends Fragment {

    String[] ts;
    ArrayList<String> tslist;
    ArrayList<String> currentTopsellers;
    ArrayList<String> currentTopsellersusernames;
    String TAG = "ManageTopSellers";
    EditText[] topseller = new EditText[10];
    public int counter = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout_manage_top_sellers, container, false);

//        final EditText ts1 = view.findViewById(R.id.ts1);
//        final EditText ts2 = view.findViewById(R.id.ts2);
//        final EditText ts3 = view.findViewById(R.id.ts3);
//        final EditText ts4 = view.findViewById(R.id.ts4);
//        final EditText ts5 = view.findViewById(R.id.ts5);
//        final EditText ts6 = view.findViewById(R.id.ts6);
//        final EditText ts7 = view.findViewById(R.id.ts7);
        tslist = new ArrayList<>();
        topseller[0] = view.findViewById(R.id.ts1);
        topseller[1] = view.findViewById(R.id.ts2);
        topseller[2] = view.findViewById(R.id.ts3);
        topseller[3] = view.findViewById(R.id.ts4);
        topseller[4] = view.findViewById(R.id.ts5);
        topseller[5] = view.findViewById(R.id.ts6);
        topseller[6] = view.findViewById(R.id.ts7);

        ts = new String[7];
        currentTopsellers = new ArrayList<>();
        currentTopsellersusernames = new ArrayList<>();
        final DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
        dref.child("top_sellers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {

//                    currentTopsellers.add(ds.getKey());
                    dref.child("user_account_settings").child(ds.getKey()).child("username").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                topseller[counter].setText(dataSnapshot.getValue(String.class));
                                counter++;
                            }catch (Exception e){}

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


//                    Log.d(TAG, "onDataChange: inside current top sellers : "+ds.getKey());
//                    for(String s : currentTopsellers)
//                    {
//                        Log.d(TAG, "onDataChange: in inside current top sellers : " + s);
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "onDataChange: outside current top sellers : " + currentTopsellers.size());

        for(String s : currentTopsellers)
        {
            Log.d(TAG, "onDataChange: outside current top sellers : " + s);
        }

//        for(String s: currentTopsellers)
//        {
//            dref.child("user_account_settings").child(s).child("username").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String username = dataSnapshot.getValue(String.class);
//                    currentTopsellersusernames.add(username);
//                    try{
//
//                        ts1.setText(currentTopsellersusernames.get(0));
//                        ts2.setText(currentTopsellersusernames.get(1));
//                        ts3.setText(currentTopsellersusernames.get(2));
//                        ts4.setText(currentTopsellersusernames.get(3));
//                        ts5.setText(currentTopsellersusernames.get(4));
//                        ts6.setText(currentTopsellersusernames.get(5));
//                        ts7.setText(currentTopsellersusernames.get(6));
//                    }catch (Exception e){}
//                    for(String t : currentTopsellersusernames)
//                    {
//                        Log.d(TAG, "onCreateView: top sellers username inside = " + t);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
        for(String t : currentTopsellersusernames)
        {
            Log.d(TAG, "onCreateView: top sellers username = " + t);
        }

//        try{
//
//            ts1.setText(currentTopsellersusernames.get(0));
//            ts2.setText(currentTopsellersusernames.get(1));
//            ts3.setText(currentTopsellersusernames.get(2));
//            ts4.setText(currentTopsellersusernames.get(3));
//            ts5.setText(currentTopsellersusernames.get(4));
//            ts6.setText(currentTopsellersusernames.get(5));
//            ts7.setText(currentTopsellersusernames.get(6));
//        }catch (Exception e){}


        Button update = view.findViewById(R.id.update);



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("top_sellers")
                        .setValue(null);
                topseller[0] = view.findViewById(R.id.ts1);
                topseller[1] = view.findViewById(R.id.ts2);
                topseller[2] = view.findViewById(R.id.ts3);
                topseller[3] = view.findViewById(R.id.ts4);
                topseller[4] = view.findViewById(R.id.ts5);
                topseller[5] = view.findViewById(R.id.ts6);
                topseller[6] = view.findViewById(R.id.ts7);
                ts[0] = topseller[0].getText().toString().trim().toLowerCase();
                ts[1] = topseller[1].getText().toString().trim().toLowerCase();
                ts[2] = topseller[2].getText().toString().trim().toLowerCase();
                ts[3] = topseller[3].getText().toString().trim().toLowerCase();
                ts[4] = topseller[4].getText().toString().trim().toLowerCase();
                ts[5] = topseller[5].getText().toString().trim().toLowerCase();
                ts[6] = topseller[6].getText().toString().trim().toLowerCase();
                for(final String ts_s : ts){
                    Log.d(TAG, "onDataChange: top seller input = " + ts_s);
                    FirebaseDatabase.getInstance().getReference()
                            .child("user_account_settings").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                UserAccountSettings uas =ds.getValue(UserAccountSettings.class);
                                Log.d(TAG, "onDataChange: top seller input = " + ts_s);
                                Log.d(TAG, "onDataChange: top seller input username db = " + uas.getUsername() + " bool = " + uas.getUsername().equals(ts_s));
                                if(uas.getUsername().equals(ts_s))
                                {
                                    tslist.add(ds.getKey());

                                }
                            }
                            for(String u : tslist) {
                                Log.d(TAG, "onDataChange: users list = " + u);
                            }

                            try {
                                for (String ts : tslist) {
                                    FirebaseDatabase.getInstance().getReference().child("top_sellers")
                                            .child(ts).setValue(1);
                                }
                            }catch (Exception e){}

                            Toast.makeText(getContext(),"Top sellers updated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });




        return view;
    }
}
