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
    private Button changepwButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.name);
        emailAddress = findViewById(R.id.email);
        changepwButton = (Button) findViewById(R.id.changepasswordbutton);
        logoutButton = (Button) findViewById(R.id.logoutbutton);

        getUserProfile();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

//        changepwButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
//                startActivity(intent);
//            }
//        });

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
}