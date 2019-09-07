package com.luxuryshauk.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.luxuryshauk.Home.HomeActivity;
import com.luxuryshauk.Home.HomeFragment;
import com.luxuryshauk.Profile.AccountSettingsActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.Share.NextActivity;
import com.luxuryshauk.materialcamera.MaterialCamera;
import com.luxuryshauk.models.Photo;
import com.luxuryshauk.models.Story;
import com.luxuryshauk.models.User;
import com.luxuryshauk.models.UserAccountSettings;
import com.luxuryshauk.models.UserSettings;

/**
 * Created by User on 6/26/2017.
 */



public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    public String[] url;
    int url_count;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;
    private NextActivity na;
    int count_main;
    private boolean success;
    public int[] urls = new int[10];
    int j,temp;
    Task<Uri> uriTask;
    boolean isprocessing = false;
    boolean[] isimg = new boolean[5];

    private int i;
    public int countt;

    boolean[] done = new boolean[9];


    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context) {
        url_count = 1;
        success = false;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
        myRef.child("image_count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countt = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void uploadNewPhoto(String photoType, final String caption,final int count, final String imgUrl,
                               Bitmap bm,final float price,final int type){

        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");
        myRef.child("image_count").setValue(countt+1);

        final FilePaths filePaths = new FilePaths();
        //case1) new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (count + 1));

            //convert image url to bitmap
            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 70);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (count + 1)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            addPhotoToDatabase(caption,task.getResult().toString(),price,type);
                        }
                    });
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                    Activity c = (Activity)mContext;
                    c.finish();
                    //add the new photo to 'photos' node and 'user_photos' node
                    //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                    NextActivity n = new NextActivity();
                    n.finishA();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }
        //case new profile photo
        else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");


            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 70);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //insert into 'user_account_settings' node
                    //setProfilePhoto(firebaseUrl.toString());

                    ((AccountSettingsActivity)mContext).setViewPager(
                            ((AccountSettingsActivity)mContext).pagerAdapter
                                    .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }

    }



    public void uploadNewPhotos(final String photoType, final String caption, final int count, final String[] imgUrl,
                                Bitmap bm, final float price, final int type){
        int image_count = imgUrl.length;
        //Issue photo upload horahi h lekin sahi se ni horahi
        final String key = myRef.push().getKey();
        for(String img:imgUrl) {
            Log.d(TAG, "uploadNewPhotos: retrieved image URLs : " + img);
        }
        myRef.child("image_count").setValue(countt+image_count);

        done = new boolean[image_count];
        Arrays.fill(done, false);
        success = false;
        if(image_count>0)
        if (!imgUrl[0].isEmpty()) {
            final FilePaths filePaths = new FilePaths();
            //case1) new photo
            if (photoType.equals(mContext.getString(R.string.new_photo))) {
                Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

                final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference storageReference = mStorageReference
                        .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 1));
                Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[0]);

                //convert image url to bitmap
                //if (bm == null) {
                    bm = ImageManager.getBitmap(imgUrl[0]);
                //}

                byte[] bytes1 = ImageManager.getBytesFromBitmap(bm, 70);

                UploadTask uploadTask1 = null;
                uploadTask1 = storageReference.putBytes(bytes1);

                uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                        mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 1)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,1,0);
                            }
                        });
                        Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                        //add the new photo to 'photos' node and 'user_photos' node
                        //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                        //navigate to the main feed so the user can see their photo
                        //Intent intent = new Intent(mContext, HomeActivity.class);
                        //mContext.startActivity(intent);
//                        NextActivity n = new NextActivity();
//                        n.finishA();
                        done[0]=true;
                        if(checkifucompleted())
                        {
                            ((Activity) mContext).finish();
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            mContext.startActivity(intent);
                            NextActivity n = new NextActivity();
                            n.finishA();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed.");
                        Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        if (progress - 15 > mPhotoUploadProgress) {
                            Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;
                        }

                        Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                    }
                });
            }
        }
        if(image_count>1)
        if (!imgUrl[1].isEmpty()) {
            final FilePaths filePaths = new FilePaths();
            //case1) new photo
            if (photoType.equals(mContext.getString(R.string.new_photo))) {
                Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

                final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference storageReference = mStorageReference
                        .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 2));
                Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[1]);

                //convert image url to bitmap
                //if (bm == null) {
                    bm = ImageManager.getBitmap(imgUrl[1]);
                //}

                byte[] bytes2 = ImageManager.getBytesFromBitmap(bm, 70);

                UploadTask uploadTask2 = null;
                uploadTask2 = storageReference.putBytes(bytes2);

                uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                        mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 2)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,2,1);
                            }
                        });
                        Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                        //add the new photo to 'photos' node and 'user_photos' node
                        //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                        //navigate to the main feed so the user can see their photo
//                        Intent intent = new Intent(mContext, HomeActivity.class);
//                        mContext.startActivity(intent);
//                        NextActivity n = new NextActivity();
//                        n.finishA();
                        done[1]=true;
                        if(checkifucompleted())
                        {
                            ((Activity) mContext).finish();
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            mContext.startActivity(intent);
                            NextActivity n = new NextActivity();
                            n.finishA();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed.");
                        Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        if (progress - 15 > mPhotoUploadProgress) {
                            Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;
                        }

                        Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                    }
                });
            }
        }
        if(image_count>2)
        if (!imgUrl[2].isEmpty()) {
            final FilePaths filePaths = new FilePaths();
            //case1) new photo
            if (photoType.equals(mContext.getString(R.string.new_photo))) {
                Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

                final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference storageReference = mStorageReference
                        .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 3));
                Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[2]);

                //convert image url to bitmap
                //if (bm == null) {
                    bm = ImageManager.getBitmap(imgUrl[2]);
                //}

                byte[] bytes3 = ImageManager.getBytesFromBitmap(bm, 70);

                UploadTask uploadTask3 = null;
                uploadTask3 = storageReference.putBytes(bytes3);
                try {
                    Thread.sleep(200);
                }catch (Exception e){}

                uploadTask3.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                        mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 3)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,3,1);
                            }
                        });
                        Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                        //add the new photo to 'photos' node and 'user_photos' node
                        //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                        //navigate to the main feed so the user can see their photo
//                        Intent intent = new Intent(mContext, HomeActivity.class);
//                        mContext.startActivity(intent);
//                        NextActivity n = new NextActivity();
//                        n.finishA();
                        done[2]=true;
                        if(checkifucompleted())
                        {
                            ((Activity) mContext).finish();
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            mContext.startActivity(intent);
                            NextActivity n = new NextActivity();
                            n.finishA();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed.");
                        Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        if (progress - 15 > mPhotoUploadProgress) {
                            Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;
                        }

                        Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                    }
                });
            }
        }
        if(image_count>3)
        if (!imgUrl[3].isEmpty()) {
            final FilePaths filePaths = new FilePaths();
            //case1) new photo
            if (photoType.equals(mContext.getString(R.string.new_photo))) {
                Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");


                final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference storageReference = mStorageReference
                        .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 4));
                Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[3]);

                //convert image url to bitmap
                //if (bm == null) {
                    bm = ImageManager.getBitmap(imgUrl[3]);
                //}

                byte[] bytes4 = ImageManager.getBytesFromBitmap(bm, 70);

                UploadTask uploadTask4 = null;
                uploadTask4 = storageReference.putBytes(bytes4);

                uploadTask4.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                        mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 4)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,4,1);
                            }
                        });
                        Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                        //add the new photo to 'photos' node and 'user_photos' node
                        //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                        //navigate to the main feed so the user can see their photo
//                        Intent intent = new Intent(mContext, HomeActivity.class);
//                        mContext.startActivity(intent);
//                        NextActivity n = new NextActivity();
//                        n.finishA();
                        done[3]=true;
                        if(checkifucompleted())
                        {
                            ((Activity) mContext).finish();
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            mContext.startActivity(intent);
                            NextActivity n = new NextActivity();
                            n.finishA();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed.");
                        Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        if (progress - 15 > mPhotoUploadProgress) {
                            Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;
                        }

                        Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                    }
                });
            }
        }
        if(image_count>4)
        if (!imgUrl[4].isEmpty()) {
            final FilePaths filePaths = new FilePaths();
            //case1) new photo
            if (photoType.equals(mContext.getString(R.string.new_photo))) {
                Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

                final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference storageReference = mStorageReference
                        .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 5));

                Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[4]);

                //convert image url to bitmap
                //if (bm == null) {
                    bm = ImageManager.getBitmap(imgUrl[4]);
                //}

                byte[] bytes5 = ImageManager.getBytesFromBitmap(bm, 70);

                UploadTask uploadTask5 = null;
                uploadTask5 = storageReference.putBytes(bytes5);

                uploadTask5.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                        mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 5)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,5,1);
                            }
                        });
                        Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                        //add the new photo to 'photos' node and 'user_photos' node
                        //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                        //navigate to the main feed so the user can see their photo
//                        Intent intent = new Intent(mContext, HomeActivity.class);
//                        mContext.startActivity(intent);
//                        NextActivity n = new NextActivity();
//                        n.finishA();
                        done[4]=true;
                        if(checkifucompleted())
                        {
                            ((Activity) mContext).finish();
                            Intent intent = new Intent(mContext, HomeActivity.class);
                            mContext.startActivity(intent);
                            NextActivity n = new NextActivity();
                            n.finishA();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed.");
                        Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        if (progress - 15 > mPhotoUploadProgress) {
                            Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;
                        }

                        Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                    }
                });
            }
        }
        if(image_count>5)
            if (!imgUrl[5].isEmpty()) {
                final FilePaths filePaths = new FilePaths();
                //case1) new photo
                if (photoType.equals(mContext.getString(R.string.new_photo))) {
                    Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

                    final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    StorageReference storageReference = mStorageReference
                            .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 6));

                    Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[5]);

                    //convert image url to bitmap
                    //if (bm == null) {
                    bm = ImageManager.getBitmap(imgUrl[5]);
                    //}

                    byte[] bytes6 = ImageManager.getBytesFromBitmap(bm, 70);

                    UploadTask uploadTask6 = null;
                    uploadTask6 = storageReference.putBytes(bytes6);

                    uploadTask6.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                            mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 6)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,6,1);
                                }
                            });
                            Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                            //add the new photo to 'photos' node and 'user_photos' node
                            //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                            //navigate to the main feed so the user can see their photo
//                            Intent intent = new Intent(mContext, HomeActivity.class);
//                            mContext.startActivity(intent);
//                            NextActivity n = new NextActivity();
//                            n.finishA();
                            done[5]=true;
                            if(checkifucompleted())
                            {
                                ((Activity) mContext).finish();
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                mContext.startActivity(intent);
                                NextActivity n = new NextActivity();
                                n.finishA();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Photo upload failed.");
                            Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            if (progress - 15 > mPhotoUploadProgress) {
                                Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                                mPhotoUploadProgress = progress;
                            }

                            Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                        }
                    });
                }
            }
        if(image_count>6)
            if (!imgUrl[6].isEmpty()) {
                final FilePaths filePaths = new FilePaths();
                //case1) new photo
                if (photoType.equals(mContext.getString(R.string.new_photo))) {
                    Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

                    final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    StorageReference storageReference = mStorageReference
                            .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 7));

                    Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[6]);

                    //convert image url to bitmap
                    //if (bm == null) {
                    bm = ImageManager.getBitmap(imgUrl[6]);
                    //}

                    byte[] bytes6 = ImageManager.getBytesFromBitmap(bm, 70);

                    UploadTask uploadTask6 = null;
                    uploadTask6 = storageReference.putBytes(bytes6);

                    uploadTask6.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                            mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 7)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,7,1);
                                }
                            });
                            Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                            //add the new photo to 'photos' node and 'user_photos' node
                            //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                            //navigate to the main feed so the user can see their photo
//                            Intent intent = new Intent(mContext, HomeActivity.class);
//                            mContext.startActivity(intent);
//                            NextActivity n = new NextActivity();
//                            n.finishA();
                            done[6]=true;
                            if(checkifucompleted())
                            {
                                ((Activity) mContext).finish();
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                mContext.startActivity(intent);
                                NextActivity n = new NextActivity();
                                n.finishA();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Photo upload failed.");
                            Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            if (progress - 15 > mPhotoUploadProgress) {
                                Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                                mPhotoUploadProgress = progress;
                            }

                            Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                        }
                    });
                }
            }
        if(image_count>7)
            if (!imgUrl[7].isEmpty()) {
                final FilePaths filePaths = new FilePaths();
                //case1) new photo
                if (photoType.equals(mContext.getString(R.string.new_photo))) {
                    Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

                    final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    StorageReference storageReference = mStorageReference
                            .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 8));

                    Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[7]);

                    //convert image url to bitmap
                    //if (bm == null) {
                    bm = ImageManager.getBitmap(imgUrl[7]);
                    //}

                    byte[] bytes8 = ImageManager.getBytesFromBitmap(bm, 70);

                    UploadTask uploadTask8 = null;
                    uploadTask8 = storageReference.putBytes(bytes8);

                    uploadTask8.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                            mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 8)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,8,1);
                                }
                            });
                            Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                            //add the new photo to 'photos' node and 'user_photos' node
                            //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                            //navigate to the main feed so the user can see their photo
//                            Intent intent = new Intent(mContext, HomeActivity.class);
//                            mContext.startActivity(intent);
//                            NextActivity n = new NextActivity();
//                            n.finishA();
                            done[7]=true;
                            if(checkifucompleted())
                            {
                                ((Activity) mContext).finish();
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                mContext.startActivity(intent);
                                NextActivity n = new NextActivity();
                                n.finishA();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Photo upload failed.");
                            Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            if (progress - 15 > mPhotoUploadProgress) {
                                Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                                mPhotoUploadProgress = progress;
                            }

                            Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                        }
                    });
                }
            }
        if(image_count>8)
            if (!imgUrl[8].isEmpty()) {
                final FilePaths filePaths = new FilePaths();
                //case1) new photo
                if (photoType.equals(mContext.getString(R.string.new_photo))) {
                    Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

                    final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    StorageReference storageReference = mStorageReference
                            .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 9));

                    Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[8]);

                    //convert image url to bitmap
                    //if (bm == null) {
                    bm = ImageManager.getBitmap(imgUrl[8]);
                    //}

                    byte[] bytes8 = ImageManager.getBytesFromBitmap(bm, 70);

                    UploadTask uploadTask8 = null;
                    uploadTask8 = storageReference.putBytes(bytes8);

                    uploadTask8.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                            mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 9)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,9,1);
                                }
                            });
                            Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                            //add the new photo to 'photos' node and 'user_photos' node
                            //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                            //navigate to the main feed so the user can see their photo
//                            Intent intent = new Intent(mContext, HomeActivity.class);
//                            mContext.startActivity(intent);
//                            NextActivity n = new NextActivity();
//                            n.finishA();
                            done[8]=true;
                            if(checkifucompleted())
                            {
                                ((Activity) mContext).finish();
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                mContext.startActivity(intent);
                                NextActivity n = new NextActivity();
                                n.finishA();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Photo upload failed.");
                            Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            if (progress - 15 > mPhotoUploadProgress) {
                                Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                                mPhotoUploadProgress = progress;
                            }

                            Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                        }
                    });
                }
            }
        final FilePaths filePaths = new FilePaths();
        //case1) new photo
        if (photoType.equals(mContext.getString(R.string.new_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 2));
            Log.d(TAG, "uploadNewPhotos: share img url : " + imgUrl[1]);

            //convert image url to bitmap
            //if (bm == null) {
            bm = ImageManager.getBitmap(imgUrl[1]);
            //}

            byte[] bytesm = ImageManager.getBytesFromBitmap(bm, 70);

            UploadTask uploadTaskm = null;
            uploadTaskm = storageReference.putBytes(bytesm);

            uploadTaskm.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo_s" + (countt + 2)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            addPhotosToDatabase(caption,task.getResult().toString(),price,type,key,2,1);
                        }
                    });
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                    //add the new photo to 'photos' node and 'user_photos' node
                    //addPhotoToDatabase(caption, firebaseUrl.toString(),price);
                    //navigate to the main feed so the user can see their photo
//                        Intent intent = new Intent(mContext, HomeActivity.class);
//                        mContext.startActivity(intent);
//                        NextActivity n = new NextActivity();
//                        n.finishA();
                    done[1]=true;
                    if(checkifucompleted())
                    {
                        ((Activity) mContext).finish();
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        mContext.startActivity(intent);
                        NextActivity n = new NextActivity();
                        n.finishA();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }

    }

    public boolean checkifucompleted()
    {
        for(boolean b : done) if(!b) return false;
        return true;
    }


   /* public void uploadNewPhotos(final String photoType, final String caption, final int count, final String[] imgUrl,
                                Bitmap bm, final float price, final int type){
        //Issue photo upload horahi h lekin sahi se ni horahi

        success = false;

        myRef.child("image_count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count_main = dataSnapshot.getValue(Integer.class);
                Log.d(TAG, "uploadNewPhotos: count = (inside) "+count_main);
                Log.d(TAG, "uploadNewPhotos: count = (outside) "+count_main);
                url = new String[imgUrl.length];
                int imgc=imgUrl.length;
                Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");
                final String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
                final FilePaths filePaths = new FilePaths();
                //case1) new photo
                for(i=0;i<imgUrl.length;i++)
                {
                    if(photoType.equals(mContext.getString(R.string.new_photo))){
                        urls[i] = count_main + i;
                        Log.d(TAG, "uploadNewPhoto: uploading NEW photos. i " + i +"Url = "+ imgUrl[i]+ " Count = "+(count_main + i));
                        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final StorageReference storageReference = mStorageReference
                                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count_main + i));
                        // convert image url to bitmap
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Bitmap bm = ImageManager.getBitmap(imgUrl[i-1]);
                                byte[] bytes = ImageManager.getBytesFromBitmap(bm, 10);
                                UploadTask uploadTask = null;
                                uploadTask = storageReference.putBytes(bytes);
                                try {
                                    Tasks.await(uploadTask);
                                }catch (InterruptedException e)
                                {
                                    Log.d(TAG, "uploadNewPhotos: " + e.getMessage());
                                }catch (ExecutionException e)
                                {
                                    Log.d(TAG, "uploadNewPhotos: " + e.getMessage());
                                }
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count_main + i)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if(!success) {
                                                    String tags = StringManipulation.getTags(caption);
                                                    Photo photo = new Photo();
                                                    photo.setCaption(caption);
                                                    photo.setDate_created(getTimestamp());
                                                    //photo.setImage_path("none");
                                                    photo.setPrice(price);
                                                    photo.setTags(tags);
                                                    photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    photo.setPhoto_id(newPhotoKey);
                                                    photo.setType(type);
                                                    //insert into database
                                                    myRef.child(mContext.getString(R.string.dbname_user_photos))
                                                            .child(FirebaseAuth.getInstance().getCurrentUser()
                                                                    .getUid()).child(newPhotoKey).setValue(photo);
                                                    myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);
                                                    myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).child("price").setValue(price);
                                                    myRef.child(mContext.getString(R.string.dbname_user_photos))
                                                            .child(FirebaseAuth.getInstance().getCurrentUser()
                                                                    .getUid()).child(newPhotoKey).child("price").setValue(price);
                                                    myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).child("type").setValue(2);
                                                    myRef.child(mContext.getString(R.string.dbname_user_photos))
                                                            .child(FirebaseAuth.getInstance().getCurrentUser()
                                                                    .getUid()).child(newPhotoKey).child("type").setValue(2);
                                                    success=true;

                                                }
                                                increment_order_count(count_main,imgUrl.length);
                                                Log.d(TAG, "onComplete: image url count = " + (count_main + i));
                                                //Log.d(TAG, "onComplete: url = " + task.getResult().toString() +" counter = "+ Integer.toString(url_count));
//                                                myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).child("image_path").child(Integer.toString(url_count)).setValue(task.getResult().toString());
//                                                myRef.child(mContext.getString(R.string.dbname_user_photos))
//                                                        .child(FirebaseAuth.getInstance().getCurrentUser()
//                                                                .getUid()).child(newPhotoKey).child("image_path").child(Integer.toString(url_count)).setValue(task.getResult().toString());
                                                url_count++;

                                                if(url_count==imgUrl.length-1)
                                                {
                                                    temp = 0;
                                                    //for(j=0;j<imgUrl.length;j++)



                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    for(j=0;temp<imgUrl.length;j++) {
                                                                        Thread.sleep(4000);
                                                                        Log.d(TAG, "run: indicator tmp = running");
                                                                        if (!isprocessing) {
                                                                            try {
                                                                                uriTask = mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count_main + temp)).getDownloadUrl();
                                                                            }catch (NullPointerException e)
                                                                            {
                                                                                Toast.makeText(mContext, "Something went wrong try again",Toast.LENGTH_LONG).show();
                                                                            }

                                                                            isprocessing = true;
                                                                            Log.d(TAG, "run: temp = " + temp + " uri = " + uriTask.toString()+" count main + temp = "+(count_main+temp));
                                                                            try {
                                                                                Tasks.await(uriTask);
                                                                            } catch (InterruptedException e) {
                                                                                Log.d(TAG, "uploadNewPhotos: " + e.getMessage());
                                                                            } catch (ExecutionException e) {
                                                                                Log.d(TAG, "uploadNewPhotos: " + e.getMessage());
                                                                            }
                                                                            uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Uri> task) {
                                                                                    try{
                                                                                        Log.d(TAG, "onComplete: j = " + j + " tmp = " + temp + " URL = " + task.getResult().toString());
                                                                                        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).child("image_path").child(Integer.toString(temp + 1)).setValue(task.getResult().toString());
                                                                                        myRef.child(mContext.getString(R.string.dbname_user_photos))
                                                                                                .child(FirebaseAuth.getInstance().getCurrentUser()
                                                                                                        .getUid()).child(newPhotoKey).child("image_path").child(Integer.toString(temp + 1)).setValue(task.getResult().toString());
                                                                                        temp++;
                                                                                        isprocessing = false;
                                                                                    }catch (NullPointerException e)
                                                                                    {
                                                                                        Toast.makeText(mContext, "Something went wrong try again",Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                    }

                                                                            });
                                                                        }


                                                                        if (j == imgUrl.length-1) {

                                                                            Intent intent = new Intent(mContext, HomeActivity.class);
                                                                            mContext.startActivity(intent);
                                                                            NextActivity n = new NextActivity();
                                                                            n.finishA();
                                                                        }
                                                                    }
                                                                }catch (Exception e) {
                                                                    e.getLocalizedMessage();
                                                                }
                                                            }

                                                    }).start();


                                            }
                                            }
                                        });
//                      add the new photo to 'photos' node and 'user_photos' node
//                      addPhotoToDatabase(caption, firebaseUrl.toString(),price);
//                      navigate to the main feed so the user can see their photo
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Photo upload failed.");
                                        Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                        if(progress - 15 > mPhotoUploadProgress){
                                            Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                                            mPhotoUploadProgress = progress;
                                        }

                                        Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                                    }
                                });
                            }
                        }).start();
                    }
                }
                if(success) {
                    //addPhotosToDatabase(caption,url,price,type,newPhotoKey);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });



    }*/
    public int increment_order_count(int count_no,int count)
    {
        try {
            DatabaseReference myRef;
            myRef = FirebaseDatabase.getInstance().getReference();
            Log.d(TAG, "upload_details: order no = " + count_main);
            myRef.child("image_count").setValue(count_main + count);
        }catch (Exception e)
        {
            Log.d(TAG, "increment_order_count: exception"+ e.toString());
        }
        return count_no-1;
    }

    public void uploadNewStory(Intent intent, final HomeFragment fragment){
        Log.d(TAG, "uploadNewStory: attempting to upload new story to storage.");

        final String uri = intent.getDataString();
        final boolean deleteCompressedVideo = intent.getBooleanExtra(MaterialCamera.DELETE_UPLOAD_FILE_EXTRA, false);
         /*
            upload a new photo to firebase storage
         */
        if(!isMediaVideo(uri)){
            Log.d(TAG, "uploadNewStory: uploading new story (IMAGE) to firebase storage.");
            fragment.mStoriesAdapter.startProgressBar();
            final FilePaths filePaths = new FilePaths();

            //specify where the photo will be stored
            final StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_STORY_STORAGE + "/" + userID + "/" + uri.substring(uri.indexOf("Stories/") + 8, uri.indexOf(".")));

            BackgroundGetBytesFromBitmap getBytes = new BackgroundGetBytesFromBitmap();
            byte[] bytes = getBytes.doInBackground(uri);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // Uri firebaseURL = taskSnapshot.getDownloadUrl();
                    fragment.mStoriesAdapter.stopProgressBar();
                    Toast.makeText(mContext, "Upload Success", Toast.LENGTH_SHORT).show();
                    //addNewStoryImageToDatabase(firebaseURL.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    fragment.mStoriesAdapter.stopProgressBar();
                    Toast.makeText(mContext, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            Log.d(TAG, "uploadNewStory: uploading new story (VIDEO) to firebase storage.");
            fragment.mStoriesAdapter.startProgressBar();
            final FilePaths filePaths = new FilePaths();

            //specify where the photo will be stored
            final StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_STORY_STORAGE + "/" + userID + "/" + uri.substring(uri.indexOf("Stories/") + 8, uri.indexOf(".")));


            FileInputStream fis = null;
            File file = new File(uri);
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] bytes = new byte[0];
            try {
                bytes = readBytes(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "uploadNewStory: video upload bytes: " + bytes.length);
            final byte[] uploadBytes = bytes;

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri firebaseURL = taskSnapshot.getDownloadUrl();
                    fragment.mStoriesAdapter.stopProgressBar();
                    Toast.makeText(mContext, "Upload Success", Toast.LENGTH_SHORT).show();
                    //addNewStoryVideoToDatabase(firebaseURL.toString(), uploadBytes);

                    if(deleteCompressedVideo){
                        deleteOutputFile(uri);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    fragment.mStoriesAdapter.stopProgressBar();
                    Toast.makeText(mContext, "Upload Failed", Toast.LENGTH_SHORT).show();
                    if(deleteCompressedVideo){
                        deleteOutputFile(uri);
                    }
                }
            });
        }
    }

    private class BackgroundGetBytesFromBitmap extends AsyncTask<String, Integer, byte[]> {

        @Override
        protected byte[] doInBackground(String... params) {
            byte[] bytes = null;

//            Bitmap bm = ImageManager.getBitmap(Uri.parse(params[0]).getPath());
            Bitmap bm = null;
            try{
                RotateBitmap rotateBitmap = new RotateBitmap();
                bm = rotateBitmap.HandleSamplingAndRotationBitmap(mContext, Uri.parse("file://" + params[0]));
            }catch (IOException e){
                Log.e(TAG, "BackgroundGetBytesFromBitmap: IOException: " + e.getMessage());
            }

            bytes = ImageManager.getBytesFromBitmap(bm, ImageManager.IMAGE_SAVE_QUALITY);
            return bytes;
        }
    }


    private void deleteOutputFile(@Nullable String uri) {
        if (uri != null)
            //noinspection ResultOfMethodCallIgnored
            new File(Uri.parse(uri).getPath()).delete();
    }


    public byte[] readBytes(FileInputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }


    private void addNewStoryImageToDatabase(String url){
        Log.d(TAG, "addNewStoryToDatabase: adding new story to database.");

        Story story =  new Story();
        story.setImage_url(url);
        String newKey = myRef.push().getKey();
        story.setStory_id(newKey);
        story.setTimestamp(getTimestamp());
        story.setUser_id(userID);
        story.setViews("0");

        myRef.child(mContext.getString(R.string.dbname_stories))
                .child(userID)
                .child(newKey)
                .setValue(story);

    }

    private void addNewStoryVideoToDatabase(String url, byte[] bytes){
        Log.d(TAG, "addNewStoryToDatabase: adding new story to database.");

        Story story =  new Story();
        story.setVideo_url(url);
        String newKey = myRef.push().getKey();
        story.setStory_id(newKey);
        story.setTimestamp(getTimestamp());
        story.setUser_id(userID);
        story.setViews("0");

        // calculate the estimated duration.
        // need to do this for the progress bars in the block. We can't get the video duration of MP4 files
        double megabytes = bytes.length / 1000000.000;
        Log.d(TAG, "addNewStoryVideoToDatabase: estimated MB: " + megabytes);
        String duration = String.valueOf(Math.round(15 * (megabytes / 6.3)));
        Log.d(TAG, "addNewStoryVideoToDatabase: estimated video duration: " + duration);
        story.setDuration(duration);

        myRef.child(mContext.getString(R.string.dbname_stories))
                .child(userID)
                .child(newKey)
                .setValue(story);

    }

    private boolean isMediaVideo(String uri){
        if(uri.contains(".mp4") || uri.contains(".wmv") || uri.contains(".flv") || uri.contains(".avi")){
            return true;
        }
        return false;
    }

    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url,float price, int type){
        Log.d(TAG, "addPhotoToDatabase: adding photo and price to database.");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);

        photo.setPrice((long)price);
        photo.setTags(tags.toLowerCase().trim());
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);
        photo.setType(type);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(photo);

        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).child("price").setValue(price);
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).child("price").setValue(price);

        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).child("type").setValue(1);
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).child("type").setValue(1);


    }

    private void addPhotosToDatabase(String caption, String url,float price, int type,String newPhotoKey,int c,int done){
        Log.d(TAG, "addPhotoToDatabase: adding photo and price to database.");
        String co = String.valueOf(c);
        if(done==0) {
            String tags = StringManipulation.getTags(caption);
            Photo photo = new Photo();
            photo.setCaption(caption);
            photo.setDate_created(getTimestamp());
            photo.setImage_path("none");
            photo.setPrice((long)price);
            photo.setTags(tags.toLowerCase().trim());
            photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
            photo.setPhoto_id(newPhotoKey);
            photo.setType(type);
            //insert into database
            myRef.child(mContext.getString(R.string.dbname_user_photos))
                    .child(FirebaseAuth.getInstance().getCurrentUser()
                            .getUid()).child(newPhotoKey).setValue(photo);
            myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);
            myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).child("price").setValue(price);
            myRef.child(mContext.getString(R.string.dbname_user_photos))
                    .child(FirebaseAuth.getInstance().getCurrentUser()
                            .getUid()).child(newPhotoKey).child("price").setValue(price);


            myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).child("type").setValue(2);
            myRef.child(mContext.getString(R.string.dbname_user_photos))
                    .child(FirebaseAuth.getInstance().getCurrentUser()
                            .getUid()).child(newPhotoKey).child("type").setValue(2);
        }
            myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).child("image_path").child(co).setValue(url);
            myRef.child(mContext.getString(R.string.dbname_user_photos))
                    .child(FirebaseAuth.getInstance().getCurrentUser()
                            .getUid()).child(newPhotoKey).child("image_path").child(co).setValue(url);
    }

    private void addPhotoPrice(float price)
    {
        Log.d(TAG, "addPhotoPrice: adding price");
    }

    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }

    /**
     * Update 'user_account_settings' node for the current user
     * @param displayName
     * @param website
     * @param description
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber){

        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");

        if(displayName != null){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
        }


        if(website != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }

        if(description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }

        if(phoneNumber != 0) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
        }
    }

    /**
     * update username in the 'users' node and 'user_account_settings' node
     * @param username
     */
    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: upadting username to: " + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    /**
     * update the email in the 'user's' node
     * @param email
     */
    public void updateEmail(String email){
        Log.d(TAG, "updateEmail: upadting email to: " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

    }

//    public boolean checkIfUsernameExists(String username, DataSnapshot datasnapshot){
//        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");
//
//        User user = new User();
//
//        for (DataSnapshot ds: datasnapshot.child(userID).getChildren()){
//            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);
//
//            user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());
//
//            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
//                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username, final String phone){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        boolean connected = false;
                        ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                            //we are connected to a network
                            connected = true;
                        }
                        else {
                            connected = false;
                        }
                        if(connected) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, "Email already exists",
                                        Toast.LENGTH_SHORT).show();
//                            Toast.makeText(mContext, R.string.auth_failed,
//                                    Toast.LENGTH_SHORT).show();

                            } else if (task.isSuccessful()) {
                                //send verificaton email
                                sendVerificationEmail();

                                userID = mAuth.getCurrentUser().getUid();
                                Log.d(TAG, "onComplete: Authstate changed: " + userID);
                            }
                        }else
                        {
                            Toast.makeText(mContext, "Check your connection",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Add information to the users nodes
     * Add information to the user_account_settings node
     * @param email
     * @param username
     * @param description
     * @param website
     * @param profile_photo
     */
    public void addNewUser(String email, String username, String description, String website, String profile_photo, String phones){

        long phone = Long.parseLong(phones);

        User user = new User( userID,  phone,  email,  StringManipulation.condenseUsername(username) );

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);


        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                StringManipulation.condenseUsername(username),
                website,
                userID

        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);

    }


    /**
     * Retrieves the account settings for teh user currently logged in
     * Database: user_acount_Settings node
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserSettings: retrieving user account settings from firebase.");


        UserAccountSettings settings  = new UserAccountSettings();
        User user = new User();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            // user_account_settings node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserSettings: user account settings node datasnapshot: " + ds);

                try {

                    settings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()
                    );
                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );
                    settings.setWebsite(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getWebsite()
                    );
                    settings.setDescription(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription()
                    );
                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    settings.setPosts(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()
                    );
                    settings.setFollowing(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()
                    );
                    settings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information: " + settings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }
            }


                // users node
                Log.d(TAG, "getUserSettings: snapshot key: " + ds.getKey());
                if(ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                    Log.d(TAG, "getUserAccountSettings: users node datasnapshot: " + ds);

                    user.setUsername(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUsername()
                    );
                    user.setEmail(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getEmail()
                    );
                    user.setPhone_number(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getPhone_number()
                    );
                    user.setUser_id(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUser_id()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved users information: " + user.toString());
                }
        }
        return new UserSettings(user, settings);

    }

    void urlTask(Task<Uri> uriTask,final int temp,final String newPhotoKey)
    {

    }

}