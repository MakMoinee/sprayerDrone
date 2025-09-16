package com.thesis.sprayerdrone.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.github.MakMoinee.library.preference.LoginPref;
import com.thesis.sprayerdrone.databinding.FragmentSettingsBinding;
import com.thesis.sprayerdrone.interfaces.LogoutListener;
import com.thesis.sprayerdrone.services.UserService;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;
    UserService userService;
    LogoutListener logoutListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(LayoutInflater.from(requireContext()), container, false);
        userService = new UserService(requireContext());
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.cardDelete.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());
            DialogInterface.OnClickListener dListener = (dd, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        int id = new LoginPref(requireContext()).getIntItem("id");
                        if (id != 0) {
                            userService.deleteUserByID(id, new DefaultBaseListener() {
                                @Override
                                public <T> void onSuccess(T any) {
                                    Toast.makeText(requireContext(), "Successfully Deleted Account", Toast.LENGTH_SHORT).show();
                                    dd.dismiss();
                                    logoutListener.onLogout();
                                }

                                @Override
                                public void onError(Error error) {
                                    Toast.makeText(requireContext(), "Failed To Delete Account, Please Try Again Later", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    default:
                        dd.dismiss();
                        break;
                }
            };

            mBuilder.setMessage("Are You Sure You Want To Delete This Account?")
                    .setNegativeButton("Yes", dListener)
                    .setPositiveButton("No", dListener)
                    .setCancelable(false)
                    .show();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LogoutListener) {
            logoutListener = (LogoutListener) context;
        }
    }
}
