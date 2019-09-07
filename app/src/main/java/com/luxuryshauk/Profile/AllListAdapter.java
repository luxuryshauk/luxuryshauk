package com.luxuryshauk.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.R;
import com.luxuryshauk.models.UserAccountSettings;
import com.luxuryshauk.models.order_sell;
import com.luxuryshauk.models.wallet;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllListAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    private List<String> mNotification = null;
    private int layoutResource;
    private Context mContext;

    public AllListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mNotification = objects;
    }

    private static class ViewHolder {
        Spinner status;
        TextView order_id;
        ImageView product;
        TextView buyername;
        TextView caption,time,price;
        TextView update,release,refund;

    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = mInflater.inflate(layoutResource, parent, false);
        final DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
        final AllListAdapter.ViewHolder holder;
        holder = new AllListAdapter.ViewHolder();
        holder.status = (Spinner) convertView.findViewById(R.id.spinner);
        holder.order_id = (TextView)convertView.findViewById(R.id.order_id);
        holder.product = (ImageView)convertView.findViewById(R.id.product);
        holder.buyername = (TextView)convertView.findViewById(R.id.buyer_name);
        holder.caption = (TextView)convertView.findViewById(R.id.caption);
        holder.update = (TextView)convertView.findViewById(R.id.update);
        holder.refund = (TextView)convertView.findViewById(R.id.refund);
        holder.release = (TextView)convertView.findViewById(R.id.release);
        holder.time = (TextView)convertView.findViewById(R.id.time_date);
        holder.price = (TextView)convertView.findViewById(R.id.price);
        final String order_key = getItem(position);


//        holder.refund.setEnabled(false);
//        holder.release.setEnabled(false);

        dref.child("sell").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot ds : dataSnapshot.getChildren())
                {
                    for(final DataSnapshot ads : ds.getChildren()) {

                        holder.product.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dref.child("sell").child(ds.getKey()).child(ads.getKey())
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
                        });





                        if (ads.getKey().equals(order_key)) {
                            final order_sell os = ads.getValue(order_sell.class);
                            Picasso.get().load(os.getImgurl()).into(holder.product);
                            String userid = ds.getKey();
                            holder.caption.setText(os.getCaption());
                            holder.time.setText(os.getDate());
                            holder.price.setText("₹ " + os.getPrice());
                            holder.order_id.setText("ORDER ID #" + os.getOrder_no());
                            switch (os.getStatus()){
                                case 0:
                                    holder.status.setSelection(0);
                                    break;
                                case 1:
                                    holder.status.setSelection(1);
                                    break;
                                case 2:
                                    holder.status.setSelection(2);
                                    break;
                                case 3:
                                    holder.status.setSelection(3);
                                    break;
                                case 4:
                                    holder.status.setSelection(4);
                                    break;

                                default:
                                    break;
                            }
                            try {
                                if (os.getLock() == 1) {
                                    holder.update.setEnabled(false);
                                    holder.update.setBackgroundColor(Color.parseColor("#C0C0C0"));
                                    holder.refund.setEnabled(false);
                                    holder.refund.setBackgroundColor(Color.parseColor("#C0C0C0"));
                                    holder.release.setEnabled(false);
                                    holder.release.setBackgroundColor(Color.parseColor("#C0C0C0"));
                                }
                            }catch (Exception e){}


                            dref.child("user_account_settings").child(userid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    UserAccountSettings ua = dataSnapshot.getValue(UserAccountSettings.class);
                                    holder.buyername.setText(ua.getUsername() + " -> " + os.getBuyer());

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            holder.update.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int status=0;
                                    switch (holder.status.getSelectedItem().toString()){
                                        case "In Transit":
                                            status = 0;
                                            break;
                                        case "Issue in Transit":
                                            status = 1;
                                            break;
                                        case "Delivered":
                                            status = 2;
                                            break;
                                        case "Complaint":
                                            status = 3;
                                            break;
                                        case "Cancelled":
                                            status = 4;
                                            break;
                                        default:
                                                break;
                                    }
                                    final int status_f = status;
                                    dref.child("sell").child(ds.getKey()).child(ads.getKey()).child("status").setValue(status);
                                    dref.child("sell").child(ds.getKey()).child(ads.getKey()).child("status").setValue(status);

                                    if(os.getFromshare()==0)
                                    {
                                        dref.child("buy").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds : dataSnapshot.getChildren())
                                                {
                                                    for(DataSnapshot ads: ds.getChildren())
                                                    {
                                                        if(ads.getKey().equals(order_key))
                                                        {
                                                            dref.child("buy").child(ds.getKey()).child(ads.getKey()).child("status").setValue(status_f);
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    Toast.makeText(mContext,"Updated",Toast.LENGTH_SHORT).show();


                                }
                            });
                            holder.release.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    dref.child("sell").child(ds.getKey()).child(ads.getKey()).child("lock").setValue(1);
                                                    wallet Wallet = new wallet();
                                                    Wallet.setDate(os.getDate());
                                                    Wallet.setImgurl(os.getImgurl());
                                                    Wallet.setOrderno(os.getOrder_no());

                                                    String price = String.valueOf(Math.floor(Double.valueOf(os.getPrice())*.95));
                                                    Wallet.setPrice(price);
                                                    Wallet.setType("1");
                                                    dref.child("mywallet").child(ds.getKey()).push().setValue(Wallet);
                                                    Toast.makeText(mContext,"Payment Released",Toast.LENGTH_SHORT).show();
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:

                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setMessage("Are you really want to release payment?").setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();
                                }
                            });
                            // database.ref("mywallet").child(sellerid).child(key).set({
                            //     orderno: options.notes.order_id.toString(),
                            //     price: baseprice.toString(),
                            //     date: orderdate,
                            //     imgurl: chosen,
                            //     status:0,
                            //     type: "1"
                            // });
                            holder.refund.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which){
                                                case DialogInterface.BUTTON_POSITIVE:

                                                    dref.child("sell").child(ds.getKey()).child(ads.getKey()).child("lock").setValue(1);
                                                    wallet Wallet = new wallet();
                                                    Wallet.setDate(os.getDate());
                                                    Wallet.setImgurl(os.getImgurl());
                                                    Wallet.setOrderno(os.getOrder_no());
                                                    String price = String.valueOf(Math.ceil(Double.valueOf(os.getPrice())*.05));
                                                    Wallet.setPrice(price);
                                                    Wallet.setType("2");
                                                    dref.child("mywallet").child(ds.getKey()).push().setValue(Wallet);
                                                    Toast.makeText(mContext,"Payment Refunded",Toast.LENGTH_SHORT).show();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:

                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setMessage("Are you really want to Refund payment?").setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();
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
        String text = holder.status.getSelectedItem().toString();

        return convertView;
    }
}
