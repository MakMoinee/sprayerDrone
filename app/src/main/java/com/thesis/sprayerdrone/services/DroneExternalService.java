package com.thesis.sprayerdrone.services;

import android.content.Context;

import com.github.MakMoinee.library.interfaces.LocalVolleyRequestListener;
import com.github.MakMoinee.library.models.LocalVolleyRequestBody;
import com.github.MakMoinee.library.services.LocalVolleyRequest;

public class DroneExternalService extends LocalVolleyRequest {
    public DroneExternalService(Context mContext) {
        super(mContext);
    }

    public void pingDrone(LocalVolleyRequestBody body, LocalVolleyRequestListener listener) {
        this.sendTextPlainRequest(body, listener);
    }
}
