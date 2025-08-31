package com.thesis.sprayerdrone.common;

import android.content.Context;
import android.widget.Toast;

import com.github.MakMoinee.library.services.Utils;
import com.thesis.sprayerdrone.DroneActivity;
import com.thesis.sprayerdrone.interfaces.NetworkListener;

public class MyUtils {

    public static void checkInternet(Context mContext, NetworkListener listener) {
        boolean isConnected = Utils.isInternetAvailable(mContext);
        if (isConnected) {
            listener.onSuccess("has network");
        } else {
            listener.onError(new Error("no network"));
            Toast.makeText(mContext, "Please make sure you are connected a WIFI/Internet", Toast.LENGTH_SHORT).show();
        }
    }
}
