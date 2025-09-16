package com.thesis.sprayerdrone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.MakMoinee.library.preference.LoginPref;
import com.github.MakMoinee.library.services.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.thesis.sprayerdrone.databinding.ActivityDashboardBinding;
import com.thesis.sprayerdrone.interfaces.LogoutListener;

public class DashboardActivity extends AppCompatActivity implements LogoutListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDashboardBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarDashboard.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        String username = new LoginPref(DashboardActivity.this).getStringItem("username");
        String firstName = new LoginPref(DashboardActivity.this).getStringItem("firstName");
        String middleName = new LoginPref(DashboardActivity.this).getStringItem("middleName");
        String lastName = new LoginPref(DashboardActivity.this).getStringItem("lastName");
        TextView txtFullName = Utils.getNavView(navigationView).findViewById(R.id.txtFullName);
        TextView txtUsername = Utils.getNavView(navigationView).findViewById(R.id.txtUsername);
        txtUsername.setText(String.format("@%s", username));
        txtFullName.setText(String.format("%s %s %s", firstName, middleName, lastName));
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_drones, R.id.nav_settings, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        Utils.setUpNavigation(this, navigationView, navController, mAppBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onLogout() {
        new LoginPref(DashboardActivity.this).clearLogin();
        Toast.makeText(DashboardActivity.this, "Successfully Logout", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onCancelLogout() {
        navController.navigate(R.id.nav_home);
    }

    @Override
    public void openDrones() {
        navController.navigate(R.id.nav_drones);
    }

    @Override
    public void openSettings() {
        navController.navigate(R.id.nav_settings);
    }
}