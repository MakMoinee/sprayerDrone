package com.thesis.sprayerdrone.ui.drones;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.github.MakMoinee.library.interfaces.LocalVolleyRequestListener;
import com.github.MakMoinee.library.preference.LoginPref;
import com.github.MakMoinee.library.services.Utils;
import com.google.gson.Gson;
import com.thesis.sprayerdrone.DroneActivity;
import com.thesis.sprayerdrone.adapters.NavDronesAdapter;
import com.thesis.sprayerdrone.common.MyUtils;
import com.thesis.sprayerdrone.databinding.DialogAddDroneBinding;
import com.thesis.sprayerdrone.databinding.FragmentDronesBinding;
import com.thesis.sprayerdrone.interfaces.DronesListener;
import com.thesis.sprayerdrone.interfaces.LogoutListener;
import com.thesis.sprayerdrone.interfaces.NetworkListener;
import com.thesis.sprayerdrone.models.Drones;
import com.thesis.sprayerdrone.services.DroneExternalService;
import com.thesis.sprayerdrone.services.DronesService;

import java.util.ArrayList;
import java.util.List;

public class DroneFragment extends Fragment {

    FragmentDronesBinding binding;
    NavDronesAdapter adapter;
    List<Drones> dronesList = new ArrayList<>();
    DronesService dronesService;
    DroneExternalService externalService;
    int userID = 0;
    AlertDialog addDroneDialog;
    DialogAddDroneBinding dialogAddDroneBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDronesBinding.inflate(LayoutInflater.from(requireContext()), null, false);
        dronesService = new DronesService(requireContext());
        userID = new LoginPref(requireContext()).getIntItem("id");
        externalService = new DroneExternalService(requireContext());
        setListeners();
        loadAllDrones();
        return binding.getRoot();
    }

    private void loadAllDrones() {
        dronesList = new ArrayList<>();
        binding.recycler.setAdapter(null);
        dronesService.getAllDronesByUserID(userID, new DefaultBaseListener() {
            @Override
            public <T> void onSuccess(T any) {
                binding.myRefresher.setRefreshing(false);
                if (any instanceof List<?>) {
                    List<?> tmpList = (List<?>) any;
                    List<Drones> dList = (List<Drones>) tmpList;
                    if (dList != null) {
                        dronesList = dList;
                        adapter = new NavDronesAdapter(requireContext(), dronesList, new DronesListener() {
                            @Override
                            public void onClickListener() {

                            }

                            @Override
                            public void onDeleteListener(Drones sDrone) {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());
                                DialogInterface.OnClickListener dListener = (d, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            dronesService.deleteDrone(sDrone, new DefaultBaseListener() {
                                                @Override
                                                public <T> void onSuccess(T any) {
                                                    Toast.makeText(requireContext(), "Successfully Deleted Drone", Toast.LENGTH_SHORT).show();
                                                    loadAllDrones();
                                                }

                                                @Override
                                                public void onError(Error error) {
                                                    Toast.makeText(requireContext(), "Failed To Delete Drone, Please Try Again Later", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            break;
                                        default:
                                            d.dismiss();
                                            break;
                                    }
                                };
                                mBuilder.setMessage("Are You Sure You Want To Delete This Drone?")
                                        .setPositiveButton("No", dListener)
                                        .setNegativeButton("Yes", dListener)
                                        .setCancelable(false)
                                        .show();
                            }

                            @Override
                            public void onClickListener(int position) {
                                Drones focusedDrone = dronesList.get(position);
                                Intent intent = new Intent(requireContext(), DroneActivity.class);
                                intent.putExtra("drones", new Gson().toJson(focusedDrone));
                                requireContext().startActivity(intent);
                            }
                        });
                        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
                        binding.recycler.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onError(Error error) {
                Log.e("empty_data", error.getLocalizedMessage());

            }
        });
    }

    private void setListeners() {
        binding.myRefresher.setOnRefreshListener(() -> {
            loadAllDrones();
            binding.myRefresher.setRefreshing(false);
        });
        binding.btnSearch.setOnClickListener(v -> {
            String search = binding.editSearch.getText().toString().trim();
            if (search.equals("")) {
                Toast.makeText(requireContext(), "Please Don't Leave Search Field Empty", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    loadAllDrones();
                } catch (Exception e) {

                } finally {
                    if (dronesList.size() > 0) {
//                        TODO: implement drone list filter
                    }
                }
            }
        });

        binding.btnAddDrones.setOnClickListener(v -> MyUtils.checkInternet(requireContext(), new NetworkListener() {
            @Override
            public <T> void onSuccess(T any) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());
                dialogAddDroneBinding = DialogAddDroneBinding.inflate(LayoutInflater.from(requireContext()), null, false);
                mBuilder.setView(dialogAddDroneBinding.getRoot());
                setDialogAddDroneListeners();
                addDroneDialog = mBuilder.create();
                addDroneDialog.setCancelable(false);
                addDroneDialog.show();
            }
        }));
    }

    private void setDialogAddDroneListeners() {
        dialogAddDroneBinding.btnPing.setOnClickListener(v -> {

            String ip = dialogAddDroneBinding.editDroneIP.getText().toString().trim();
            String droneName = dialogAddDroneBinding.editDroneName.getText().toString().trim();

            if (!ip.equals("")) {
                externalService.pingDrone(ip, new LocalVolleyRequestListener() {
                    @Override
                    public void onSuccessString(String response) {
                        Toast.makeText(requireContext(), "Successfully Pinged Drone, you can proceed to saving the drone", Toast.LENGTH_SHORT).show();
                        dialogAddDroneBinding.btnSaveDrone.setEnabled(true);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(requireContext(), "Can't connect to the specified ip drone, please check network and try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialogAddDroneBinding.btnSaveDrone.setOnClickListener(v -> {
            String ip = dialogAddDroneBinding.editDroneIP.getText().toString().trim();
            String droneName = dialogAddDroneBinding.editDroneName.getText().toString().trim();

            if (ip.equals("") || droneName.equals("")) {
                Toast.makeText(requireContext(), "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                Drones newDrone = new Drones.DroneBuilder()
                        .setUserID(userID)
                        .setDeviceName(droneName)
                        .setDeviceIP(ip)
                        .setStatus("active")
                        .setRegisteredDate(Utils.getCurrentDate("yyyy-MM-dd"))
                        .build();

                dronesService.insertDrone(newDrone, new DefaultBaseListener() {
                    @Override
                    public <T> void onSuccess(T any) {
                        Toast.makeText(requireContext(), "Successfully Added Drone", Toast.LENGTH_SHORT).show();
                        addDroneDialog.dismiss();
                        loadAllDrones();
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(requireContext(), "Failed to add drone, please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialogAddDroneBinding.btnCancel.setOnClickListener(v -> {
            addDroneDialog.dismiss();
            addDroneDialog = null;
        });
    }


}
