package com.OCK.earn;

import static com.tapjoy.TapjoyConnectCore.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.OCK.earn.fragment.FragmentReplacerActivity;
import com.OCK.earn.model.ProfileModel;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pollfish.classes.SurveyInfo;
import com.pollfish.constants.Position;
import com.pollfish.interfaces.PollfishClosedListener;
import com.pollfish.interfaces.PollfishCompletedSurveyListener;
import com.pollfish.interfaces.PollfishOpenedListener;
import com.pollfish.interfaces.PollfishReceivedSurveyListener;
import com.pollfish.main.PollFish;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.Tapjoy;

import org.jsoup.Jsoup;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements PollfishCompletedSurveyListener, PollfishOpenedListener, PollfishReceivedSurveyListener, PollfishClosedListener, MaxAdListener {
    private CardView dailycheckcard, luckydraw, taskcard, referearn, withdraw, watchcard, aboutcard, logoutmain;
    private CircleImageView profileimage;
    private TextView coinearn, nameearn, emailearn;
    Toolbar toolbar;

    DatabaseReference reference;
    FirebaseAuth auth;
    private FirebaseUser user;
    private Dialog dialog;
    float currentVersion;
    Internet internet;
    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;
    StartAppAd startAppAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        StartAppSDK.init(this, "208310562", true);

// TODO: Add adView to your view hierarchy.
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(MainActivity.this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
            }
        });

        setContentView(R.layout.activity_main);


        startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

                Toast.makeText(getContext(), "Can't Grant user with the reward", Toast.LENGTH_SHORT).show();

            }
        });
        try {

            String current = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            currentVersion = Float.valueOf(current);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new GetVersionCode().execute();
        StartAppAd.disableSplash();
        createInterstitialAd();

        init();
        internet = new Internet(MainActivity.this);

        checkInternetConnection();

        setSupportActionBar(toolbar);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        getDataFromDatabase();
        //  getAdsIds();

        referearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showInterstitalAd(2);
                CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
                customIntent.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                openCustomTab(MainActivity.this, customIntent.build(), Uri.parse("http://971.win.qureka.com/"));


            }
        });


        dailycheckcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (interstitialAd.isReady()) {
                    interstitialAd.showAd();
                }
                dailyCheck();

            }
        });
        taskcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connecttapjoy();

                if (Tapjoy.isConnected()) {
                    Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
                        @Override
                        public void onEarnedCurrency(String currencyName, int amount) {
                            Log.i("Tapjoy", "You've just earned " + amount + " " + currencyName);
                            message("Amount Earned");
                            updateData(amount);
                        }
                    });
                }

            }
        });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitalAd(1);
            }
        });

        logoutmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
        watchcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitalAd(6);
            }
        });
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitalAd(3);

            }
        });

        luckydraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitalAd(4);
            }
        });
        aboutcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitalAd(2);

            }
        });


    }

    private void init() {
        coinearn = findViewById(R.id.coinearn);
        nameearn = findViewById(R.id.namePro);
        emailearn = findViewById(R.id.emailPro);
        dailycheckcard = findViewById(R.id.dailyCheckCard);
        luckydraw = findViewById(R.id.luckySpinCard);
        taskcard = findViewById(R.id.taskCard);
        referearn = findViewById(R.id.referCard);
        aboutcard = findViewById(R.id.aboutCard);
        watchcard = findViewById(R.id.watchcard);
        withdraw = findViewById(R.id.redemCard);
        logoutmain = findViewById(R.id.mainlogoutcard);
        toolbar = findViewById(R.id.toolbar);
        profileimage = findViewById(R.id.proimage);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
    }

    private void getDataFromDatabase() {

        dialog.show();
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileModel model = snapshot.getValue(ProfileModel.class);
                nameearn.setText(model.getName());
                emailearn.setText(model.getEmail());
                coinearn.setText(String.valueOf(model.getCoins()));
                Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .timeout(6000)
                        .placeholder(R.drawable.profile)
                        .into(profileimage);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkInternetConnection() {
        if (internet.isConnected()) {
            new isInternetActive().execute();
        } else {
            Toast.makeText(this, "please check your Internet", Toast.LENGTH_SHORT).show();

        }
    }

    private void dailyCheck() {
        if (internet.isConnected()) {
            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitleText("Please Wait");
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();

            final Date currentDate = Calendar.getInstance().getTime();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            reference.child("Daily Check").child(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String dbDateString = snapshot.child("date").getValue(String.class);
                                try {
                                    assert dbDateString != null;
                                    Date dbDate = dateFormat.parse(dbDateString);
                                    String xDate = dateFormat.format(currentDate);
                                    Date date = dateFormat.parse(xDate);

                                    if (date.after(dbDate) && date.compareTo(dbDate) != 0) {
                                        reference.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                ProfileModel model = snapshot.getValue(ProfileModel.class);
                                                int currentCoin = model.getCoins();
                                                int update = currentCoin + 10;
                                                int spinC = model.getSpins();
                                                int updateSpins = spinC + 2;
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("coins", update);
                                                map.put("spins", updateSpins);
                                                reference.child("Users").child(user.getUid())
                                                        .updateChildren(map);

                                                Date newDate = Calendar.getInstance().getTime();
                                                String newDateString = dateFormat.format(newDate);
                                                HashMap<String, String> dateMap = new HashMap<>();
                                                dateMap.put("date", newDateString);

                                                reference.child("Daily Check").child(user.getUid()).setValue(dateMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                sweetAlertDialog.changeAlertType(sweetAlertDialog.SUCCESS_TYPE);
                                                                sweetAlertDialog.setTitleText("Success");
                                                                sweetAlertDialog.setContentText("Coins added to your Account Successfully");
                                                                sweetAlertDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                        sweetAlertDialog.dismissWithAnimation();
                                                                    }
                                                                }).show();
                                                            }
                                                        });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                                Toast.makeText(MainActivity.this, "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        sweetAlertDialog.changeAlertType(sweetAlertDialog.ERROR_TYPE);
                                        sweetAlertDialog.setTitleText("Failed");
                                        sweetAlertDialog.setContentText("You have already rewarded,come back tomorrow");
                                        sweetAlertDialog.setConfirmButton("Dismiss", null);
                                        sweetAlertDialog.show();

                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    sweetAlertDialog.dismissWithAnimation();
                                }

                            } else {
                                sweetAlertDialog.changeAlertType(sweetAlertDialog.WARNING_TYPE);
                                sweetAlertDialog.setTitleText("System busy");
                                sweetAlertDialog.setContentText("System busy please try again later");
                                sweetAlertDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();

                                    }
                                });
                                sweetAlertDialog.show();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Error" + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
        } else {
            Toast.makeText(this, "Please Check your Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPollfishSurveyCompleted(SurveyInfo surveyInfo) {

        int reward = surveyInfo.getRewardValue();
        updateData(reward);
    }

    private void updateData(int reward) {

        int currentCoins = Integer.parseInt(coinearn.getText().toString());
        int updateCoins = currentCoins + reward;
        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", updateCoins);
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Coins Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onPollfishOpened() {
        Toast.makeText(this, "Survey Opened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPollfishClosed() {
        Toast.makeText(this, "Survey Closed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPollfishSurveyReceived(SurveyInfo surveyInfo) {
        Toast.makeText(this, "Survey Received", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'

        // Reset retry attempt

        // Toast.makeText(MainActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
        retryAttempt = 0;
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        // Interstitial ad is hidden. Pre-load the next ad
        interstitialAd.loadAd();
        // Toast.makeText(MainActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        // Interstitial ad is hidden. Pre-load the next ad
        interstitialAd.loadAd();
        //   Toast.makeText(MainActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClicked(MaxAd ad) {

        Toast.makeText(MainActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                interstitialAd.loadAd();
            }
        }, delayMillis);
        Toast.makeText(MainActivity.this, "Failed ad Load", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
        interstitialAd.loadAd();

    }


    class isInternetActive extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            InputStream inputStream = null;
            String json = "";

            try {
                String strURL = "https://icons.iconarchive.com/";
                URL url = new URL(strURL);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoOutput(true);

                inputStream = urlConnection.getInputStream();
                json = "success";


            } catch (Exception e) {
                e.printStackTrace();
                json = "failed";
            }
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                if (s.equals("success")) {
                    Toast.makeText(MainActivity.this, "Internet Connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, " No Internet Access", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "Validating Internet", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }
    }


    private void showInterstitalAd(final int i) {

        if (i == 1)  //profile Image
        {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);

        }
        if (i == 2)//refer and Earn
        {
            Intent intent = new Intent(MainActivity.this, InviteActivity.class);
            startActivity(intent);
        }

        if (i == 3)//withdraw
        {
            if (interstitialAd.isReady()) {
                interstitialAd.showAd();
                Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
                intent.putExtra("position", 3);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
                intent.putExtra("position", 3);
                startActivity(intent);
            }
        }
        if (i == 4) //lucky draw
        {
            if (interstitialAd.isReady()) {
                interstitialAd.showAd();
                Intent intent = new Intent(MainActivity.this, FragmentReplacerActivity.class);
                intent.putExtra("position", 2);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, FragmentReplacerActivity.class);
                intent.putExtra("position", 2);
                startActivity(intent);
            }

        }
        if (i == 5) {

            Intent intent = new Intent(MainActivity.this, FragmentReplacerActivity.class);
            intent.putExtra("position", 3);
            startActivity(intent);
        }
        if (i == 6)//watch
        {
            Intent intent = new Intent(MainActivity.this, WatchActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();
        PollFish.ParamsBuilder paramsBuilder = new PollFish.ParamsBuilder(getString(R.string.pollfish_api))
                .requestUUID(user.getUid())
                .releaseMode(true)

                .indicatorPosition(Position.MIDDLE_RIGHT)
                .indicatorPadding(12)
                .build();
        PollFish.initWith(this, paramsBuilder);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Tapjoy.onActivityStart(this);
    }

    //session end
    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
    }


    public void connecttapjoy() {
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        Tapjoy.connect(MainActivity.this, "w9OfZVl9SO-HxA970ZvItgECH9IbeVMo9w3zIn594pJf8Jx1HBApoxCUK-36", connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                message("Successfully Connected to Tapjoy");
            }

            @Override
            public void onConnectFailure() {
                message("Failed to Connect to Tapjoy");
            }
        });
    }

    public void message(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void showForceUpdateDialog(Context context, String latestVersion) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyAlertDialogTheme));
        alertDialogBuilder.setTitle(context.getString(R.string.youAreNotUpdatedTitle));
        alertDialogBuilder.setMessage(context.getString(R.string.youAreNotUpdatedMessage) + " " + latestVersion + " " + context.getString(R.string.youAreNotUpdatedMessage1));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
    }

    void createInterstitialAd() {
        interstitialAd = new MaxInterstitialAd("61d5b03237a19fc7", this);
        interstitialAd.setListener(this);
        // Load the first ad
        interstitialAd.loadAd();
    }


    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {

                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    showForceUpdateDialog(MainActivity.this, onlineVersion);
                }
            }

        }
    }

    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {
        String packageName = "com.android.chrome";
        customTabsIntent.intent.setPackage(packageName);
        customTabsIntent.launchUrl(activity, uri);
    }


}
