package com.thesis.sprayerdrone.services;

import android.content.Context;

import com.github.MakMoinee.library.interfaces.LocalVolleyRequestListener;
import com.github.MakMoinee.library.models.LocalVolleyRequestBody;
import com.github.MakMoinee.library.services.LocalVolleyRequest;

public class DroneExternalService extends LocalVolleyRequest {
    public DroneExternalService(Context mContext) {
        super(mContext);
    }

    public void pingDrone(String ip, LocalVolleyRequestListener listener) {
        LocalVolleyRequestBody body = new LocalVolleyRequestBody.LocalVolleyRequestBodyBuilder()
                .setUrl(String.format("http://%s/ping", ip))
                .build();
        this.sendTextPlainRequest(body, listener);
    }

    public void arm(String ip, LocalVolleyRequestListener listener) {
        LocalVolleyRequestBody body = new LocalVolleyRequestBody.LocalVolleyRequestBodyBuilder()
                .setUrl(String.format("http://%s/arm", ip))
                .build();
        this.sendTextPlainRequest(body, listener);
    }

    public void disArm(String ip, LocalVolleyRequestListener listener) {
        LocalVolleyRequestBody body = new LocalVolleyRequestBody.LocalVolleyRequestBodyBuilder()
                .setUrl(String.format("http://%s/disarm", ip))
                .build();
        this.sendTextPlainRequest(body, listener);
    }

    public void pumpOn(String ip, LocalVolleyRequestListener listener) {
        LocalVolleyRequestBody body = new LocalVolleyRequestBody.LocalVolleyRequestBodyBuilder()
                .setUrl(String.format("http://%s/pump_on", ip))
                .build();
        this.sendTextPlainRequest(body, listener);
    }

    public void pumpOff(String ip, LocalVolleyRequestListener listener) {
        LocalVolleyRequestBody body = new LocalVolleyRequestBody.LocalVolleyRequestBodyBuilder()
                .setUrl(String.format("http://%s/pump_off", ip))
                .build();
        this.sendTextPlainRequest(body, listener);
    }
}
