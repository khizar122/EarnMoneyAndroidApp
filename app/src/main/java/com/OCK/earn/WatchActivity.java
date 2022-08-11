package com.OCK.earn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.OCK.earn.model.ProfileModel;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.adlisteners.VideoListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.HashMap;

public class WatchActivity extends AppCompatActivity implements MaxRewardedAdListener {

    private Button watchBtn1, watchBtn2;
    private TextView coinsEt;
    DatabaseReference reference;
    StartAppAd startAppAd;
    private String unityGameID = "4380485";
    private Boolean testMode = false;
    private String adUnitId = "Rewarded_Android";
    int click = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private MaxRewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //208310562
        StartAppSDK.init(this, "208310562", true);
        setContentView(R.layout.activity_watch);
        init();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        startAppAd = new StartAppAd(this);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        loadData();
        createRewardedAd();
        clickListener();
        final UnityAdsListener myAdsListener = new UnityAdsListener();
        UnityAds.addListener(myAdsListener);
        UnityAds.initialize(this, unityGameID, testMode);

    }

    private void clickListener() {
        watchBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clc = pref.getInt("click", 0);

                if (clc == 0) {
                    click = 1;
                    editor.putInt("click", click);
                    editor.apply();
                    DisplayRewardedAd();
                }

                else if (clc == 1) {
                    click = 2;
                    editor.putInt("click", click);
                    editor.apply();
                    if (rewardedAd.isReady()) {
                        rewardedAd.showAd();
                    } else if (!rewardedAd.isReady()) {
                        Toast.makeText(WatchActivity.this, "Ad Not Loaded", Toast.LENGTH_SHORT).show();
                    }
                }

                else if (clc == 2) {
                    rewardAd();
                    editor.clear();
                    editor.apply();
                }
            }


        });
        watchBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                displayOfferWall(view);
            }
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Watch & Earn");

        watchBtn1 = findViewById(R.id.watchBtn1);
        watchBtn2 = findViewById(R.id.watchBtn2);
        coinsEt = findViewById(R.id.coinEt);

    }


    @Override
    public void onBackPressed() {

        Intent it = new Intent(WatchActivity.this, MainActivity.class);
        startActivity(it);
        finish();

    }

    private void loadData() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileModel model = snapshot.getValue(ProfileModel.class);
                coinsEt.setText(String.valueOf(model.getCoins()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WatchActivity.this, "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateDataFirebase() {
        int currentCoins = Integer.parseInt(coinsEt.getText().toString());
        int updateCoin = currentCoins + 10;
        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", updateCoin);
        reference.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(WatchActivity.this, "Coins added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void displayOfferWall(View view) {
        startAppAd.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {
                // Grant user with the reward
                Toast.makeText(WatchActivity.this, "Grant user with the reward", Toast.LENGTH_SHORT).show();
                updateDataFirebase();
            }
        });
        startAppAd.loadAd(StartAppAd.AdMode.OFFERWALL, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                Toast.makeText(WatchActivity.this, "Can't Grant user with the reward", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void rewardAd() {

        startAppAd.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {
                message("Click and Install the App to get your Reward");
                //// updateDataFirebase();

            }
        });

        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                message("Error : " + ad.getErrorMessage());
                Log.i("TAG", "adNotDisplayed" + ad.getErrorMessage());
                startAppAd.showAd(new AdDisplayListener() {
                    @Override
                    public void adHidden(Ad ad) {
                        message("adHidden");
                    }

                    @Override
                    public void adDisplayed(Ad ad) {
                        message("adDisplayed");

                    }

                    @Override
                    public void adClicked(Ad ad) {
                        message("adClicked");
                        updateDataFirebase();
                    }

                    @Override
                    public void adNotDisplayed(Ad ad) {
                        message("Error : " + ad.getErrorMessage());
                        Log.i("TAG", "adNotDisplayed" + ad.getErrorMessage());
                    }
                });
            }
        });
    }

    // Implement a function to display an ad if the Ad Unit is ready:
    public void DisplayRewardedAd() {
        if (UnityAds.isReady(adUnitId)) {
            UnityAds.show(this, adUnitId);
        }
    }

    public void message(String message) {
        Toast.makeText(WatchActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRewardedVideoStarted(MaxAd ad) {
        message("Download the APP to get extra Reward..");
    }

    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {

        message("Download the APP to get extra Reward..");
    }

    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {

        updateDataFirebase();
        // rewardedAd.loadAd();
        Intent it = new Intent(WatchActivity.this, MainActivity.class);
        startActivity(it);
        finish();

    }

    @Override
    public void onAdLoaded(MaxAd ad) {

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        Toast.makeText(this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
        rewardedAd.loadAd();

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        //rewardedAd.loadAd();
    }

    private class UnityAdsListener implements IUnityAdsListener {

        @Override
        public void onUnityAdsReady(String adUnitId) {
            // Implement functionality for an ad being ready to show.
        }

        @Override
        public void onUnityAdsStart(String adUnitId) {
            // Implement functionality for a user starting to watch an ad.
        }

        @Override
        public void onUnityAdsFinish(String adUnitId, UnityAds.FinishState finishState) {
            // Implement functionality for a user finishing an ad.
            if (finishState.equals(UnityAds.FinishState.COMPLETED)) {
                updateDataFirebase();


            } else if (finishState.equals(UnityAds.FinishState.SKIPPED)) {
                // Do not reward the user for skipping the ad.
                Toast.makeText(WatchActivity.this, "Reward Value 0", Toast.LENGTH_SHORT).show();


            } else if (finishState.equals(UnityAds.FinishState.ERROR)) {
                // Log an error.
                message("Error Occured");

            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
            // Implement functionality for a Unity Ads service error occurring.
            message("" + message);
        }
    }


    void createRewardedAd() {
        rewardedAd = MaxRewardedAd.getInstance("fcace148a73dbd5a", this);
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
    }
}