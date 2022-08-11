package com.OCK.earn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Internet {
    Context context;

    public Internet(Context context) {
        this.context = context;
    }
    public boolean isConnected()
    {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return info != null && info.isConnected();


    }
}
