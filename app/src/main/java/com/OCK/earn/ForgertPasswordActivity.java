package com.OCK.earn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgertPasswordActivity extends AppCompatActivity {

    private EditText resetEmailPass;
    private Button reset_btn;
     ProgressBar progressBar;
     FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgert_password);
        resetEmailPass=(EditText)findViewById(R.id.reset_email);
        reset_btn=(Button)findViewById(R.id.resetpass_btn);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        auth=FirebaseAuth.getInstance();
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
    String email=resetEmailPass.getText().toString().trim();
    if (email.isEmpty())
    {
        resetEmailPass.setError("Email is Required");
        resetEmailPass.requestFocus();
        return;
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
    {
        resetEmailPass.setError("Valid Email Address");
        resetEmailPass.requestFocus();
        return;
    }
    progressBar.setVisibility(View.VISIBLE);
    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful())
            {
                Toast.makeText(ForgertPasswordActivity.this, "Check your Email to reset your Password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
            else
            {
                Toast.makeText(ForgertPasswordActivity.this, "Try again something wrong happen", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    });

    }
}