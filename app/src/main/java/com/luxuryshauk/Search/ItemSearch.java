package com.luxuryshauk.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.R;
import com.luxuryshauk.Utils.ItemListAdapter;
import com.luxuryshauk.Utils.ViewMultiplePostFragment;
import com.luxuryshauk.models.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public class ItemSearch extends Fragment {

    private String TAG = "Item search";

    private ArrayList<Photo> itemList;
    private ListView itemListView;

    private String title;
    private int page;
    private EditText searchString;
    private ItemListAdapter itemListAdapter;
    List<String> img_urls;
    String img_url;
    Photo p = new Photo();





    // newInstance constructor for creating fragment with arguments
    public static ItemSearch newInstance(int page, String title) {
        ItemSearch fragmentFirst = new ItemSearch();
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
        View view = inflater.inflate(R.layout.item_search_layout, container, false);
        searchString = view.findViewById(R.id.search);
        itemListView = view.findViewById(R.id.itemlist);
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
                    itemList = new ArrayList<>();
                    searchForMatch(text);
                }
            }
        });
        return view;
    }

    private void searchForMatch(final String text)
    {
        itemList.clear();
        FirebaseDatabase.getInstance().getReference().child("photos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
//                    Log.d(TAG, "onDataChange: test input = " + text + " \n ds = " + ds.toString() );
                    try {
                        Long price = ds.child("price").getValue(Long.class);
                        String caption = ds.child("caption").getValue(String.class);
                        Integer type = ds.child("type").getValue(Integer.class);
                        String id = ds.child("photo_id").getValue(String.class);
                        String tag = ds.child("tags").getValue(String.class);
                        String imgurl = ds.child("image_path").getValue(String.class);
                        String userid = ds.child("user_id").getValue(String.class);
//                        if(imgurl.contains("["))
//                        {
//
//                        }

                        Photo newp = new Photo();
                        Log.d(TAG, "onDataChange: newp = " + price + caption + type + id + tag + imgurl);
                        newp.setTags(tag);
                        newp.setCaption(caption);
                        newp.setType(type);
                        newp.setPhoto_id(id);
                        newp.setPrice(price);
                        newp.setImage_path(imgurl);
                        newp.setUser_id(userid);
                        if (tag.contains(text)) {
                            itemList.add(newp);
                        }
                    }catch (Exception e){}
                }
                Collections.reverse(itemList);
                updateItemList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateItemList(){

        Log.d(TAG, "onDataChange: item size = " + itemList.size());

            itemListAdapter = new ItemListAdapter(getActivity(), R.layout.layout_user_listitem, itemList);
            itemListView.setAdapter(itemListAdapter);
            itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String[] img = itemList.get(position).getImage_path().split(", ");

                    String none = "none";
                    Bundle bundle = new Bundle();
                    bundle.putString("path", none);
                    bundle.putStringArray("url", img);
                    bundle.putString("id", itemList.get(position).getPhoto_id());
                    bundle.putString("type", String.valueOf(itemList.get(position).getType()));
                    bundle.putParcelable("intent_user", itemList.get(position));
                    bundle.putString("fromsearch", "1");
                    ViewMultiplePostFragment view_post = new ViewMultiplePostFragment();
                    view_post.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, view_post).addToBackStack("ViewMultplePostFragment").commit();

                }
            });
//        mAdapter1.notifyDataSetChanged();


    }

}
