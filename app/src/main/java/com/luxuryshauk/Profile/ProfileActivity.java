package com.luxuryshauk.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.luxuryshauk.R;
import com.luxuryshauk.Utils.ViewCommentsFragment;
import com.luxuryshauk.Utils.ViewMultiplePostFragment;
import com.luxuryshauk.Utils.ViewMultiplePostFragmentFromProfile;
import com.luxuryshauk.Utils.ViewPostFragment;
import com.luxuryshauk.Utils.ViewProfileFragment;
import com.luxuryshauk.models.Photo;
import com.luxuryshauk.models.User;

/**
 * Created by User on 5/28/2017.
 */

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener ,
        ViewPostFragment.OnCommentThreadSelectedListener,
        ViewProfileFragment.OnGridImageSelectedListener{

    private static final String TAG = "ProfileActivity";
    FragmentManager mFragmentManager = getSupportFragmentManager();

    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onCommentThreadSelectedListener(Photo photo) {
        Log.d(TAG, "onCommentThreadSelectedListener:  selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }

    public void multi_show(String url, String image_id,String[] img_url,String type, String userid)
    {
        Bundle bundle = new Bundle();
        bundle.putString("path", url);
        bundle.putStringArray("url",img_url);
        bundle.putString("id",image_id);
        bundle.putString("type",type);
        bundle.putString("userid", userid);

        ViewMultiplePostFragmentFromProfile view_post = new ViewMultiplePostFragmentFromProfile();
//        ViewMultiplePostFragment view_post = new ViewMultiplePostFragment();
        view_post.setArguments(bundle);
        if(getIntent().hasExtra("notification")) {

            mFragmentManager.beginTransaction().add(R.id.container, view_post).commit();
        }else{
            mFragmentManager.beginTransaction().add(R.id.container, view_post).addToBackStack("ViewMultplePostFragment").commit();
        }

    }
    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStackImmediate();

        }
        else super.onBackPressed();
    }


    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + photo.toString());

        ViewMultiplePostFragment fragment = new ViewMultiplePostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);

        fragment.setArguments(args);

        FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();

    }


    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;

    private Context mContext = ProfileActivity.this;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");
        Bundle b = getIntent().getExtras();
        if(getIntent().hasExtra("notification")) {
            if (b.getInt("notification") == 1) {
                Log.d(TAG, "init: inflating Profile");
                ProfileFragment fragment = new ProfileFragment();
                FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                //transaction.addToBackStack(getString(R.string.profile_fragment));
                transaction.commit();
                //Toast.makeText(mContext, "Came from notification", Toast.LENGTH_SHORT).show();
                String[] images = b.getStringArray("images");
                String image = b.getString("image");
                String id = b.getString("id");
                int type = b.getInt("type");
                String userid = b.getString("");
                for(String s : images)
                    Log.d(TAG, "onCreate: notification image = " + s);

                multi_show(image,id,images,String.valueOf(type), userid);
            }
        }else {
            init();
        }




    }

    private void init(){
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        Intent intent = getIntent();
//        finish();
//        startActivity(intent);

        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: searching for user object attached as intent extra");
            if(intent.hasExtra(getString(R.string.intent_user))){
                User user = intent.getParcelableExtra(getString(R.string.intent_user));
                if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Log.d(TAG, "init: inflating view profile");
                    ViewProfileFragment fragment = new ViewProfileFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user),
                            intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    //transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    transaction.commit();
                }else{
                    Log.d(TAG, "init: inflating Profile");
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    //transaction.addToBackStack(getString(R.string.profile_fragment));
                    transaction.commit();
                }
            }else{
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }

        }else{
            Log.d(TAG, "init: inflating Profile");
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            //transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }

    }



}