package com.OCK.earn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.OCK.earn.fragment.FragmentReplacerActivity;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
//import com.google.android.gms.ads.AdError;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//
//import com.OCK.earn.fragment.FragmentReplacerActivity;
//import com.google.android.gms.ads.FullScreenContentCallback;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.LoadAdError;


import static com.OCK.earn.model.Veriables.PaytmCardImageURL;
import static com.OCK.earn.model.Veriables.amazonGiftCardImageURL;

public class WithdrawActivity extends AppCompatActivity {
    private ImageView amazonImage,PaytmImage;
    private CardView amazonCard,PaytmCard,redeemHistoryCard,payoneer;
   // private InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd mInterstitial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        init();

       // loadImage();
        loadInterstitialAd();

        clickListener();


    }
    private void init()
    {
        PaytmCard=findViewById(R.id.paytmcard);
        PaytmImage=findViewById(R.id.paytmimage);
        amazonCard=findViewById(R.id.amazonGiftCard);
        amazonImage=findViewById(R.id.amazonImage);
        redeemHistoryCard=findViewById(R.id.redeemHistoryCard);
        payoneer = findViewById(R.id.payoneer);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void loadImage()
    {

        Glide.with(WithdrawActivity.this)
                .load(amazonGiftCardImageURL)
                .into(amazonImage);

        Glide.with(WithdrawActivity.this)
                .load(PaytmCardImageURL)
                .into(PaytmImage);
    }

    private void clickListener()
    {
        redeemHistoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WithdrawActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    amazonCard.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent= new Intent(WithdrawActivity.this, FragmentReplacerActivity.class);
            intent.putExtra("position",1);
            startActivity(intent);
        }
    });
    PaytmCard.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent= new Intent(WithdrawActivity.this, FragmentReplacerActivity.class);
            intent.putExtra("position",4);
            startActivity(intent);
        }
    });
    payoneer.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent= new Intent(WithdrawActivity.this, FragmentReplacerActivity.class);
            intent.putExtra("position",5);
            startActivity(intent);
        }
    });
    }
    private void loadInterstitialAd()
    {

        //Admob ads
//        interstitialAd= new InterstitialAd(this);
//        interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
//        interstitialAd.loadAd(new AdRequest.Builder().build());

        //facebook ads
//        mInterstitial=new com.facebook.ads.InterstitialAd(this,getString(R.string.fb_interstitial_id));
//        mInterstitial.loadAd();
    }

    @Override
    public void onBackPressed() {

        Intent it = new Intent(WithdrawActivity.this,MainActivity.class);
        startActivity(it);
        finish();
//
//     //       facebook
//        if(mInterstitial.isAdLoaded()){
//            mInterstitial.show();
//            mInterstitial.setAdListener(new InterstitialAdListener() {
//                @Override
//                public void onInterstitialDisplayed(Ad ad) {
//
//                }
//
//                @Override
//                public void onInterstitialDismissed(Ad ad) {
//                    finish();
//                }
//
//
//                @Override
//                public void onError(Ad ad, com.facebook.ads.AdError adError) {
//
//                }
//
//                @Override
//                public void onAdLoaded(Ad ad) {
//
//                }
//
//                @Override
//                public void onAdClicked(Ad ad) {
//
//                }
//
//                @Override
//                public void onLoggingImpression(Ad ad) {
//
//                }
//            });
//            return;
//        }
            //admob
//        if(interstitialAd.isLoaded())
//        {
//            interstitialAd.show();
//            interstitialAd.setAdListener(new AdListener(){
//
//                @Override
//                public void onAdClosed() {
//                    super.onAdClosed();
//                    finish();
//                }
//            });
//            return;
//        }
//
//        finish();
    }
}