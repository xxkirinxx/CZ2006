package com.example.cz2006.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cz2006.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText confirmpassword;
    private Button registerButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (EditText) findViewById(R.id.emailRegister);
        password = (EditText) findViewById(R.id.passwordRegister);
        confirmpassword = (EditText) findViewById(R.id.confirmPasswordRegister);
        registerButton = (Button) findViewById(R.id.registerButton);
        backButton = (Button) findViewById(R.id.backButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = email.getText().toString();
                String pw = password.getText().toString();
                String cpw = confirmpassword.getText().toString();
                Context context = getApplicationContext();

                RegisterActivity.BackgroundTask backgroundTask = new RegisterActivity.BackgroundTask(context);
                backgroundTask.execute(name, pw, cpw);
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


    private class BackgroundTask extends AsyncTask<String, String, String> {
        Context context;

        BackgroundTask(Context ctx) {
            this.context = ctx;
        }

        @Override
        protected String doInBackground(String... strings) {
            String username = strings[0];
            String password = strings[1];
            String cpassword = strings[2];
            String registerUrl = "http://10.0.2.2/AndriodLogin/signup.php";

            try {
                URL url = new URL(registerUrl);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String user_account = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") +
                            "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") +
                            "&" + URLEncoder.encode("cpassword", "UTF-8") + "=" + URLEncoder.encode(cpassword, "UTF-8");
                    bufferedWriter.write(user_account);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String result = "";
                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    result = stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    outputStream.close();
                    httpURLConnection.disconnect();
                    return result;


                } catch (IOException e) {
                    e.printStackTrace();
                    return "IOexception";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "URLexception";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                Toast.makeText(context, "Email already in use", Toast.LENGTH_LONG).show();
            } else if (result.equals("2")) {
                Toast.makeText(context, "Password and Confirm Password are different!", Toast.LENGTH_LONG).show();
            } else if (result.equals("3")) {
                Toast.makeText(context, "Registration Success", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            } else {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        }

    }
}