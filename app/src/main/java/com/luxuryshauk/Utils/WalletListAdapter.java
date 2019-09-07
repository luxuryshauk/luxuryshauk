package com.luxuryshauk.Utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luxuryshauk.R;
import com.luxuryshauk.models.wallet;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WalletListAdapter extends ArrayAdapter<wallet> {
    private LayoutInflater mInflater;
    private List<wallet> mNotification = null;
    private int layoutResource;
    private Context mContext;

    public WalletListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<wallet> objects) {
        super(context, resource, objects);

        try {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutResource = resource;
            this.mNotification = objects;
        }catch (Exception e){}
    }

    private static class ViewHolder {
        TextView no;
        TextView caption;
        TextView date_1;
        TextView price;
        ImageView img;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        final WalletListAdapter.ViewHolder holder;
//        if(convertView == null){
        convertView = mInflater.inflate(layoutResource, parent, false);
        holder = new WalletListAdapter.ViewHolder();
        holder.no = convertView.findViewById(R.id.order_id);
        holder.price = convertView.findViewById(R.id.price);
        holder.date_1 = convertView.findViewById(R.id.date);
        holder.caption = convertView.findViewById(R.id.caption);
        holder.img = convertView.findViewById(R.id.product);

        if (getItem(position).getType().equals("1")) {
            holder.price.setText("+ ₹ " + getItem(position).getPrice());
            holder.no.setText("ORDER ID #" + getItem(position).getOrderno());
//                        if (!added[Integer.parseInt(Wallet.getOrderno())]) {
//                           // blnce += Float.valueOf(Wallet.getPrice());
//                            added[Integer.parseInt(Wallet.getOrderno())] = true;
//                        }
            holder.caption.setText("Earned for Order");
//            Log.d(TAG, "populateView: earn balance = " + blnce);
            Picasso.get().load(getItem(position).getImgurl()).into(holder.img);
            holder.date_1.setText(getItem(position).getDate());
        } else if (getItem(position).getType().equals("2")) {
            holder.price.setText("- ₹ " + getItem(position).getPrice());
            holder.no.setText("ORDER ID #" + getItem(position).getOrderno());
//                        if (!added[Integer.parseInt(Wallet.getOrderno())]) {
//                         //   blnce -= Float.valueOf(Wallet.getPrice());
//                            added[Integer.parseInt(Wallet.getOrderno())] = true;
//                        }
            holder.caption.setText("Penalty on Order (-5%)");
//            Log.d(TAG, "populateView: spend balance = " + blnce);
            Picasso.get().load(getItem(position).getImgurl()).into(holder.img);
            holder.date_1.setText(getItem(position).getDate());
        } else if (getItem(position).getType().equals("3")) {
            holder.price.setText("- ₹ " + getItem(position).getPrice());
            holder.no.setText("ORDER ID #" + getItem(position).getOrderno());
//                        if (!added[Integer.parseInt(Wallet.getOrderno())]) {
//                        //    blnce -= Float.valueOf(Wallet.getPrice());
//                            added[Integer.parseInt(Wallet.getOrderno())] = true;
//                        }
//            Log.d(TAG, "populateView: withdrawl balance = " + blnce);
            holder.caption.setText("Withdrawal");
            holder.date_1.setText(getItem(position).getDate());
        }
//
//        Log.d(TAG, "populateView: final balance = " + blnce);
//        Log.d(TAG, "populateView: working : true");
        return convertView;

    }
}
