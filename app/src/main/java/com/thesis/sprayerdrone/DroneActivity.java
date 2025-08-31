package com.thesis.sprayerdrone;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.MakMoinee.library.services.Utils;
import com.thesis.sprayerdrone.databinding.ActivityDroneBinding;
import com.thesis.sprayerdrone.services.DroneExternalService;

public class DroneActivity extends AppCompatActivity {

    ActivityDroneBinding binding;
    DroneExternalService externalService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDroneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        externalService = new DroneExternalService(DroneActivity.this);

        setListeners();
    }

    private void setListeners() {
        binding.btnConnect.setOnClickListener(v -> {
            boolean isConnected = Utils.isInternetAvailable(DroneActivity.this);
            if (isConnected) {
//                externalService.pingDrone();
            } else {
                Toast.makeText(DroneActivity.this, "Please make sure you are connected a WIFI/Internet", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
