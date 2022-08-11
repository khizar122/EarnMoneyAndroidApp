package com.OCK.earn.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.OCK.earn.WatchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.OCK.earn.R;
import com.OCK.earn.model.ProfileModel;
import com.OCK.earn.spin.SpinItem;
import com.OCK.earn.spin.WheelView;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.adlisteners.VideoListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class LuckySpin extends Fragment {

    private Button playBtn, watchBtn;
    private TextView coinEt;
    private WheelView wheelView;
    List<SpinItem> spinItemsList = new ArrayList<>();
    private FirebaseUser user;
    DatabaseReference reference;
    int currentSpins;
    StartAppAd startAppAd ;
    private String unityGameID = "4380485";
    private Boolean testMode = false;
    private String adUnitId = "Rewarded_Android";

    public LuckySpin() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lucky_spin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StartAppSDK.init(getContext(), "208310562", true);
        init(view);
        loadData();
        spinList();
        clickListener();
        UnityAdsListener myAdsListener = new UnityAdsListener();
        UnityAds.addListener(myAdsListener);
        UnityAds.initialize(getContext(), unityGameID, testMode);


    }

    private void init(View view) {
        watchBtn = view.findViewById(R.id.watchBtn);
        playBtn = view.findViewById(R.id.playBtn);
        coinEt = view.findViewById(R.id.coinEt);
        wheelView = view.findViewById(R.id.wheelView);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void spinList() {
        SpinItem item1 = new SpinItem();
        item1.text = "0";
        item1.color = 0xffFFF3E0;
        spinItemsList.add(item1);

        SpinItem item2 = new SpinItem();
        item2.text = "5";
        item2.color = 0xffFFE0B2;
        spinItemsList.add(item2);

        SpinItem item3 = new SpinItem();
        item3.text = "3";
        item3.color = 0xffFFCC80;
        spinItemsList.add(item3);

        SpinItem item4 = new SpinItem();
        item4.text = "8";
        item4.color = 0xffFFF3E0;
        spinItemsList.add(item4);

        SpinItem item5 = new SpinItem();
        item5.text = "7";
        item5.color = 0xffFFE0B2;
        spinItemsList.add(item5);

        SpinItem item6 = new SpinItem();
        item6.text = "15";
        item6.color = 0xffFFCC80;
        spinItemsList.add(item6);

        SpinItem item7 = new SpinItem();
        item7.text = "10";
        item7.color = 0xffFFF3E0;
        spinItemsList.add(item7);

        SpinItem item8 = new SpinItem();
        item8.text = "7";
        item8.color = 0xffFFE0B2;
        spinItemsList.add(item8);
        SpinItem item9 = new SpinItem();
        item9.text = "9";
        item9.color = 0xffFFCC80;
        spinItemsList.add(item9);

        SpinItem item10 = new SpinItem();
        item10.text = "5";
        item10.color = 0xffFFF3E0;
        spinItemsList.add(item10);

        SpinItem item11 = new SpinItem();
        item11.text = "11";
        item11.color = 0xffFFE0B2;
        spinItemsList.add(item11);

        SpinItem item12 = new SpinItem();
        item12.text = "20";
        item12.color = 0xffFFCC80;
        spinItemsList.add(item12);


        wheelView.setData(spinItemsList);
        wheelView.setRound(getRandCircleRound());
        wheelView.LuckyRoundItemSeletedListener(new WheelView.LuckyRoundItemSeletedListener() {
            @Override
            public void LuckyRoundItemSeletedListened(int index) {

                playBtn.setEnabled(true);
                playBtn.setAlpha(1f);
                //Wheel stop rotating
                //  showAd();
                String value = spinItemsList.get(index - 1).text;
                updateDataFirebase(Integer.parseInt(value));

            }
        });
    }

    private void clickListener() {
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = getRandomIndex();
                if (currentSpins >= 1 && currentSpins < 3) {
                    wheelView.startWheelWithTargetIndex(index);
                    Toast.makeText(getActivity(), "Watch Video get More Spins", Toast.LENGTH_SHORT).show();
                    watchBtn.setVisibility(View.VISIBLE);
                }
                if (currentSpins < 1) {
                    playBtn.setEnabled(false);
                    playBtn.setAlpha(.6f);
                    Toast.makeText(getActivity(), "Watch Video get More Spins", Toast.LENGTH_SHORT).show();
                    watchBtn.setVisibility(View.VISIBLE);
                } else {


                    playBtn.setEnabled(false);
                    playBtn.setAlpha(.6f);

                    wheelView.startWheelWithTargetIndex(index);
                    watchBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        watchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               DisplayRewardedAd();

            }
        });
    }


    private int getRandomIndex() {
        int[] index = new int[]{1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 9, 9, 10, 11, 12};
        int random = new Random().nextInt(index.length);
        return index[random];
    }

    private int getRandCircleRound() {
        Random random = new Random();
        return random.nextInt(10) + 15;
    }

    private void loadData() {
        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProfileModel model = snapshot.getValue(ProfileModel.class);
                        coinEt.setText(String.valueOf(model.getCoins()));
                        currentSpins = model.getSpins();

                        String currentSpin = "Spin The Wheel" + String.valueOf(currentSpins);
                        playBtn.setText(currentSpin);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error:" + error.getMessage(), Toast.LENGTH_LONG).show();
                        if (getActivity() != null)
                            getActivity().finish();
                    }
                });

    }

    private void updateDataFirebase(int reward) {

        int currentCoins = Integer.parseInt(coinEt.getText().toString());
        int updateCoins = currentCoins + reward;
        int updateSpins = currentSpins - 1;
        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", updateCoins);
        map.put("spins", updateSpins);
        reference.child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Coins Added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public void displayRewardVideoAd() {
        startAppAd = new StartAppAd(getContext());
        startAppAd.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {
                                    int spinup=currentSpins+1;
                                    HashMap<String,Object> map=new HashMap<>();
                                    map.put("spins",spinup);
                                    reference.child(user.getUid()).updateChildren(map);
                Toast.makeText(getContext(), "Grant user with the reward", Toast.LENGTH_SHORT).show();

            }
        });
        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                Toast.makeText(getContext(), "Can't Grant user with the reward", Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void DisplayRewardedAd() {
        if (UnityAds.isReady(adUnitId)) {
            UnityAds.show(getActivity(), adUnitId);
        }
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
                int spinup=currentSpins+1;
                HashMap<String,Object> map=new HashMap<>();
                map.put("spins",spinup);
                reference.child(user.getUid()).updateChildren(map);


            } else if (finishState.equals(UnityAds.FinishState.SKIPPED)) {
                // Do not reward the user for skipping the ad.
                Toast.makeText(getContext(), "Reward Value 0", Toast.LENGTH_SHORT).show();


            } else if (finishState.equals(UnityAds.FinishState.ERROR)) {
                // Log an error.
                Toast.makeText(getContext(), "Error Occured", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
            // Implement functionality for a Unity Ads service error occurring.
            Toast.makeText(getContext(), "Message : "+message, Toast.LENGTH_SHORT).show();
        }
    }
}