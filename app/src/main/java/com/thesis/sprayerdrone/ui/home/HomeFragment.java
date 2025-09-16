package com.thesis.sprayerdrone.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.thesis.sprayerdrone.databinding.FragmentHomeBinding;
import com.thesis.sprayerdrone.interfaces.LogoutListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    LogoutListener logoutListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setListeners();
        return root;
    }

    private void setListeners() {

        binding.cardDrones.setOnClickListener(v -> logoutListener.openDrones());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof LogoutListener) {
            logoutListener = (LogoutListener) context;
        }
    }
}