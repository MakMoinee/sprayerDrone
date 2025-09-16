package com.thesis.sprayerdrone.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.thesis.sprayerdrone.interfaces.LogoutListener;

public class LogoutFragment extends Fragment {

    LogoutListener logoutListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());
        DialogInterface.OnClickListener dListener = (d, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE:
                    logoutListener.onLogout();
                    break;
                default:
                    logoutListener.onCancelLogout();
                    d.dismiss();
                    break;
            }
        };
        mBuilder.setMessage("Are You Sure You Want To Logout?")
                .setNegativeButton("Yes", dListener)
                .setPositiveButton("No", dListener)
                .setCancelable(false)
                .show();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof LogoutListener) {
            logoutListener = (LogoutListener) context;
        }
    }
}
