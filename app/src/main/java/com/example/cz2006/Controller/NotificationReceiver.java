package com.example.cz2006.Controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.example.cz2006.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationReceiver extends BroadcastReceiver {

    String title = "NozzieMozzie";
    String content = "Have you done your mozzie wipeout today?";
    private static final String NOTIFICATION_CHANNEL_ID = "0001";

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context);
    }

    public void createNotification(Context mContext)
    {

        Intent intent = new Intent(mContext , LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0, mBuilder.build());

        addNotificationToDatabase();
    }

    public void addNotificationToDatabase()
    {
        // Create a new notification
        Map<String, String> notification = new HashMap<>();

        String currentTime = Calendar.getInstance().getTime().toString().substring(0, 16);
        notification.put("time", currentTime);
        notification.put("title", title);
        notification.put("content", content);

        // Add a new document with a generated ID using user's email
        String user = LoginActivity.user.getEmail();
        FirebaseFirestore.getInstance().collection(user).add(notification);
    }
}
