package com.luxuryshauk.Utils;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.luxuryshauk.R;
import com.luxuryshauk.models.Comment;
import com.luxuryshauk.models.Photo;

/**
 * Created by User on 8/12/2017.
 */

public class ViewCommentsFragment extends Fragment {

    private static final String TAG = "ViewCommentsFragment";

    public ViewCommentsFragment(){
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String currentUsername = "";

    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mComment;
    private ListView mListView;

    //vars
    private Photo mPhoto;
    private ArrayList<Comment> mComments;
    private ArrayList<Comment> mComments_sorted;
    private Context mContext;
    String commentID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mCheckMark = (ImageView) view.findViewById(R.id.ivPostComment);
        mComment = (EditText) view.findViewById(R.id.comment);
        mListView = (ListView) view.findViewById(R.id.listView);
        mComments = new ArrayList<>();
        mComments_sorted = new ArrayList<>();
        mContext = getActivity();
        try{
            mPhoto = getPhotoFromBundle();
            setupFirebaseAuth();

        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }

        return view;
    }

    private void setupWidgets(){
        Collections.reverse(mComments);
        CommentListAdapter adapter = new CommentListAdapter(mContext,
                R.layout.layout_comment, mComments);
        adapter.notifyDataSetChanged();
        mListView.setAdapter(adapter);
        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mComment.getText().toString().equals("")){

                    myRef = FirebaseDatabase.getInstance().getReference();
                    String notKey = myRef.push().getKey();
                    String currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if(!mPhoto.getUser_id().equals(currentuid)) {
                        String mUserID = mPhoto.getUser_id();
                        SetNotification sn = new SetNotification();
                        sn.notify(currentuid,notKey,"unseen",getTimestamp(),"2",mUserID , mPhoto.getPhoto_id());
                    }

                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment(mComment.getText().toString());
                    myRef.child(mContext.getString(R.string.dbname_photos))
                            .child(mPhoto.getPhoto_id())
                            .child(mContext.getString(R.string.field_comments))
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.d(TAG, "onChildAdded: child added.");

                                    Query query = myRef
                                            .child(mContext.getString(R.string.dbname_photos))
                                            .orderByChild(mContext.getString(R.string.field_photo_id))
                                            .equalTo(mPhoto.getPhoto_id());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                                                Photo photo = new Photo();
                                                Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                                photo.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());
                                                photo.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());
                                                photo.setPhoto_id(objectMap.get(mContext.getString(R.string.field_photo_id)).toString());
                                                photo.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
                                                photo.setDate_created(objectMap.get(mContext.getString(R.string.field_date_created)).toString());
                                                photo.setImage_path(objectMap.get(mContext.getString(R.string.field_image_path)).toString());


                                                mComments.clear();
                                                //Comment firstComment = new Comment();
                                                //firstComment.setComment(mPhoto.getCaption());
                                                //firstComment.setUser_id(mPhoto.getUser_id());
                                                //firstComment.setDate_created(mPhoto.getDate_created());
                                                //firstComment.setComment_id(commentID);
                                                //mComments.add(firstComment);

                                                for (DataSnapshot dSnapshot : singleSnapshot
                                                        .child(mContext.getString(R.string.field_comments)).getChildren()){
                                                    Comment comment = new Comment();
                                                    comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                                    comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                                    comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                                    comment.setPhoto_id(dSnapshot.getValue(Comment.class).getPhoto_id());
                                                    comment.setComment_id(dSnapshot.getValue(Comment.class).getComment_id());
                                                    Log.d(TAG, "onDataChange: got from firebase comment : " + comment.toString());
                                                    mComments.add(comment);
                                                    mListView.setAdapter(null);
                                                    CommentListAdapter adapter = new CommentListAdapter(mContext,
                                                            R.layout.layout_comment, mComments);
                                                    adapter.notifyDataSetChanged();
                                                    mListView.setAdapter(adapter);

                                                }


                                                photo.setComments(mComments);

                                                mPhoto = photo;

                                                setupWidgets();
//                    List<Like> likesList = new ArrayList<Like>();
//                    for (DataSnapshot dSnapshot : singleSnapshot
//                            .child(getString(R.string.field_likes)).getChildren()){
//                        Like like = new Like();
//                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
//                        likesList.add(like);
//                    }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.d(TAG, "onCancelled: query cancelled.");
                                        }
                                    });
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
//                    FirebaseDatabase.getInstance().getReference()
//                            .child("photos")
//                            .child(mPhoto.getPhoto_id()).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                            //Photo p = dataSnapshot.getValue(Photo.class);
////                            Photo newPhoto = new Photo();
////                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
////
////                            newPhoto.setCaption(objectMap.get("caption").toString());
////                            newPhoto.setTags(objectMap.get("tags").toString());
////                            newPhoto.setPhoto_id(objectMap.get("photo_id").toString());
////                            newPhoto.setUser_id(objectMap.get("user_id").toString());
////                            newPhoto.setDate_created(objectMap.get("date_created").toString());
////                            newPhoto.setImage_path(objectMap.get("image_path").toString());
////                            mComments.add(comment);
////                            CommentListAdapter adapter = new CommentListAdapter(mContext,
////                                    R.layout.layout_comment, mComments);
////                            adapter.notifyDataSetChanged();
////                            mListView.setAdapter(adapter);
//////                            try {
////                                ((HomeActivity) mContext).onCommentThreadSelected(newPhoto,
////                                        mContext.getString(R.string.home_activity));
//////                            }catch (Exception e){
//////                                ((ProfileActivity) mContext).onCommentThreadSelectedListener(newPhoto);
//////                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
                    mComment.setText("");
                    closeKeyboard();
                }else{
                    Toast.makeText(getActivity(), "you can't post a blank comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        mBackArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating back");
//                if(getCallingActivityFromBundle().equals(getString(R.string.home_activity))){
//                    getActivity().getSupportFragmentManager().popBackStack();
//                    ((HomeActivity)getActivity()).showLayout();
//                }else{
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }
//
//            }
//        });
    }

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void addNewComment(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);

        commentID = myRef.push().getKey();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        final Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        comment.setComment_id(commentID);
        comment.setPhoto_id(mPhoto.getPhoto_id());


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

//
//        final String mGroupId = reference.child("notification").child(comment.getUser_id()).push().getKey();
//
//
//        reference.child("notification").child(comment.getUser_id()).child(mGroupId).child("type").setValue("comments");
//        reference.child("notification").child(comment.getUser_id()).child(mGroupId).child("seen").setValue("unseen");
//        reference.child("notification").child(comment.getUser_id()).child(mGroupId).child("time").setValue(currentDateandTime);
//        reference.child("notification").child(comment.getUser_id()).child(mGroupId).child("com_id").setValue(mGroupId);
//
//        Query query = reference
//                .child(mContext.getString(R.string.dbname_users))
//                .orderByChild(mContext.getString(R.string.field_user_id))
//                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
//                    reference.child("notification").child(comment.getUser_id()).child(mGroupId).child("by_user").setValue(currentUsername);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



        //insert into photos node
        myRef.child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into user_photos node
        myRef.child(getString(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id()) //should be mphoto.getUser_id()
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);
        CommentListAdapter adapter = new CommentListAdapter(mContext,
                R.layout.layout_comment, mComments);
        adapter.notifyDataSetChanged();
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private String getCallingActivityFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getString(getString(R.string.home_activity));
        }else{
            return bundle.getString(getString(R.string.home_activity));
        }
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private Photo getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return null;
        }
    }

           /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        if(mPhoto.getComments().size() == 0){
            mComments.clear();
            Comment firstComment = new Comment();
            //firstComment.setComment(mPhoto.getCaption());
            //firstComment.setUser_id(mPhoto.getUser_id());
            //firstComment.setDate_created(mPhoto.getDate_created());
            //mComments.add(firstComment);
            //mPhoto.setComments(mComments);
            setupWidgets();
        }


        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAdded: child added.");

                        Query query = myRef
                                .child(mContext.getString(R.string.dbname_photos))
                                .orderByChild(mContext.getString(R.string.field_photo_id))
                                .equalTo(mPhoto.getPhoto_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                                    Photo photo = new Photo();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    photo.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());
                                    photo.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());
                                    photo.setPhoto_id(objectMap.get(mContext.getString(R.string.field_photo_id)).toString());
                                    photo.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
                                    photo.setDate_created(objectMap.get(mContext.getString(R.string.field_date_created)).toString());
                                    photo.setImage_path(objectMap.get(mContext.getString(R.string.field_image_path)).toString());


                                    mComments.clear();
                                    //Comment firstComment = new Comment();
                                    //firstComment.setComment(mPhoto.getCaption());
                                    //firstComment.setUser_id(mPhoto.getUser_id());
                                    //firstComment.setDate_created(mPhoto.getDate_created());
                                    //firstComment.setComment_id(commentID);
                                    //mComments.add(firstComment);

                                    for (DataSnapshot dSnapshot : singleSnapshot
                                           .child(mContext.getString(R.string.field_comments)).getChildren()){
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                        comment.setPhoto_id(dSnapshot.getValue(Comment.class).getPhoto_id());
                                        comment.setComment_id(dSnapshot.getValue(Comment.class).getComment_id());
                                        Log.d(TAG, "onDataChange: got from firebase comment : " + comment.toString());
                                        mComments.add(comment);
                                        mListView.setAdapter(null);
                                        CommentListAdapter adapter = new CommentListAdapter(mContext,
                                                R.layout.layout_comment, mComments);
                                        adapter.notifyDataSetChanged();
                                        mListView.setAdapter(adapter);

                                    }


                                    photo.setComments(mComments);

                                    mPhoto = photo;

                                   setupWidgets();
//                    List<Like> likesList = new ArrayList<Like>();
//                    for (DataSnapshot dSnapshot : singleSnapshot
//                            .child(getString(R.string.field_likes)).getChildren()){
//                        Like like = new Like();
//                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
//                        likesList.add(like);
//                    }

                                       }

                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {
                                       Log.d(TAG, "onCancelled: query cancelled.");
                                   }
                               });
                           }

                           @Override
                           public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                           }

                           @Override
                           public void onChildRemoved(DataSnapshot dataSnapshot) {

                           }

                           @Override
                           public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });



    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}





















