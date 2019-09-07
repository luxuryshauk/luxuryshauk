package com.luxuryshauk.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.luxuryshauk.Home.HomeActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.Utils.FirebaseMethods;
import com.luxuryshauk.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CashoutActivity extends AppCompatActivity {

    public static final String TAG = "Cashout Activity";
    Context mContext = CashoutActivity.this;
    public int order_no;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cashout);
        Bundle bundle = getIntent().getExtras();
        final float bal = bundle.getFloat("tbal");
        final TextView amount = findViewById(R.id.amount);
        final EditText holder = findViewById(R.id.holder);
        final EditText accno = findViewById(R.id.acc_no);
        final EditText bank = findViewById(R.id.bank_name);
        final EditText ifsc = findViewById(R.id.ifsc);
        final EditText phone = findViewById(R.id.phone);
        Button r_c = findViewById(R.id.casho_btn);

        amount.setText("Wallet Amount = ₹ "+bal +"/-");
        r_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String holder_s = holder.getText().toString();
                final String accno_s = accno.getText().toString();
                final String bank_s = bank.getText().toString();
                final String ifsc_s = ifsc.getText().toString();
                final String phone_s = phone.getText().toString();
                final String currentuid = FirebaseAuth.getInstance().getUid();
                if(!holder_s.equals("")&&!accno_s.equals("")&&!bank_s.equals("")&&!ifsc_s.equals(""))
                {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(currentuid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final User user_s = dataSnapshot.getValue(User.class);
                            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            final FirebaseAuth mAuth;
                            final FirebaseAuth.AuthStateListener mAuthListener;
                            final FirebaseDatabase mFirebaseDatabase;
                            final DatabaseReference myRef;
                            final FirebaseMethods mFirebaseMethods;
                            final StorageReference mStorageReference;
                            final String userID;
                            mAuth = FirebaseAuth.getInstance();
                            mFirebaseDatabase = FirebaseDatabase.getInstance();
                            final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                            myRef = mFirebaseDatabase.getReference();
                            myRef.child("next_count").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                    order_no = dataSnapshot.getValue(Integer.class);
                                    Log.d(TAG, "onDataChange: order_no = " + order_no);
                                    increment_order_count(order_no);
                                    final String key = myRef.child("buy").push().getKey();
                                    long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                                    myRef.child("mywallet").child(currentFirebaseUser.getUid()).child(key).child("price").setValue(String.valueOf(bal));
                                    myRef.child("mywallet").child(currentFirebaseUser.getUid()).child(key).child("date").setValue(date);
                                    myRef.child("mywallet").child(currentFirebaseUser.getUid()).child(key).child("type").setValue("3");
                                    myRef.child("mywallet").child(currentFirebaseUser.getUid()).child(key).child("orderno").setValue(String.valueOf("W_"+number));
                                    HomeActivity ha = new HomeActivity();
                                    String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    ha.sendEmail("CASHOUT REQUEST\n" +
                                                    "\nTransferred ID : " + "W_"+number+
                                                    "\n\nYou recieved a Cashout Request" +
                                                    "\nName of Reseller : "+user_s.getUsername()+
                                                    "\nEmail Id : " + user_s.getEmail()+
                                                    "\nPhone no : " + phone_s + "" +
                                                    "\nThe details of the bank provided to us are mentioned below :- " +
                                                    "\nAccount holder : "+holder_s+
                                                    "\nAccount no : "+accno_s+"" +
                                                    "\nBank Name : " + bank_s+"" +
                                                    "\nIFSC code : " + ifsc_s+"" +
                                                    "\nCashout Request Amount : "+ bal+"" +
                                                    "","LuxuryShauk Cashout Requested by " + user_s.getUsername(),"support@collectmoney.in");
                                    ha.sendEmail("CASHOUT REQUEST\n" +
                                            "\nYour Transferred ID : " + "W_"+number+
                                            "\n\nYour Cashout Request Details are : " +
                                            "\nUsername : "+user_s.getUsername()+
                                            "\nEmail Id : " + user_s.getEmail()+
                                            "\nPhone no : " + phone_s + "" +
                                            "\nThe details of the bank provided to us are mentioned below :- " +
                                            "\nAccount holder : " + holder_s+
                                            "\nAccount no : " + accno_s + "" +
                                            "\nBank Name : " + bank_s + "" +
                                            "\nIFSC code : " + ifsc_s + "" +
                                            "\nCashout Request Amount : " + bal+"" +
                                            "","Your LuxuryShauk Cashout Request is in process" +
                                            "\nFor any concern mail us on Luxuryshaukapp@gmail.com",user_s.getEmail());

                                    Toast.makeText(mContext,"Success",Toast.LENGTH_SHORT).show();
                                    setContentView(R.layout.cashout_success_fragment);
                                    Button goback = findViewById(R.id.goback);
                                    goback.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(CashoutActivity.this, HomeActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                    });
//                            Button back = findViewById(R.id.goback);
//                            back.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    finish();
//                                    startActivity(new Intent(CashoutActivity.this, HomeActivity.class));
//                                }
//                            });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    Toast.makeText(mContext,"All fields are mandatory",Toast.LENGTH_SHORT).show();
                }
            }
        });



        Log.d(TAG, "onCreate: total = "+bal);
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
