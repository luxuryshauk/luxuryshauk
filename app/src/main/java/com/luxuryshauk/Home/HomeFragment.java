package com.luxuryshauk.Home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.eschao.android.widget.elasticlistview.LoadFooter;
import com.eschao.android.widget.elasticlistview.OnLoadListener;
import com.eschao.android.widget.elasticlistview.OnUpdateListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.luxuryshauk.R;
import com.luxuryshauk.Utils.MainFeedListAdapter;
import com.luxuryshauk.Utils.ProductAdapter;
import com.luxuryshauk.Utils.StoriesRecyclerViewAdapter;
import com.luxuryshauk.models.Comment;
import com.luxuryshauk.models.Photo;
import com.luxuryshauk.models.UserAccountSettings;

/**
 * Created by User on 5/28/2017.
 */

public class HomeFragment extends Fragment implements OnUpdateListener, OnLoadListener {

    private static final String TAG = "HomeFragment";

    @Override
    public void onUpdate() {
        Log.d(TAG, "ElasticListView: updating list view...");

        getFollowing();
    }


    @Override
    public void onLoad() {
//        Log.d(TAG, "ElasticListView: loading...");
//
//        // Notify load is done
//        mListView.notifyLoaded();
    }


    //vars
    private ArrayList<Photo> mPhotos;
    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private int recursionIterator = 0;
    LinkedHashSet<Photo> hashSet;
//        private ListView mListView;
    private ElasticListView mListView;
//    private MainFeedListAdapter adapter;
    ProductAdapter adapter;
    MainFeedListAdapter adapter1;
    private boolean gotstories = false;
    private int resultsCount = 0;
    private ArrayList<UserAccountSettings> mUserAccountSettings;
    //    private ArrayList<UserStories> mAllUserStories = new ArrayList<>();
    private JSONArray mMasterStoriesArray;
    public int topseller_c = 1;
    public int count_stories = 0;

    boolean isLoading = false;

    private RecyclerView mRecyclerView;
    public StoriesRecyclerViewAdapter mStoriesAdapter;
    private Button buy_now;
    ArrayList<String> uids = new ArrayList<>();
    public long total_stories;
    int refreshcount = 0;
    RecyclerView recyclerView;
    PullRefreshLayout layout;
    TextView firstusetext;
    ProgressBar pbar;

    int ydy = 0;
    private static int firstVisibleInListview;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

//        mListView = (ListView) view.findViewById(R.id.listView);
        firstusetext = view.findViewById(R.id.firstusetext);
        pbar = view.findViewById(R.id.progressbarnew);
        mListView = (ElasticListView) view.findViewById(R.id.listView);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        layout.setColor(Color.parseColor("#D3D3D3"));

        recyclerView.setHasFixedSize(true);




        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mListView.enableLoadHeader(true);
//            mListView.enableUpdateHeader(false);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up

//                    if(mListView.getVisibility()!=View.GONE) {
//                        mListView.setVisibility(View.GONE);
//                    }

                } else {
                    // Scrolling down
//                    if(mListView.getVisibility()!=View.VISIBLE) {
                        mListView.setVisibility(View.VISIBLE);
//                    }

                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Do something
                    mListView.setVisibility(View.GONE);

                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                    mListView.setVisibility(View.VISIBLE);
                    // Do something
                } else {
//                    mListView.setVisibility(View.VISIBLE);

                    // Do something
                }
            }
        });
        initListViewRefresh();



//        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                recyclerView.setAdapter(adapter);
//                try {
//                    layout.setRefreshing(true);
//                    Thread.sleep(100);
//                    layout.setRefreshing(false);
//
//                }catch (Exception e){}
//            }
//        });

        getFollowing();

//        try {
//            Thread t = new Thread() {
//                @Override
//                public void run() {
//                    while (!isInterrupted()) {
//                        try {
//                            //Thread.sleep(1000);
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(refreshcount<6) {
                                        getFriendsAccountSettings();
//                                        refreshcount++;
//                                    }
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//
//            t.start();
//        }catch (Exception e){}
//        initScrollListener();


        return view;
    }

    private void initListViewRefresh(){
        mListView.setHorizontalFadingEdgeEnabled(true);
        mListView.setAdapter(adapter1);
        mListView.enableLoadFooter(true)
                .getLoadFooter().setLoadAction(LoadFooter.LoadAction.RELEASE_TO_LOAD);
        mListView.setOnUpdateListener(this)
                .setOnLoadListener(this);
        mListView.requestUpdate();
        mListView.enableUpdateHeader(false);
    }


    private void getFriendsAccountSettings(){
        Log.d(TAG, "getFriendsAccountSettings: getting friends account settings.");

        for(int i = 0; i < mFollowing.size(); i++) {
            Log.d(TAG, "getFriendsAccountSettings: user: " + mFollowing.get(i));
            final int count = i;
            try {
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_user_account_settings))
                        .orderByKey()
                        .equalTo(mFollowing.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "getFriendsAccountSettings: got a user: " + snapshot.getValue(UserAccountSettings.class).getDisplay_name());
                            mUserAccountSettings.add(snapshot.getValue(UserAccountSettings.class));

//                        if(count == 0){
//                            JSONObject userObject = new JSONObject();
//                            try {
//                                userObject.put(getString(R.string.field_display_name), mUserAccountSettings.get(count).getDisplay_name());
//                                userObject.put(getString(R.string.field_username), mUserAccountSettings.get(count).getUsername());
//                                userObject.put(getString(R.string.field_profile_photo), mUserAccountSettings.get(count).getProfile_photo());
//                                userObject.put(getString(R.string.field_user_id), mUserAccountSettings.get(count).getUser_id());
//                                JSONObject userSettingsStoryObject = new JSONObject();
//                                userSettingsStoryObject.put(getString(R.string.user_account_settings), userObject);
//                                mMasterStoriesArray.put(0, userSettingsStoryObject);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }

                        }
                        if (count == mFollowing.size() - 1) {
                            getFriendsStories();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }catch (Exception e){}
        }
    }

    private void getFriendsStories(){
        Log.d(TAG, "getFriendsStories: getting stories of following.");


            Query query = FirebaseDatabase.getInstance().getReference()
                    .child("top_sellers");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    total_stories = dataSnapshot.getChildrenCount();
                    Log.d(TAG, "onDataChange: user found user = "+ dataSnapshot.getValue());
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: Top sellers are : " + snapshot.getKey());
                        uids.add(snapshot.getKey());
                    }

                    if(!dataSnapshot.exists()){
                        Log.d(TAG, "getFriendsStories: no stories could be found.");
//                        Log.d(TAG, "getFriendsStories: " + mMasterStoriesArray.toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            for(String uid:uids)
            {
                    topseller_c = 0;
                    Log.d(TAG, "getFriendsStories: checking for uid = " + uid);
                    Query new_q = FirebaseDatabase.getInstance().getReference()
                            .child("user_account_settings").child(uid);
                    new_q.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserAccountSettings user = dataSnapshot.getValue(UserAccountSettings.class);
                            try {
                                Log.d(TAG, "onDataChange: got topseller : " + user.toString());
                                JSONObject userObject = new JSONObject();
                                try {
                                    if(topseller_c<total_stories) {
                                        userObject.put("display_name", user.getDisplay_name());
                                        userObject.put("username", user.getUsername());
                                        userObject.put("profile_photo", user.getProfile_photo());
                                        userObject.put("user_id", user.getUser_id());
                                        Log.d(TAG, "onDataChange: user object  = " + userObject.toString());
                                        Log.d(TAG, "onDataChange: topseller counts = " + topseller_c+" child count = " + dataSnapshot.getChildrenCount());
                                        //mMasterStoriesArray.put(topseller_c, userObject);
                                        JSONObject userSettingsStoryObject = new JSONObject();
                                        userSettingsStoryObject.put(getString(R.string.user_account_settings), userObject);
                                        mMasterStoriesArray.put(topseller_c, userSettingsStoryObject);

                                        topseller_c++;
                                    }
                                } catch (Exception e) {
                                }
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            }
        initRecyclerView();



//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    JSONArray storiesArray = new JSONArray();
//                    JSONObject userObject = new JSONObject();
//
//                    try{
//
//                        Log.d(TAG, "getFriendsStories: count: " + count);
//                        Log.d(TAG, "getFriendsStories: user: " + mUserAccountSettings.get(count).getDisplay_name());
//                        if(count != 0){
//                            userObject.put(getString(R.string.field_display_name), mUserAccountSettings.get(count).getDisplay_name());
//                            userObject.put(getString(R.string.field_username), mUserAccountSettings.get(count).getUsername());
//                            userObject.put(getString(R.string.field_profile_photo), mUserAccountSettings.get(count).getProfile_photo());
//                            userObject.put(getString(R.string.field_user_id), mUserAccountSettings.get(count).getUser_id());
//                        }
//
//                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                            JSONObject story = new JSONObject();
//                            story.put(getString(R.string.field_user_id), snapshot.getValue(Story.class).getUser_id());
//                            story.put(getString(R.string.field_timestamp), snapshot.getValue(Story.class).getTimestamp());
//                            story.put(getString(R.string.field_image_uri), snapshot.getValue(Story.class).getImage_url());
//                            story.put(getString(R.string.field_video_uri), snapshot.getValue(Story.class).getVideo_url());
//                            story.put(getString(R.string.field_story_id), snapshot.getValue(Story.class).getStory_id());
//                            story.put(getString(R.string.field_views), snapshot.getValue(Story.class).getViews());
//                            story.put(getString(R.string.field_duration), snapshot.getValue(Story.class).getDuration());
//
//
//                            Log.d(TAG, "getFriendsStories: got a story: " + story.get(getString(R.string.field_user_id)));
////                            Log.d(TAG, "getFriendsStories: story: " + story.toString());
//                            storiesArray.put(story);
//                        }
//
//                        JSONObject userSettingsStoryObject = new JSONObject();
//                        if(count != 0){
//                            userSettingsStoryObject.put(getString(R.string.user_account_settings), userObject);
//                            if(storiesArray.length() > 0){
//                                userSettingsStoryObject.put(getString(R.string.user_stories), storiesArray);
//                                int position = mMasterStoriesArray.length();
//                                mMasterStoriesArray.put(position, userSettingsStoryObject);
//                                Log.d(TAG, "onDataChange: adding list of stories to position #" + position);
//                            }
//                        }
//                        else {
//                            userObject = mMasterStoriesArray.getJSONObject(0).getJSONObject(getString(R.string.user_account_settings));
//                            userSettingsStoryObject.put(getString(R.string.user_account_settings), userObject);
//                            userSettingsStoryObject.put(getString(R.string.user_stories), storiesArray);
////                            int position = mMasterStoriesArray.length() - 1;
//                            int position = 0;
//                            mMasterStoriesArray.put(position, userSettingsStoryObject);
//                            Log.d(TAG, "onDataChange: adding list of stories to position #" + position);
//                        }
//
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                    if(!dataSnapshot.exists()){
//                        Log.d(TAG, "getFriendsStories: no stories could be found.");
////                        Log.d(TAG, "getFriendsStories: " + mMasterStoriesArray.toString());
//
//                    }
//                    if(count == mFollowing.size() - 1){
//                        initRecyclerView();
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });


    }



    private void initRecyclerView(){
        try {

            Log.d(TAG, "initRecyclerView: init recyclerview.");
            if (mRecyclerView == null) {
                TextView textView = new TextView(getActivity());
                textView.setText("Top Sellers");
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(14);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                textView.setLayoutParams(params);
                mListView.addHeaderView(textView);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                mRecyclerView = new RecyclerView(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                mListView.addHeaderView(mRecyclerView);
            }
            mStoriesAdapter = new StoriesRecyclerViewAdapter(mMasterStoriesArray, getActivity());
            mRecyclerView.setAdapter(mStoriesAdapter);
        }catch (Exception e){}

    }

    private void clearAll(){
        if(mFollowing != null){
            mFollowing.clear();
        }

//        if(mPhotos != null){
//            mPhotos.clear();
//            if(adapter != null){
////                adapter.clear();
//                adapter.notifyDataSetChanged();
//            }
//        }
        if(mUserAccountSettings != null){
            mUserAccountSettings.clear();
        }
        if(mPaginatedPhotos != null){
            mPaginatedPhotos.clear();
        }
        mMasterStoriesArray = new JSONArray(new ArrayList<String>());
        if(mStoriesAdapter != null){
            mStoriesAdapter.notifyDataSetChanged();
        }
        if(mRecyclerView != null){
            mRecyclerView.setAdapter(null);
        }
        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();
        mPaginatedPhotos = new ArrayList<>();
        mUserAccountSettings = new ArrayList<>();
    }

    /**
     //     * Retrieve all user id's that current user is following
     //     */
    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

        clearAll();
        //also add your own id to the list
        mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getActivity().getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                ;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "getFollowing: found user: " + singleSnapshot
                                .child(getString(R.string.field_user_id)).getValue());

                        mFollowing.add(singleSnapshot
                                .child(getString(R.string.field_user_id)).getValue().toString());
                    }
                }catch (Exception e){}

                getPhotos();
//                getMyUserAccountSettings();
                getFriendsAccountSettings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting list of photos");
        mPhotos = new ArrayList<>();

        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getActivity().getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i))
                    ;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                        Photo newPhoto = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        newPhoto.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        newPhoto.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        newPhoto.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        Log.d(TAG, "getPhotos: photo: " + newPhoto.getPhoto_id());
                        List<Comment> commentsList = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Map<String, Object> object_map = (HashMap<String, Object>) dSnapshot.getValue();
                            Comment comment = new Comment();
                            comment.setUser_id(object_map.get(getString(R.string.field_user_id)).toString());
                            comment.setComment(object_map.get(getString(R.string.field_comment)).toString());
                            comment.setDate_created(object_map.get(getString(R.string.field_date_created)).toString());
                            commentsList.add(comment);
                        }
                        newPhoto.setComments(commentsList);
                        mPhotos.add(newPhoto);
                    }
                    if(count >= mFollowing.size() - 1){
                        //display the photos
                        displayPhotos();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });

        }
    }

    private void displayPhotos(){
//        mPaginatedPhotos = new ArrayList<>();
        if(mPhotos != null){

            try{

                //sort for newest to oldest
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                //we want to load 10 at a time. So if there is more than 10, just load 10 to start
                int iterations = mPhotos.size();
                if(iterations>0)
                {
                    firstusetext.setVisibility(View.INVISIBLE);
                    pbar.setVisibility(View.INVISIBLE);
                }else{
                    pbar.setVisibility(View.INVISIBLE);
                }

                if(iterations > 250){
                    iterations = 250;
//                    iterations = 0;
                }

//
                resultsCount = 0;
                for(int i = 0; i < iterations; i++){
//                    for(int k=0;k<mPaginatedPhotos.size();k++)
//                    {
//                        for(int l=0;l<k;l++)
//                        {
//                            if(mPhotos.get(k).getPhoto_id().equals())
//
//                        }
//                    }
                    if(!mPaginatedPhotos.contains(mPhotos.get(i))) {
                        mPaginatedPhotos.add(mPhotos.get(i));
                    }
                    resultsCount++;
                    Log.d(TAG, "displayPhotos: adding a photo to paginated list: " + mPhotos.get(i).getPhoto_id());
                }

                hashSet = new LinkedHashSet<>(mPaginatedPhotos);
                ArrayList<String> photoids = new ArrayList<>();
                ArrayList<Photo> finalimages = new ArrayList<>();

                mPaginatedPhotos = new ArrayList<>(hashSet);
                for(Photo p :mPaginatedPhotos)
                {
                    if(!photoids.contains(p.getPhoto_id())) {
                        photoids.add(p.getPhoto_id());
                        finalimages.add(p);
                    }
                    Log.d(TAG, "displayPhotos: added photos : " + p.getPhoto_id());
                }

//                adapter = new ProductAdapter(getActivity(),  mPaginatedPhotos);
                adapter = new ProductAdapter(getActivity(),  finalimages);

//                adapter = new MainFeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPhotos);
                try {
//                    mListView.setAdapter(adapter);
                    recyclerView.setAdapter(adapter);
                }catch (Exception e)
                {

                }

                // Notify update is done
//                mListView.notifyUpdated();
                layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getActivity().finish();
                        Intent intent1 = new Intent(getContext(), HomeActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        getActivity().startActivity(intent1);
//                        getActivity().overridePendingTransition(0, 0);
//                        mPhotos.clear();
//                        getPhotos();
//                        //displayPhotos();
//
//                        adapter = new ProductAdapter(getActivity(),  mPaginatedPhotos);
//                        recyclerView.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//                        layout.setRefreshing(false);

                    }
                });


            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
            }
        }
    }
//    private void initScrollListener() {
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//
//                if (!isLoading) {
//                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mPhotos.size() - 1) {
//                        //bottom of list!
//                        loadMore();
//                        isLoading = true;
//                    }
//                }
//            }
//        });
//
//
//    }
//    private void loadMore() {
//        mPaginatedPhotos.add(null);
//        adapter.notifyItemInserted(mPaginatedPhotos.size() - 1);
//
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPaginatedPhotos.remove(mPaginatedPhotos.size() - 1);
//                int scrollPosition = mPaginatedPhotos.size();
//                adapter.notifyItemRemoved(scrollPosition);
//                int currentSize = scrollPosition;
//                int nextLimit = currentSize + 10;
//
//                while (currentSize - 1 < nextLimit) {
//                    mPaginatedPhotos.add(mPhotos.get(currentSize));
//                    currentSize++;
//                }
//
//                adapter.notifyDataSetChanged();
//                isLoading = false;
//            }
//        }, 2000);
//    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(mPhotos.size() > resultsCount && mPhotos.size() > 0){

                int iterations;
                if(mPhotos.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                resultsCount = resultsCount + iterations;
                adapter.notifyDataSetChanged();
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }

    }

}
