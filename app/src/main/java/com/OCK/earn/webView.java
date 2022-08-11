package com.OCK.earn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pollfish.classes.SurveyInfo;
import com.pollfish.constants.Position;
import com.pollfish.interfaces.PollfishClosedListener;
import com.pollfish.interfaces.PollfishCompletedSurveyListener;
import com.pollfish.interfaces.PollfishOpenedListener;
import com.pollfish.interfaces.PollfishReceivedSurveyListener;
import com.pollfish.main.PollFish;

public class webView extends AppCompatActivity implements PollfishCompletedSurveyListener, PollfishOpenedListener, PollfishReceivedSurveyListener, PollfishClosedListener {


    private FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @Override
    public void onPollfishClosed() {
        Toast.makeText(this, "Survey Closed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPollfishSurveyCompleted(SurveyInfo surveyInfo) {
        Toast.makeText(this, ""+surveyInfo.getRewardValue(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPollfishOpened() {
        Toast.makeText(this, "Survey Opened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPollfishSurveyReceived(SurveyInfo surveyInfo) {
        Toast.makeText(this, "Received", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PollFish.ParamsBuilder paramsBuilder = new PollFish.ParamsBuilder(getString(R.string.pollfish_api))
                .requestUUID(user.getUid())
                .releaseMode(false)
                .rewardMode(true)
                .offerWallMode(true)
                .indicatorPosition(Position.MIDDLE_RIGHT)
                .indicatorPadding(12)
                .build();
        PollFish.initWith(this, paramsBuilder);

    }

}