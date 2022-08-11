package com.OCK.earn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.*;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    private Button register;
    private EditText InputName,InputPhone,InputEmail,Inputpassword,InputCpassword;
    private ProgressDialog loadingBar;
    private FirebaseAuth auth;
    private String deviceID;


    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth=FirebaseAuth.getInstance();
        deviceID= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        init();
        clickListener();


    }
    private void init()
    {
        register=(Button)findViewById(R.id.register_btn);
        InputName=(EditText)findViewById(R.id.register_fullname);
        InputPhone=(EditText)findViewById(R.id.register_phone);
        InputEmail=(EditText)findViewById(R.id.register_email);
        Inputpassword=(EditText)findViewById(R.id.register_password);
        InputCpassword=(EditText)findViewById(R.id.register_cpassword);
        loadingBar=new ProgressDialog(this);
    }

    private void  clickListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = InputName.getText().toString().trim();
                String phone = InputPhone.getText().toString().trim();
                String email = InputEmail.getText().toString().trim();
                String password = Inputpassword.getText().toString().trim();
                String cpassword = InputCpassword.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    InputName.setError("Please Enter Fullname");
                } else if (!Patterns.PHONE.matcher(phone).matches()) {
                    InputPhone.setError("Enter Valid Phone Number");
                } else if (phone.length() <= 9) {
                    InputPhone.setError("Less then 10 digit Mobile Number not allowed");
                } else if (phone.length() >= 11) {
                    InputPhone.setError("More then 10 digit Mobile Number not allowed");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    InputEmail.setError("Enter Email Address Invalid");
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegistrationActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(cpassword)) {
                    InputCpassword.setError("Password don't Match");
                } else {
                    loadingBar.setTitle("Create Account");
                    loadingBar.setMessage("Please wait we are Checking Credentials");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                   queryAccountExistence(email,password);

                }
            }

        });
    }


    private void queryAccountExistence(final String email,final String password) {
       DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
        Query query=ref.orderByChild("deviceID").equalTo(deviceID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Toast.makeText(RegistrationActivity.this, "This device Already Register with Another Email", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    finish();
                }
                else
                {
                    CreateAccount(email,password);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            loadingBar.dismiss();
                Toast.makeText(RegistrationActivity.this, "Error : "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void CreateAccount(final String email, final String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            //Registration
                            final FirebaseUser user=auth.getCurrentUser();
                            assert user != null;

                            //send email verification
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                updateUi(user,email);
                                            }
                                            else
                                            {
                                                Toast.makeText(RegistrationActivity.this, "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }

                                        }
                                    });

                        }
                        else
                        {
                            Toast.makeText(RegistrationActivity.this, "Error"+
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }
    private void updateUi(FirebaseUser user,String email) {
        String refer=email.substring(0,email.lastIndexOf("@"));
        String  referCode=refer.replace(".","");

        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",user.getUid());
        map.put("name",InputName.getText().toString());
        map.put("phone",InputPhone.getText().toString());
        map.put("email",InputEmail.getText().toString());
        map.put("image", " ");
        map.put("coins",0);
        map.put("referCode",referCode);
        map.put("spins",2);
        map.put("deviceID",deviceID);
        map.put("redeemed",false);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar calendar=Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH,-1);
        Date previousDate=calendar.getTime();
        String dateString= dateFormat.format(previousDate);
        FirebaseDatabase.getInstance().getReference().child("Daily Check")
                .child(user.getUid())
                .child("date")
                .setValue(dateString);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(user.getUid())
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegistrationActivity.this, "Register! Please Verify your Email,check your Spam folder if its not in Inbox", Toast.LENGTH_LONG).show();
                                 startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                         finish();
                        }
                        else
                        {
                            Toast.makeText(RegistrationActivity.this, "Error:"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           loadingBar.dismiss();
                            finish();
                        }

                    }
                });

    }
}