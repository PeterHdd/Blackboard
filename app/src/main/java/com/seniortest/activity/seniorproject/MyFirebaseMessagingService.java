package com.seniortest.activity.seniorproject;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by peter on 11/19/17.
   This is required if you want to do any message handling beyond receiving notifications on apps in the background.
  To receive notifications in foregrounded apps
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServce";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationTitle = null, notificationBody = null;
        String dataTitle=null;
        String dataMessage=null;
        String datateach=null;
        String datacoursename=null;
        String databody=null;



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            dataTitle=remoteMessage.getData().get("titles");
            dataMessage = remoteMessage.getData().get("bodys");
            datacoursename=remoteMessage.getData().get("coursename");
            databody=remoteMessage.getData().get("body");
            datateach=remoteMessage.getData().get("teachname");
            Log.d(TAG, "Message data payloadssssss: " + dataTitle);

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        if(dataTitle!=null) {
            sendNotification(dataTitle, dataMessage, datateach);
        }

        if(datacoursename!=null) {
            sendNotification(datacoursename, databody);
        }

    }
    private void sendNotification(String datacoursename,String databody){
        Intent intent = new Intent(this, SplashActivity.class); //activity to go after clicking it
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_ONE_SHOT); //pending intent wraps another intent object,
        // then it can be passed to a foriegn app where you grant that app the right to perform the action. here it gets the activity to go to
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigTextStyle bigStyle =
                new NotificationCompat.BigTextStyle();
        bigStyle.bigText(databody);
        bigStyle.setSummaryText("From: "+ datacoursename);
        Log.d(TAG, "Message data payloadasssssssss: " + databody);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)

                .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle("New Assignment")
                .setContentText(databody)
                .setStyle(bigStyle)
                .setTicker("You have received new notification from AUL Announcement")
                .setSound(defaultSoundUri);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        notificationBuilder.setLargeIcon(largeIcon);



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); //used to recieve background notification
        int num = (int) System.currentTimeMillis();
        notificationManager.notify(num, notificationBuilder.build()); //to send a notification, num is the id of the notification

    }


    private void sendNotification(String dataTitle, String dataMessage,String datateach) {
        Intent intent = new Intent(this, SplashActivity.class); //activity to go after clicking it
        intent.putExtra("title", dataTitle);
        intent.putExtra("message", dataMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT); //pending intent wraps another intent object,
        // then it can be passed to a foriegn app where you grant that app the right to perform the action. here it gets the activity
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigTextStyle bigStyle =
                new NotificationCompat.BigTextStyle();
        bigStyle.bigText(dataMessage);
        bigStyle.setSummaryText("From: "+ datateach + " \uD83D\uDE00");
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)

                .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(dataTitle)
                .setContentText(dataMessage)
                .setStyle(bigStyle)
                .setTicker("You have received new notification from AUL Announcement")
                .setSound(defaultSoundUri)
                .setAutoCancel(true);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        notificationBuilder.setLargeIcon(largeIcon);
        Log.d(TAG, "Message data payload: " + dataTitle);



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); //used to recieve background notification
        int num = (int) System.currentTimeMillis();
        notificationManager.notify(num, notificationBuilder.build()); //to send a notification, num is the id of the notification

    }


}