package com.example.cz2006.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cz2006.R;

import java.util.Calendar;
import java.util.Date;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        myAlarm();
    }

    public void myAlarm() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
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