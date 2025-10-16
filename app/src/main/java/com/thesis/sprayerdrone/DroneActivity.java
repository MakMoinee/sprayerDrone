package com.thesis.sprayerdrone;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.MakMoinee.library.common.CustomErrors;
import com.github.MakMoinee.library.dialogs.MyDialog;
import com.github.MakMoinee.library.interfaces.LocalVolleyRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thesis.sprayerdrone.common.MyUtils;
import com.thesis.sprayerdrone.databinding.ActivityDroneBinding;
import com.thesis.sprayerdrone.interfaces.NetworkListener;
import com.thesis.sprayerdrone.models.Drones;
import com.thesis.sprayerdrone.services.DroneExternalService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class DroneActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice pumpBT;
    ActivityDroneBinding binding;
    DroneExternalService externalService;
    Drones selectedDrones;
    boolean isPumpOn = true;
    private float pitch = 1500;   // Default values for pitch, roll, throttle, and yaw
    private float roll = 1500;
    private float throttle = 1500;
    private float yaw = 1500;
    ProgressDialog progressDialog;
    private static final UUID UUID_SERIAL_PORT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String DEVICE_NAME = "HC-06";  // Replace with your module's Bluetooth name
    OutputStream outputStream;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDroneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        externalService = new DroneExternalService(DroneActivity.this);
        progressDialog = new MyDialog(DroneActivity.this);
        selectedDrones = new Gson().fromJson(getIntent().getStringExtra("drones"), new TypeToken<Drones>() {
        }.getType());
        if (selectedDrones != null) {
            setListeners();
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show();
                finish();
            }
            binding.lblTitle.setText(String.format("Your Drone: %s", selectedDrones.getDeviceName()));
        } else {
            Toast.makeText(DroneActivity.this, "Something Wrong Happened With The Selected Drone, Please Try Again Later", Toast.LENGTH_SHORT).show();
        }

    }

    private void setListeners() {
        connectToPump();
        binding.btnDeleteDrone.setOnClickListener(v -> {
            progressDialog.show();

            MyUtils.checkInternet(DroneActivity.this, new NetworkListener() {
                @Override
                public <T> void onSuccess(T any) {

                }
            });
        });
        binding.btnConnect.setOnClickListener(v -> {
            MyUtils.checkInternet(DroneActivity.this, new NetworkListener() {
                @Override
                public <T> void onSuccess(T any) {
                    progressDialog.show();
                    externalService.pingDrone(selectedDrones.getDeviceIP(), new LocalVolleyRequestListener() {
                        @Override
                        public void onSuccessString(String response) {
                            binding.txtDroneStatus.setText("Active");
                            binding.txtDroneStatus.setTextColor(Color.GREEN);
                            binding.btnArm.setEnabled(true);
                            binding.btnDisarm.setEnabled(true);
                            binding.btnTakeOff.setEnabled(true);
                            binding.btnLanding.setEnabled(true);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onError(Error error) {
                            progressDialog.dismiss();
                            binding.txtDroneStatus.setText("Inactive");
                            binding.txtDroneStatus.setTextColor(Color.RED);
                            binding.btnArm.setEnabled(false);
                            binding.btnDisarm.setEnabled(false);
                            binding.btnTakeOff.setEnabled(false);
                            binding.btnLanding.setEnabled(false);
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
                        Log.e("arm_click","true");
                        Log.e("raw_disarm_resp",response);
                    }

                    @Override
                    public void onError(Error error) {
                        CustomErrors.error(error);
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
                        Log.e("disarm_click","true");
                        Log.e("raw_disarm_resp",response);
                    }

                    @Override
                    public void onError(Error error) {
                        CustomErrors.error(error);
                    }
                });
            }
        }));

        binding.btnPump.setOnClickListener(v -> MyUtils.checkInternet(DroneActivity.this, new NetworkListener() {
            @Override
            public <T> void onSuccess(T any) {
                Log.e("pump_triggered", "true");
                if (!isPumpOn) {
                    isPumpOn = true;
                    sendCommand("%A#");
                } else {
                    isPumpOn = false;
                    sendCommand("%B#");
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
                        CustomErrors.error(error);
                    }
                });
            }
        });
    }

    private void connectToPump() {
        progressDialog = ProgressDialog.show(DroneActivity.this, "Connecting", "Please wait...", true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(DroneActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted, request it
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Android 12 and above require runtime permission check for Bluetooth
                        ActivityCompat.requestPermissions(DroneActivity.this,
                                new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                                1);
                    }
                } else {
                }
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (DEVICE_NAME.equals(device.getName())) {
                            pumpBT = device;
                            break;
                        }
                    }
                }

                if (pumpBT != null) {
                    try {
                        bluetoothSocket = pumpBT.createRfcommSocketToServiceRecord(UUID_SERIAL_PORT);
                        bluetoothSocket.connect();
                        outputStream = bluetoothSocket.getOutputStream();
                        Toast.makeText(DroneActivity.this, "Connected to Drone's Pump", Toast.LENGTH_SHORT).show();

                        isPumpOn = false;
                        sendCommand("%B#");
                    } catch (IOException e) {
                        Toast.makeText(DroneActivity.this, "Failed to connect to Drone's Pump", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DroneActivity.this, "Drone's Pump not found. Make sure it is paired.", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
                binding.btnConnect.performClick();
            }
        }, 2000);  // Delay for 2 seconds to simulate connection time
    }

    private void sendCommand(String command) {
        if (outputStream != null) {
            try {
                outputStream.write(command.getBytes());
//                Toast.makeText(this, "Command sent: " + command, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                CustomErrors.error(e);
                Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("bt_not_connected", "true");
            Toast.makeText(this, "Not connected to robot car", Toast.LENGTH_SHORT).show();
        }
    }
}
