package com.luxuryshauk.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luxuryshauk.Utils.GridImageAdapter;
import com.luxuryshauk.Utils.ImageAdapter;
import com.luxuryshauk.Utils.ItemListAdapter;
import com.luxuryshauk.Utils.ViewMultiplePostFragment;
import com.luxuryshauk.models.Comment;
import com.luxuryshauk.models.Like;
import com.luxuryshauk.models.Photo;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.luxuryshauk.Profile.ProfileActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.Utils.BottomNavigationViewHelper;
import com.luxuryshauk.Utils.UserListAdapter;
import com.luxuryshauk.models.User;
import com.luxuryshauk.models.UserAccountSettings;

/**
 * Created by User on 5/28/2017.
 */

public class SearchActivity extends AppCompatActivity{
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;
    private static final int NUM_GRID_COLUMNS = 3;


    private Context mContext = SearchActivity.this;

    //widgets
    private EditText mSearchParam;
    private GridView gridView;
    private ListView mListView;
    private ListView mListView1;
    private TextView usertext;
    private TextView itemText;
    LinkedHashSet<User> hashSet;
    LinkedHashSet<Photo> hashSet1;
    private List<Photo> rev;
    //vars
    private List<User> mUserList;
    private List<Photo> mUserList1;
    private ItemListAdapter mAdapter1;
    private UserListAdapter mAdapter;

    private Button search_btn;
    private TextView empty;
    private FrameLayout mFrameLayout;
    private int something_found=0,item_found=0;
    Photo p = new Photo();
    List<String> img_urls;
    String img_url;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        gridView = findViewById(R.id.grid_view);
        usertext = findViewById(R.id.usertext);
        itemText = findViewById(R.id.itemText);
        mSearchParam = (EditText) findViewById(R.id.search);
        mListView = (ListView) findViewById(R.id.listView);
        mListView1 = (ListView) findViewById(R.id.listView1);
        search_btn = (Button) findViewById(R.id.search_btn);
        empty = (TextView)findViewById(R.id.empty);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        usertext.setVisibility(View.GONE);
        itemText.setVisibility(View.GONE);

        Log.d(TAG, "onCreate: started.");

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }
        });


        mSearchParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SearchItemUser.class);//ACTIVITY_NUM = 4
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        mUserList = new ArrayList<>();
        mUserList1 = new ArrayList<>();
//        searchForMatch("h");
        initRecentProducts();


        hideSoftKeyboard();
        setupBottomNavigationView();
        initTextListener();
    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: initializing");

        mUserList = new ArrayList<>();
        mUserList1 = new ArrayList<>();

        empty.setVisibility(View.GONE);
        itemText.setVisibility(View.VISIBLE);
        usertext.setVisibility(View.VISIBLE);



//                    mUserList = new ArrayList<>();
       /* DatabaseReference allItemList = FirebaseDatabase.getInstance().getReference().child("photos");
        allItemList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserList1 = new ArrayList<>();

                for(DataSnapshot ads : dataSnapshot.getChildren()) {
//                    Photo p = ads.getValue(Photo.class);
//                            Log.d(TAG, "onDataChange: ads = " + ads.toString());
//                    mUserList.add(singleSnapshot.getValue(User.class));
                    Photo p = ads.getValue(Photo.class);

                    mUserList1.add(p);
                }
                Collections.reverse(mUserList1);
                mAdapter1 = new ItemListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList1);
                mListView1.setAdapter(mAdapter1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                if(!text.isEmpty()) {
                    mUserList = new ArrayList<>();
                    mUserList1 = new ArrayList<>();
                    searchForMatch(text);
                }
                else
                {

                    mAdapter = new UserListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList);
                    mListView.setAdapter(mAdapter);

                    empty.setVisibility(View.VISIBLE);
                    itemText.setVisibility(View.GONE);
                    usertext.setVisibility(View.GONE);
                    mUserList = new ArrayList<>();
                    mUserList1 = new ArrayList<>();
                    mAdapter1 = new ItemListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList1);
                    mAdapter = new UserListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList);
                    mListView1.setAdapter(mAdapter1);
                    mListView.setAdapter(mAdapter);
                }
            }
        });
    }

    private void searchForMatch(String keyword){
        mListView1.setMinimumHeight(300);

        Log.d(TAG, "searchForMatch: searching for a match: " + keyword);
        mUserList.clear();
        mUserList1.clear();
        //update the users list view
        if(keyword.equals("")){
            empty.setVisibility(View.VISIBLE);
            itemText.setVisibility(View.GONE);
            usertext.setVisibility(View.GONE);

        }else{
            something_found = 0;
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_username)).startAt(keyword)
                    .endAt(keyword+"\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());
                        something_found = 1;
                        mUserList.add(singleSnapshot.getValue(User.class));
                        //update the users list view
                        updateUsersList();
                    }

                    if(something_found==1)
                    {
                        empty.setVisibility(View.GONE);
                        usertext.setVisibility(View.VISIBLE);
                    }
                    else {
                        mUserList = new ArrayList<>();
                        mAdapter = new UserListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList);
                        empty.setVisibility(View.VISIBLE);
                        usertext.setVisibility(View.GONE);
                    }
                    try {
                        mListView.setAdapter(mAdapter);

                        mAdapter.notifyDataSetChanged();
                    }catch (Exception e){}




                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            item_found = 0;

            //items
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
            Query query1 = reference1.child("photos")
                    .orderByChild("tags").startAt(keyword)
                    .endAt(keyword+"\uf8ff");
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


//                    String model = dataSnapshot.getValue(String.class);
                    for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//                        Log.d(TAG, "onDataChange: found item:" + singleSnapshot.getValue(Photo.class).toString());
                        try {
                            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                            };
                            img_urls = singleSnapshot.child("image_path").getValue(t);
                            Log.d(TAG, "onDataChange: imgurl new = "+ img_urls);
                            img_url = "";
                            for (String s : img_urls)
                            {
                                img_url += s + ", ";
                                Log.d(TAG, "onDataChange: imgurl appending = "+ img_url);
                            }

                        }catch (Exception e)
                        {
                            img_url = singleSnapshot.child("image_path").getValue(String.class);
                        }

                        long price = singleSnapshot.child("price").getValue(long.class);
                        String caption = singleSnapshot.child("caption").getValue(String.class);
                        int type = singleSnapshot.child("type").getValue(Integer.class);
                        String id = singleSnapshot.child("photo_id").getValue(String.class);

                        p.setImage_path(img_url);
                        p.setPrice(price);
                        p.setCaption(caption);
                        p.setType(type);
                        p.setPhoto_id(id);
                        Log.d(TAG, "onDataChange: photo model = " + p.toString());

                        item_found = 1;
                        mUserList1.add(p);
                        //update the users list view
                        updateItemList();
                    }


                    if(item_found == 1)
                    {
                        empty.setVisibility(View.GONE);
                        itemText.setVisibility(View.VISIBLE);
                    }
                    else if(item_found == 0 && something_found == 0){
                        mUserList1 = new ArrayList<>();
                        mAdapter1 = new ItemListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList1);
                        empty.setVisibility(View.VISIBLE);
                        itemText.setVisibility(View.GONE);
                    }
                    try {
                        mListView1.setAdapter(mAdapter1);
                        mAdapter1.notifyDataSetChanged();
                    }catch (Exception e){}

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void updateUsersList(){

        Log.d(TAG, "updateUsersList: updating users list");
        try {
//            mAdapter.notifyDataSetChanged();
            hashSet = new LinkedHashSet<>(mUserList);

            mUserList = new ArrayList<>(hashSet);

            mAdapter = new UserListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList);

            mListView.setAdapter(mAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: selected user: " + mUserList.get(position).toString());
                    finishAffinity();

                    //navigate to profile activity
                    Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                    intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                    intent.putExtra(getString(R.string.intent_user), mUserList.get(position));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }catch (Exception e){}
//        mAdapter.notifyDataSetChanged();
    }

    private void updateItemList(){

        try {
//            mAdapter1.notifyDataSetChanged();
            Log.d(TAG, "updateUsersList: updating users list");
            hashSet1 = new LinkedHashSet<>(mUserList1);
            mUserList1 = new ArrayList<>(hashSet1);
            mAdapter1 = new ItemListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList1);
            mListView1.setAdapter(mAdapter1);
            mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: selected user: " + mUserList1.get(position).toString());

                    //navigate to profile activity
//                Intent intent =  new Intent(SearchActivity.this, ProfileActivity.class);
//                intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
//                intent.putExtra(getString(R.string.intent_user), mUserList1.get(position));
//                startActivity(intent);
                    String[] img = mUserList1.get(position).getImage_path().split(", ");

                    String none = "none";
                    Bundle bundle = new Bundle();
                    bundle.putString("path", none);
                    bundle.putStringArray("url", img);
                    bundle.putString("id", mUserList1.get(position).getPhoto_id());
                    bundle.putString("type", String.valueOf(mUserList1.get(position).getType()));
                    bundle.putParcelable("intent_user", mUserList1.get(position));
                    bundle.putString("fromsearch", "1");
                    ViewMultiplePostFragment view_post = new ViewMultiplePostFragment();
                    view_post.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, view_post).addToBackStack("ViewMultplePostFragment").commit();

                }
            });
        }catch (Exception e){}
//        mAdapter1.notifyDataSetChanged();


    }

    public void initRecentProducts()
    {
        Log.d(TAG, "setupGridView: Setting up image grid.");

        final ArrayList<Photo> photos = new ArrayList<>();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("photos");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    try {
                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                        photo.setType(Integer.valueOf(objectMap.get("type").toString()));

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }
                        photo.setLikes(likesList);
                        Log.d(TAG, "onDataChange: photo test update = "+photo.toString());
                        photos.add(photo);
                    }catch(NullPointerException e){
                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage() );
                    }
                }

                rev = new ArrayList<Photo>();
                for (int i = photos.size() - 1; i >= 0; i--) {
                    // Append the elements in reverse order
                    rev.add(photos.get(i));
                }

                //setup our image grid
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                gridView.setColumnWidth(imageWidth);
                String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                Log.d(TAG, "onDataChange: new update try test. outside");

                final ArrayList<String> imgUrls = new ArrayList<String>();
                for(int i = 0; i < rev.size(); i++){
                    if(!rev.get(i).getImage_path().contains(",")) {
                        imgUrls.add(rev.get(i).getImage_path());
                    }else{
                        String[] exploded=rev.get(i).getImage_path().split(", ");
                        imgUrls.add(exploded[1]);
                    }
                    Log.d(TAG, "onDataChange: new update test url = "+ rev.get(i).getImage_path());
                }
                GridImageAdapter adapter = new GridImageAdapter(SearchActivity.this, R.layout.layout_grid_imageview,
                        "", imgUrls);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (rev.get(position).getType() == 2) {
                                String tmp = rev.get(position).getImage_path();
                                String[] imgurl = rev.get(position).getImage_path().split(", ");
                                Log.d(TAG, "onItemClick: image url = " + imgurl.toString() + " rev.get(position).getImage_path() = " + rev.get(position).getImage_path());
                                ((SearchActivity) mContext).multi_show(tmp,rev.get(position).getPhoto_id(), imgurl, String.valueOf(rev.get(position).getType()));
                            } else {
                                String tmp = rev.get(position).getImage_path();
                                String[] imgurl = tmp.split(", ");
                                Log.d(TAG, "onItemClick: tmp = " + tmp + " image url = " + imgurl.toString() + " rev.get(position).getImage_path() = " + rev.get(position).getImage_path());
                                ((SearchActivity) mContext).multi_show(tmp,rev.get(position).getPhoto_id(), imgurl, String.valueOf(rev.get(position).getType()));
                            }

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    FragmentManager mFragmentManager = getSupportFragmentManager();


    public void multi_show(String url, String image_id,String[] img_url,String type)
    {
        Bundle bundle = new Bundle();
        bundle.putString("path", url);
        bundle.putStringArray("url",img_url);
        bundle.putString("id",image_id);
        bundle.putString("type",type);

        ViewMultiplePostFragment view_post = new ViewMultiplePostFragment();
        view_post.setArguments(bundle);
        if(getIntent().hasExtra("notification")) {

            mFragmentManager.beginTransaction().add(R.id.container, view_post).commit();
        }else{
            mFragmentManager.beginTransaction().add(R.id.container, view_post).addToBackStack("ViewMultplePostFragment").commit();
        }

    }



    private void hideSoftKeyboard(){
        if(getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
