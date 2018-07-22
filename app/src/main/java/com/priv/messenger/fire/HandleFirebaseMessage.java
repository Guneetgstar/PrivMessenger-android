package com.priv.messenger.fire;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.priv.messenger.Chat;
import com.priv.messenger.MainActivity;
import com.priv.messenger.R;

public class HandleFirebaseMessage extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size()>0)
            Log.d("FCM Messege",remoteMessage.getData().keySet().toString() );
            Intent intent = new Intent(this, Chat.class);
            intent.putExtra("profile",remoteMessage.getData().get("profile"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent= PendingIntent
                    .getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_CANCEL_CURRENT);
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.img)
                            .setContentTitle("New Message")
                            .setContentText(remoteMessage.getData().get("profile"))
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setSound(defaultSoundUri);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            
    }
}
