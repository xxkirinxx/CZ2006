package com.example.cz2006.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cz2006.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    private boolean mLocationPermissionGranted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void launchReport(View v){
        Intent i = new Intent(this, com.example.cz2006.Controller.ReportActivity.class);
        startActivity(i);

    }

    public void launchSymptom(View v){
        Intent i = new Intent(this, com.example.cz2006.Controller.SymptomActivity.class);
        startActivity(i);
        this.finish();
    }

    public void launchLocation(View v){
        Intent i = new Intent(this, com.example.cz2006.Controller.LocationActivity.class);
        startActivity(i);

    }

    public void launchMozzie(View v){
        Intent i = new Intent(this, com.example.cz2006.Controller.MozzieActivity.class);
        startActivity(i);

    }

    public void launchSetting(View v){
        Intent i = new Intent(this, com.example.cz2006.Controller.SettingsActivity.class);
        startActivity(i);

    }

}