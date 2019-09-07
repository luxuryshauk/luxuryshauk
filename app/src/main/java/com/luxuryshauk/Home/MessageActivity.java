package com.luxuryshauk.Home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.luxuryshauk.Profile.AccountSettingsActivity;
import com.luxuryshauk.R;
import com.luxuryshauk.Utils.FilePaths;
import com.luxuryshauk.Utils.FirebaseMethods;
import com.luxuryshauk.models.User;
import com.squareup.picasso.Picasso;
import com.luxuryshauk.models.Chat;
import com.luxuryshauk.Utils.MessageAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

import static com.luxuryshauk.Profile.EditProfileFragment.applicationContext;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView Username;

    List<Chat> mchat;
    MessageAdapter messageAdapter;


    FirebaseUser fuser;
    DatabaseReference reference;

    Intent intent;

    ImageButton btn_send;
    ImageButton attach;
    EditText text_send;
    String userid;
    int index=0;

    long count;
    RecyclerView recyclerView;

    ValueEventListener seenListener;
    String TAG = "Message Activity";

    boolean notify = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        attach = findViewById(R.id.attach);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        Username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        intent = getIntent();
        userid = intent.getStringExtra("userId");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "onCreate: fuser = "+ fuser.getUid() + "Username = " + fuser);
//        final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        FirebaseDatabase.getInstance().getReference("Chats").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chat chat = snapshot.getValue(Chat.class);
//                    try {
//                        if (chat.getReceiver().equals(currentUid) && !chat.isIsseen()) {
//                            String chatid = snapshot.getKey().toString();
//                            FirebaseDatabase.getInstance().getReference("Chats").child(chatid).child("isseen").setValue(true);
//                        }
//                    }catch (Exception e){
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        reference = FirebaseDatabase.getInstance().getReference("user_account_settings").child(userid).child("profile_photo");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profile = dataSnapshot.getValue(String.class);
                if(!profile.equals("")) {
                    Picasso.get().load(profile).into(profile_image);
                }else{
                    profile_image.setImageResource(R.drawable.ic_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("users").child(userid);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fuser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Username.setText(user.getUsername());
                readMesagges(fuser.getUid(), userid, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(String sender, final String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        final String key = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid()).push().getKey();

        FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(key)
                .child(userid)
                .child("id").setValue(userid);

//        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//                .child(fuser.getUid())
//                .child(key)
//                .child(userid);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid());
//                .child(key)
//                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    for(DataSnapshot ads:ds.getChildren())
                    {
                        Log.d(TAG, "onDataChange: keyyy = " + ads.getKey() + " userid = "+userid+" pre key = " + key );
                        if(ads.getKey().equals(userid) && !ds.getKey().equals(key))
                        {
                            chatRef.child(ds.getKey()).setValue(null);
                        }
                    }
                }
//                if (!dataSnapshot.exists()){
//                    chatRef.child("id").setValue(userid);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid);
                //.child(key)
                //.child(fuser.getUid());
        FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(key)
                .child(fuser.getUid())
                .child("id").setValue(fuser.getUid());
        //chatRefReceiver.child("id").setValue(fuser.getUid());


        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    for(DataSnapshot ads:ds.getChildren())
                    {
                        Log.d(TAG, "sendMessage: --------reciever keyyy = --------");
                        Log.d(TAG, "onDataChange: reciever keyyy = ads.getKey().equals(userid) = " + ads.getKey().equals(fuser.getUid()));
                        Log.d(TAG, "onDataChange: reciever keyyy = !ds.getKey().equals(key) = "+ !ds.getKey().equals(key));
                        Log.d(TAG, "onDataChange: reciever keyyy = " + ads.getKey() + " userid ="+userid+ " fuser.getUid() = "+fuser.getUid()+" pre key = " + key );
                        Log.d(TAG, "onDataChange: reciever keyyy = ads.key = "+ads.getKey() +" ds.key = "+ds.getKey());
                        if(ads.getKey().equals(fuser.getUid()) && !ds.getKey().equals(key))
                        {
                            chatRefReceiver.child(ds.getKey()).setValue(null);
                        }
                    }
                }
//                if (!dataSnapshot.exists()){
//                    chatRef.child("id").setValue(userid);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                  Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
//                        if(chat.getMessage().contains("~img : ")) {
//                            Chat imgChat = new Chat();
//                            imgChat = chat;
//                            imgChat.setMessage("[Image]");
//                            mchat.add(imgChat);
//                        }else
//                        {
                            mchat.add(chat);
//                        }
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Uri filePath;
    public static final int PICK_IMAGE =1;
    private final int PICK_IMAGE_REQUEST = 71;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    FirebaseStorage storage;

    private void chooseImage() {
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                applicationContext = AccountSettingsActivity.getContextOfApplication();
                //Log.d(TAG, "onActivityResult: URI = "+filePath.toString() + " Application Context = " + MessageActivity.this.applicationContext.toString());
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(MessageActivity.this.getApplicationContext().getContentResolver(), filePath);
//                imageView.setImageBitmap(bitmap);
                uploadImage(filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private void uploadImage(Uri filePath) {

        final FilePaths filePaths = new FilePaths();

        userID = mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        storageReference = storage.getReference();

        if(filePath != null)
        {
            final StorageReference ref = storageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + userID + "/chat_attach" + Math.random());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri = uri;
                                    //myRef.child("user_account_settings").child(userID).child("profile_photo").setValue(downloadUri.toString());
                                    sendMessage(fuser.getUid(), userid, "~img : " + downloadUri.toString());
                                }

                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MessageActivity.this, HomeActivity.class);
        intent.putExtra("from_chat", "1");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return;

    }
}
