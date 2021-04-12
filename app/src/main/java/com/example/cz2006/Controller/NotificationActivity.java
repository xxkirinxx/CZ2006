package com.example.cz2006.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cz2006.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

public class NotificationActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView notification1;
    TextView notification2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setNotificationAlarm();

        notification1 = (TextView) findViewById(R.id.time);
        notification2 = (TextView) findViewById(R.id.content);

        getNotification();

    }

    public void setNotificationAlarm() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }
    }

    public void getNotification()
    {
        String user = LoginActivity.user.getEmail();
        db.collection(user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                notification1.append(document.getData().get("time").toString() + "\n\n");
                                notification2.append(document.getData().get("content").toString() + "\n\n");
                            }
                        }
                    }
                });
    }

    public void launchHome(View v) {
        Intent i = new Intent(this, com.example.cz2006.Controller.MainActivity.class);
        startActivity(i);
        this.finish();
    }

    public void launchProfile(View v) {
        Intent i = new Intent(this, com.example.cz2006.Controller.ProfileActivity.class);
        startActivity(i);
        this.finish();
    }
}