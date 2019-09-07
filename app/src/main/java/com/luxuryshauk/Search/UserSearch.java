package com.luxuryshauk.Search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.Profile.ProfileActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.Utils.UserListAdapter;
import com.luxuryshauk.models.Photo;
import com.luxuryshauk.models.User;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;

public class UserSearch extends Fragment {

    private static String TAG = "UserSearch";

    private String title;
    private int page;
    private EditText searchString;
    private ArrayList<User> userList;
    private ListView userListView;
    private UserListAdapter userListAdapter;

    // newInstance constructor for creating fragment with arguments
    public static UserSearch newInstance(int page, String title) {
        UserSearch fragmentFirst = new UserSearch();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_search_layout, container, false);
        searchString = view.findViewById(R.id.search);
        userListView = view.findViewById(R.id.userresult);
        searchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = searchString.getText().toString().toLowerCase(Locale.getDefault());
                if(!text.isEmpty()) {
                    userList = new ArrayList<>();
                    searchForMatch(text);
                }
            }
        });
        return view;
    }

    private void searchForMatch(final String text)
    {
        userList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("users");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                        User user = singleSnapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: user model = " + user.getUsername());
                        try {
                            if (user.getUsername().contains(text)) {
                                Log.d(TAG, "onDataChange: Added!! user model = " + user.getUsername());
                                userList.add(user);
                            }
                        }catch (Exception e){}
                }
                updateUsersList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUsersList(){

        try {
//            mAdapter.notifyDataSetChanged();

            userListAdapter = new UserListAdapter(getActivity(), R.layout.layout_user_listitem, userList);

            userListView.setAdapter(userListAdapter);

            userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //navigate to profile activity
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                    intent.putExtra(getString(R.string.intent_user), userList.get(position));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }catch (Exception e){}
//        mAdapter.notifyDataSetChanged();
    }

}
