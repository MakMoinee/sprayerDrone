package com.thesis.sprayerdrone.ui.drones;

import android.content.DialogInterface;
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
import com.github.MakMoinee.library.preference.LoginPref;
import com.thesis.sprayerdrone.adapters.NavDronesAdapter;
import com.thesis.sprayerdrone.databinding.FragmentDronesBinding;
import com.thesis.sprayerdrone.interfaces.DronesListener;
import com.thesis.sprayerdrone.models.Drones;
import com.thesis.sprayerdrone.services.DronesService;

import java.util.ArrayList;
import java.util.List;

public class DroneFragment extends Fragment {

    FragmentDronesBinding binding;
    NavDronesAdapter adapter;
    List<Drones> dronesList = new ArrayList<>();
    DronesService dronesService;
    int userID = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDronesBinding.inflate(LayoutInflater.from(requireContext()), container, false);
        dronesService = new DronesService(requireContext());
        userID = new LoginPref(requireContext()).getIntItem("id");
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
                if (any instanceof List<?>) {
                    List<?> tmpList = (List<?>) any;
                    List<Drones> dList = (List<Drones>) tmpList;
                    if (dList != null) {
                        adapter = new NavDronesAdapter(requireContext(), dronesList, new DronesListener() {
                            @Override
                            public void onClickListener() {

                            }

                            @Override
                            public void onDeleteListener(Drones drones) {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());
                                DialogInterface.OnClickListener dListener = (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_NEGATIVE:
//                                                    TODO: implement delete
                                            break;
                                        default:
                                            dialog.dismiss();
                                            break;
                                    }
                                };
                                mBuilder.setMessage("Are You Sure You Want To Delete This Drone?")
                                        .setPositiveButton("No", dListener)
                                        .setNegativeButton("Yes", dListener)
                                        .setCancelable(false)
                                        .show();
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
        binding.myRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAllDrones();
            }
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
    }
}
