package com.OCK.earn;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startapp.sdk.adsbase.StartAppAd;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        version = findViewById(R.id.versioncode);
        StartAppAd.disableSplash();
        try {
            String current = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
       version.setText("Version "+current);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    if (user.isEmailVerified()) {
                        //    getAdmobIds();
                        Intent it = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(it);
                    } else {
                        Intent it = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(it);
                    }
                } else {
                    Intent it = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(it);

                }
                finish();
            }
        }, 800);


    }


}