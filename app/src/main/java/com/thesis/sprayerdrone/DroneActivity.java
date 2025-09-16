package com.thesis.sprayerdrone;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.MakMoinee.library.interfaces.LocalVolleyRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thesis.sprayerdrone.common.MyUtils;
import com.thesis.sprayerdrone.databinding.ActivityDroneBinding;
import com.thesis.sprayerdrone.interfaces.NetworkListener;
import com.thesis.sprayerdrone.models.Drones;
import com.thesis.sprayerdrone.services.DroneExternalService;

public class DroneActivity extends AppCompatActivity {

    ActivityDroneBinding binding;
    DroneExternalService externalService;
    Drones selectedDrones;
    boolean isPumpOn = true;
    private float pitch = 1500;   // Default values for pitch, roll, throttle, and yaw
    private float roll = 1500;
    private float throttle = 1500;
    private float yaw = 1500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDroneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        externalService = new DroneExternalService(DroneActivity.this);
        selectedDrones = new Gson().fromJson(getIntent().getStringExtra("drones"), new TypeToken<Drones>() {
        }.getType());
        if (selectedDrones != null) {
            setListeners();
            binding.btnPump.performClick();
        } else {
            Toast.makeText(DroneActivity.this, "Something Wrong Happened With The Selected Drone, Please Try Again Later", Toast.LENGTH_SHORT).show();
        }

    }

    private void setListeners() {
        binding.btnConnect.setOnClickListener(v -> {
            MyUtils.checkInternet(DroneActivity.this, new NetworkListener() {
                @Override
                public <T> void onSuccess(T any) {
                    externalService.pingDrone(selectedDrones.getDeviceIP(), new LocalVolleyRequestListener() {
                        @Override
                        public void onSuccessString(String response) {
                            binding.txtDroneStatus.setText("Active");
                            binding.txtDroneStatus.setTextColor(Color.GREEN);
                        }

                        @Override
                        public void onError(Error error) {
                            binding.txtDroneStatus.setText("Inactive");
                            binding.txtDroneStatus.setTextColor(Color.RED);
                            Toast.makeText(DroneActivity.this, "Failed To Connect With The Drone, Please Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        });

        binding.btnArm.setOnClickListener(v -> MyUtils.checkInternet(DroneActivity.this, new NetworkListener() {
            @Override
            public <T> void onSuccess(T any) {
                externalService.arm(selectedDrones.getDeviceIP(), new LocalVolleyRequestListener() {
                    @Override
                    public void onSuccessString(String response) {

                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
            }
        }));

        binding.btnDisarm.setOnClickListener(v -> MyUtils.checkInternet(DroneActivity.this, new NetworkListener() {
            @Override
            public <T> void onSuccess(T any) {
                externalService.disArm(selectedDrones.getDeviceIP(), new LocalVolleyRequestListener() {
                    @Override
                    public void onSuccessString(String response) {

                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
            }
        }));

        binding.btnPump.setOnClickListener(v -> MyUtils.checkInternet(DroneActivity.this, new NetworkListener() {
            @Override
            public <T> void onSuccess(T any) {
                if (!isPumpOn) {
                    externalService.pumpOn(selectedDrones.getDeviceIP(), new LocalVolleyRequestListener() {
                        @Override
                        public void onSuccessString(String response) {
                            isPumpOn = true;
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                } else {
                    externalService.pumpOff(selectedDrones.getDeviceIP(), new LocalVolleyRequestListener() {
                        @Override
                        public void onSuccessString(String response) {
                            isPumpOn = false;
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        }));

        binding.btnLeft.setJoystickListener((x, y) -> {
            pitch = 1500 + x * 500;  // Map X to pitch (left-right)
            roll = 1500 + y * 500;   // Map Y to roll (up-down)
            sendControlCommand();
        });

        binding.btnRight.setJoystickListener((x, y) -> {
            throttle = 1500 + x * 500;     // Map X to yaw (left-right)
            yaw = 1500 + y * 500;  // Map Y to throttle (up-down)
            sendControlCommand();
        });
    }

    private void sendControlCommand() {
        MyUtils.checkInternet(DroneActivity.this, new NetworkListener() {
            @Override
            public <T> void onSuccess(T any) {
                externalService.sendCommand(selectedDrones.getDeviceIP(), pitch, roll, throttle, yaw, new LocalVolleyRequestListener() {
                    @Override
                    public void onSuccessString(String response) {
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
            }
        });
    }
}
