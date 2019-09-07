package com.luxuryshauk.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.luxuryshauk.R;

public class ResetActivity extends AppCompatActivity {

    private EditText email;
    private Button reset_btn;
    Context mContext;
    String TAG = "ResetActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = ResetActivity.this;
        setContentView(R.layout.activity_reset);
        email = findViewById(R.id.input_email);
        reset_btn = findViewById(R.id.btn_reset);


        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().isEmpty())
                {
                    final String s = email.getText().toString();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(s)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(mContext, "Reset Link sent to " + s, Toast.LENGTH_LONG).show();
                                        finish();
                                        Intent i = new Intent(ResetActivity.this, LoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(mContext, "Enter Email!", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
