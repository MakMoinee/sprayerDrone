package com.thesis.sprayerdrone.services;

import android.content.Context;
import android.util.Log;

import com.github.MakMoinee.library.interfaces.LocalVolleyRequestListener;
import com.github.MakMoinee.library.models.LocalVolleyRequestBody;
import com.github.MakMoinee.library.services.LocalVolleyRequest;

public class DroneExternalService extends LocalVolleyRequest {
    private final String controlString = "http://%s/control?pitch=%.0f&roll=%.0f&throttle=%.0f&yaw=%.0f";

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

    public void sendCommand(String ip, float pitch, float roll, float throttle, float yaw, LocalVolleyRequestListener listener) {
        Log.e("command:", String.format("pitch=%.2f,roll=%.2f,throttle=%.2f,yaw=%.2f", pitch, roll, throttle, yaw));
        String formattedUrl = String.format(controlString, ip, pitch, roll, throttle, yaw);
        LocalVolleyRequestBody body = new LocalVolleyRequestBody.LocalVolleyRequestBodyBuilder()
                .setUrl(formattedUrl)
                .build();
        this.sendTextPlainRequest(body, listener);
    }
}
