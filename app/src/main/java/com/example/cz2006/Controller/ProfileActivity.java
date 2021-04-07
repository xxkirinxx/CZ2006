package com.example.cz2006.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cz2006.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView username;
    private TextView emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.name);
        emailAddress = findViewById(R.id.email);

        getUserProfile();
    }

    public void getUserProfile() {
        FirebaseUser userprofile = LoginActivity.user;
        if (userprofile != null) {
            String name = userprofile.getDisplayName();
            String email = userprofile.getEmail();

            username.setText(name);
            emailAddress.setText(email);
        }
    }

    public void launchLogout(View v) {
        Intent i = new Intent(this, com.example.cz2006.Controller.LoginActivity.class);
        startActivity(i);
        this.finish();
    }

    public void launchChangePW(View v) {
//        Intent i = new Intent(this, com.example.cz2006.Controller.ChangePasswordActivity.class);
//        startActivity(i);
    }

    public void launchHome(View v) {
        Intent i = new Intent(this, com.example.cz2006.Controller.MainActivity.class);
        startActivity(i);
        this.finish();
    }

    public void launchNotification(View v) {
//        Intent i = new Intent(this, com.example.cz2006.Controller.NotificationActivity.class);
//        startActivity(i);
    }

}