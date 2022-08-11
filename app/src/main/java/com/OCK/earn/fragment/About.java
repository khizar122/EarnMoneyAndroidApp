package com.OCK.earn.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.OCK.earn.BuildConfig;
import com.OCK.earn.R;


public class About extends Fragment {

private TextView versionET,ctTelegram;

    public About() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        versionET=view.findViewById(R.id.version);
        ctTelegram=view.findViewById(R.id.ctTelegram);
        String versionName= BuildConfig.VERSION_NAME;
        int versionCode=BuildConfig.VERSION_CODE;
        String version="Version" +versionName+"."+versionCode;
        versionET.setText(version);
        ctTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://chat.whatsapp.com/FUHpB9zwskT9yW2B4NdaSg");
            }
        });
    }
    private void gotoUrl(String s)
    {
        Uri uri=Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}