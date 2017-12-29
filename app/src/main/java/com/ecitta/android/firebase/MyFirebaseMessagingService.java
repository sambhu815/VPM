package com.ecitta.android.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.ecitta.android.R;
import com.ecitta.android.vpm.Splash_Activity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Created by Swapnil.Patel on 23-05-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Random random = new Random();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("Msg", "Message received [" + remoteMessage + "]");

        // Create Notification
        Intent intent = new Intent(this, Splash_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = (android.support.v7.app.NotificationCompat.Builder) new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_icon)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                .setContentText(remoteMessage.getNotification().getBody())
                .setSound(uri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        notification.defaults = Notification.DEFAULT_VIBRATE;
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.contentIntent = pendingIntent;

        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
        int m = random.nextInt(9999 - 1000) + 1000;
        //1401
        notificationManager.notify(m, notificationBuilder.build());
    }
}
