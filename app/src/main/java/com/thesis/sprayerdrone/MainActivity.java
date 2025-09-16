package com.thesis.sprayerdrone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.github.MakMoinee.library.common.MapForm;
import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.github.MakMoinee.library.preference.LoginPref;
import com.github.MakMoinee.library.services.HashPass;
import com.thesis.sprayerdrone.databinding.ActivityMainBinding;
import com.thesis.sprayerdrone.models.Users;
import com.thesis.sprayerdrone.services.UserService;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    HashPass hashPass = new HashPass();
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        userService = new UserService(MainActivity.this);
        int id = new LoginPref(MainActivity.this).getIntItem("id");
        if (id != 0) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }
        setListeners();
    }

    private void setListeners() {
        binding.btnLogin.setOnClickListener(view -> {
            String username = binding.editUsername.getText().toString();
            String password = binding.editPassword.getText().toString();
            if (username.equals("") || password.equals("")) {
                Toast.makeText(MainActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                Users users = new Users.UserBuilder()
                        .setUsername(username)
                        .setPassword(password)
                        .build();
                userService.login(users, new DefaultBaseListener() {
                    @Override
                    public <T> void onSuccess(T any) {
                        if (any instanceof Users) {
                            Users verifiedUser = (Users) any;
                            if (verifiedUser != null) {
                                new LoginPref(MainActivity.this).storeLogin(MapForm.convertObjectToMap(verifiedUser));
                                Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        if (error != null && error.getLocalizedMessage() != null) {
                            Log.e("error_login", error.getLocalizedMessage());
                        }
                        Toast.makeText(MainActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.lblCreateAccount.setOnClickListener(v -> {
            binding.editUsername.setText("");
            binding.editPassword.setText("");
            Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });
    }
}