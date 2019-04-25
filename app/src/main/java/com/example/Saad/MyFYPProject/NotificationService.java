package com.example.Saad.MyFYPProject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("FireBaseMessage", "Message: " + remoteMessage.getData());

        if (remoteMessage.getData().size() > 0) {
            //  Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            //String title = remoteMessage.getData().get("title");
            if(remoteMessage.getData().get("type").equals("Booking")) {
                sendBookingNotification(remoteMessage);
            } else{
                SendDeliveredNotification(remoteMessage);
            }
        }

    }  // [END receive_message]

    private void SendDeliveredNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(getApplicationContext(),  DeliveredActivity.class);
        intent.putExtra("Products",remoteMessage.getData().get("Products"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Confirmation")
                        .setContentText("Confirm")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // JSON.noti_count++;
        if (notificationManager != null) {
            notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
        }else{
            Toast.makeText(this, "Notify Manager is empty.", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendBookingNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, BookingNotification.class);
        intent.putExtra("OrderId",remoteMessage.getData().get("OrderId"));

        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.purchase_order)
                        .setContentTitle("Booking")
                        .setContentText("You book products")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // JSON.noti_count++;
        if (notificationManager != null) {
            notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
        }else{
            Toast.makeText(this, "Notify Manager is empty", Toast.LENGTH_SHORT).show();
        }

    }


}