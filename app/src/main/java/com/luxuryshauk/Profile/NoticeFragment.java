package com.luxuryshauk.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.R;

public class NoticeFragment extends Fragment {
    EditText noticeText;
    Button settext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notice_layout, container, false);
        noticeText = view.findViewById(R.id.notice);
        settext = view.findViewById(R.id.settext);

        FirebaseDatabase.getInstance().getReference().child("important").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imp = dataSnapshot.getValue(String.class);
                noticeText.setText(imp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        settext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("important").setValue(noticeText.getText().toString());
                Toast.makeText(getContext(), "Updated",Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
