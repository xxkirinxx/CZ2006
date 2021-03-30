package com.example.cz2006.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button resetButton;
    private Button backButton;

    private String emailaddress;
    private String newpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = (EditText) findViewById(R.id.emailReset);
        resetButton = (Button) findViewById(R.id.resetButton);
        backButton = (Button) findViewById(R.id.backButton);

//        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailaddress = email.getText().toString();
                newpassword = generateRandomPW();

                ForgetPasswordActivity.BackgroundTask backgroundTask = new ForgetPasswordActivity.BackgroundTask(getApplicationContext());
                backgroundTask.execute(emailaddress, newpassword);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                ForgetPasswordActivity.this.finish();
            }
        });
    }

    public String generateRandomPW() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 8;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    public void sendNewPasswordEmail() {
        String MozzieEmail = "nozziemozzie.system@gmail.com"; //an actual account i created to send email from
        String MozzieEmailPassword = "nozzie.mozzie"; //account password

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MozzieEmail, MozzieEmailPassword);
                    }
                });


        try {
            String receiver = emailaddress;
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MozzieEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            message.setSubject("NozzieMozzie Account Password Reset");
            message.setText("Dear User,\n\n"
                    + "You have opted to reset your password and it has successfully been changed.\n"
                    + "Your new password is\n\n"
                    + newpassword + "\n\n"
                    + "We recommend that you log in immediately and change your password to secure your account.\n\n"
                    + "Regards,\n"
                    + "NozzieMozzie System\n");

            Transport.send(message);
        } catch (MessagingException e) {e.printStackTrace();}
    }


    private class BackgroundTask extends AsyncTask<String, String, String> {
        Context context;
        BackgroundTask(Context ctx){
            this.context=ctx;
        }

        @Override
        protected String doInBackground(String... strings) {
            String email = strings[0];
            String newpassword = strings[1];
            String forgetpwUrl = "http://10.0.2.2/AndriodLogin/forgetpassword.php";

            try {
                URL url = new URL(forgetpwUrl);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String user_account = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") +
                            "&" + URLEncoder.encode("newpassword", "UTF-8") + "=" + URLEncoder.encode(newpassword, "UTF-8");
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
            if (result.equals("1")){
                Toast.makeText(context, "Email Does Not Have An Account", Toast.LENGTH_LONG).show();
            }
            else if (result.equals("2")){
                sendNewPasswordEmail();
                Toast.makeText(context, "New Password Has Been Sent To Your Email", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        }
    }
}