package com.luxuryshauk.Home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.luxuryshauk.Utils.UserAdapter;
import com.luxuryshauk.models.Chatlist;
import com.luxuryshauk.models.Token;
import com.luxuryshauk.models.User;
import com.luxuryshauk.R;

import java.util.ArrayList;
import java.util.List;

import static com.nostra13.universalimageloader.core.ImageLoader.TAG;


/**
 * Created by User on 5/28/2017.
 */

public class MessagesFragment extends Fragment {
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;
    TextView nochat;

    private List<Chatlist> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        nochat = view.findViewById(R.id.nochats);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot ds: snapshot.getChildren()) {
                        Chatlist chatlist = ds.getValue(Chatlist.class);
                        Log.d(TAG, "onDataChange: user chat list = "+ ds.getValue().toString());
                        //String[] id = chatlist.getId().split("-");
                        //chatlist.setId(id[0]);
                        usersList.add(chatlist);

                        nochat.setText("");
                    }
                }
                    Log.d(TAG, "onDataChange: current chat userlist = " + usersList.toString());
                for(Chatlist c : usersList)
                {
                    Log.d(TAG, "onDataChange: userslist = " + c.getId());
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (Chatlist chatlist : usersList){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: chat user = " + user.toString());

                            try{
                                //if (user.getUser_id().equals(chatlist.getId())){
                                if(chatlist.getId().equals(user.getUser_id())){
                                    mUsers.add(user);
                                    Log.d(TAG, "onDataChange: selected chat user = " + user.toString());
                                }
                            }catch (Exception e)
                            {
                                System.out.println(e);
                            }
                    }
                }
                for(User u : mUsers)
                {
                    Log.d(TAG, "onDataChange: all users this way = " + u.getUsername());
                }
                ArrayList<User> rev = new ArrayList<User>();
                for (int i = mUsers.size() - 1; i >= 0; i--) {

                    // Append the elements in reverse order
                    rev.add(mUsers.get(i));
                }
                //userAdapter = new UserAdapter(getContext(), mUsers, true);
                userAdapter = new UserAdapter(getContext(), rev, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
