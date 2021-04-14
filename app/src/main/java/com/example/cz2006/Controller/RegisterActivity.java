package com.example.cz2006.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cz2006.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmpassword;
    private Button registerButton;
    private Button backButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        name = (EditText) findViewById(R.id.nameRegister);
        email = (EditText) findViewById(R.id.emailRegister);
        password = (EditText) findViewById(R.id.passwordRegister);
        confirmpassword = (EditText) findViewById(R.id.confirmPasswordRegister);
        registerButton = (Button) findViewById(R.id.registerButton);
        backButton = (Button) findViewById(R.id.backButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = name.getText().toString();
                String mail = email.getText().toString();
                String pw = password.getText().toString();
                String cpw = confirmpassword.getText().toString();

                if (username.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Name field is empty", Toast.LENGTH_LONG).show();
                }
                else if (mail.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Email field is empty", Toast.LENGTH_LONG).show();
                }
                else if (pw.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Password field is empty", Toast.LENGTH_LONG).show();
                }
                else if (cpw.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Enter your password again to confirm", Toast.LENGTH_LONG).show();
                }
                else if (!pw.equals(cpw)) {
                    Toast.makeText(RegisterActivity.this, "Password and Confirm Password inputs are different", Toast.LENGTH_LONG).show();
                }
                else if (!pwCheck(pw)) {
                    Toast.makeText(RegisterActivity.this, "Password need to contain 12 characters, 1 uppercase, 1 lowercase, 1 numeric and 1 special character!", Toast.LENGTH_LONG).show();
                }
                else {
                    createAccount(username, mail, pw);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
        });
    }

    public boolean pwCheck(String password)
    {
        if(password.length()>=12)
        {
            Pattern upper = Pattern.compile("[a-z]");
            Pattern lower = Pattern.compile("[A-Z]");
            Pattern numeric = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile ("[!@#$%^&]");

            Matcher hasUpper = upper.matcher(password);
            Matcher hasLower = lower.matcher(password);
            Matcher hasNumeric = numeric.matcher(password);
            Matcher hasSpecial = special.matcher(password);

            if (hasUpper.find() && hasLower.find() && hasNumeric.find() && hasSpecial.find())
                return true;
            else
                return false;
        }
        else
            return false;
    }

    private void createAccount(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateProfile(username, user);
                        } 
                      else {
                            Toast.makeText(RegisterActivity.this, "Email May Already Be In Use", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateProfile(String username, FirebaseUser user) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            RegisterActivity.this.finish();
                        }
                    }
                });
    }
}