package com.luxuryshauk.Profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.R;
import com.luxuryshauk.Utils.FullScreenImage;
import com.luxuryshauk.models.order;

public class BuyProductTrackFragment extends Fragment {
    String TAG = "BuyProductTrackFragment";
    int RESULT_LOAD_IMAGE = 1;
    public static Context applicationContext;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_track_details_buyer, container, false);
        final TextView c_name = (TextView)view.findViewById(R.id.courier_service_name);
        String userID = FirebaseAuth.getInstance().getUid();
        final order Order = (order) getArguments().getSerializable("model");

        final TextView c_no = (TextView)view.findViewById(R.id.courier_number);
                        final TextView download = (TextView)view.findViewById(R.id.download);
                        final TextView status = (TextView)view.findViewById(R.id.status);

                        DatabaseReference t_ref = FirebaseDatabase.getInstance().getReference().child("buy").child(userID).child(Order.getOrder_id()).child("track");
                        t_ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final com.luxuryshauk.models.track Track = dataSnapshot.getValue(com.luxuryshauk.models.track.class);
                                if(Track.getStatus().equals("0"))
                                {
                                    status.setText("Not Yet Updated");
                                    download.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(getContext(),"Reciept is not yet updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    status.setText("Updated");
                                    c_name.setText(Track.getName());
                                    c_no.setText(Track.getNo());
                                    download.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("img_url",Track.getPic());
                                            Intent add = new Intent(getActivity(), FullScreenImage.class);
                                            add.putExtras(bundle);
                                            startActivity(add);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
        return view;
    }
}

